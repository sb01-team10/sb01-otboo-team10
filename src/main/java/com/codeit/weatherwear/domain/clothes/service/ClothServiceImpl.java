package com.codeit.weatherwear.domain.clothes.service;

import com.codeit.weatherwear.domain.clothes.dto.request.ClothesAttributeDto;
import com.codeit.weatherwear.domain.clothes.dto.request.ClothesCreateRequest;
import com.codeit.weatherwear.domain.clothes.dto.request.ClothesSearchRequest;
import com.codeit.weatherwear.domain.clothes.dto.request.ClothesUpdateRequest;
import com.codeit.weatherwear.domain.clothes.dto.response.ClothesDto;
import com.codeit.weatherwear.domain.clothes.entity.Attribute;
import com.codeit.weatherwear.domain.clothes.entity.Cloth;
import com.codeit.weatherwear.domain.clothes.entity.ClothType;
import com.codeit.weatherwear.domain.clothes.entity.ClothWithAttributes;
import com.codeit.weatherwear.domain.clothes.exception.attribute.AttributeNotFoundException;
import com.codeit.weatherwear.domain.clothes.exception.cloth.ClothNameDuplicatedException;
import com.codeit.weatherwear.domain.clothes.exception.cloth.ClothNotFoundException;
import com.codeit.weatherwear.domain.clothes.exception.cloth.ExtractionNotFoundException;
import com.codeit.weatherwear.domain.clothes.exception.cloth.ExtractionTimeOutException;
import com.codeit.weatherwear.domain.clothes.exception.attribute.InvalidAttributeValueException;
import com.codeit.weatherwear.domain.clothes.exception.cloth.NotSupportSiteException;
import com.codeit.weatherwear.domain.clothes.mapper.ClothMapper;
import com.codeit.weatherwear.domain.clothes.repository.AttributeRepository;
import com.codeit.weatherwear.domain.clothes.repository.ClothRepository;
import com.codeit.weatherwear.domain.clothes.service.parser.SiteParser;
import com.codeit.weatherwear.domain.recommendation.service.AIRecommendationService;
import com.codeit.weatherwear.domain.user.entity.User;
import com.codeit.weatherwear.domain.user.exception.UserNotFoundException;
import com.codeit.weatherwear.domain.user.repository.UserRepository;
import com.codeit.weatherwear.global.exception.s3.S3DeleteException;
import com.codeit.weatherwear.global.request.SortDirection;
import com.codeit.weatherwear.global.response.PageResponse;
import com.codeit.weatherwear.global.storage.ThumbnailImageStorage;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ClothServiceImpl implements ClothService {

  private final ClothRepository clothRepository;
  private final AttributeRepository attributeRepository;
  private final UserRepository userRepository;
  private final ThumbnailImageStorage thumbnailImageStorage;
  private final ClothMapper clothMapper;
  private final List<SiteParser> siteParsers;
  private final AIRecommendationService aiRecommendationService;

  /**
   * 의상 등록
   *
   * @param request 의상 생성 요청 DTO
   * @return 의상 DTO
   */
  @Override
  public ClothesDto create(ClothesCreateRequest request, MultipartFile image) {
    log.info("[Start Creating Cloth] Cloth Name: {}, Cloth Type: {}", request.name(),
        request.type());
    //사용자 찾기
    User user = userRepository.findById(request.ownerId())
        .orElseThrow(() -> {
          log.warn("[Fail Creating Cloth] User Not Found - OwnerId: {}", request.ownerId());
          return new UserNotFoundException();
        });

    //이름 중복 방지
    if (clothRepository.existsByName(request.name())) {
      throw new ClothNameDuplicatedException(request.name());
    }

    // 썸네일 S3 업로드
    log.debug("[Start Uploading Thumbnail Image]");
    String thumbnailKey = (image != null && !image.isEmpty())
        ? thumbnailImageStorage.upload(image)
        : null;
    log.info("[Uploading Profile Image On S3 Completed] Key: {}", thumbnailKey);

    Cloth cloth = Cloth.builder()
        .name(request.name())
        .clothType(request.type())
        .clothesImageUrl(thumbnailKey)
        .user(user)
        .build();

    List<UUID> attributesIds = request.attributes().stream()
        .map(ClothesAttributeDto::definitionId).toList();

    //속성 찾기
    List<Attribute> attributesList = attributeRepository.findAllById(attributesIds);

    //의상에 속성 적용
    Map<UUID, Attribute> attrMap = attributesList.stream()
        .collect(Collectors.toMap(Attribute::getId, Function.identity()));

    applyAttributesToCloth(request.attributes(), attrMap, cloth);

    Cloth savedCloth = clothRepository.save(cloth);
    log.info("[Creating Cloth Completed] Id: {}, Cloth Name: {}", savedCloth.getId(),
        savedCloth.getName());
    String imageUrl = thumbnailKey != null ? thumbnailImageStorage.get(thumbnailKey) : null;
    aiRecommendationService.evictRecommendationCache(user);
    return clothMapper.toDto(savedCloth, imageUrl);
  }

  /**
   * 구매링크로 의상 등록
   *
   * @param url 구매 링크
   * @return 의상 DTO
   * @throws IOException
   */
  @Override
  public ClothesDto getFromUrl(String url) {
    log.info("[Start Getting Cloth From Url] URL: {}", url);
    ChromeOptions chromeOptions = new ChromeOptions();
    chromeOptions.addArguments("--headless=new");
    chromeOptions.addArguments("--lang=ko");
    chromeOptions.addArguments("--no-sandbox");
    chromeOptions.addArguments("--disable-gpu");
    chromeOptions.addArguments("--window-size=1920,1080");
    chromeOptions.addArguments(
        "--user-agent=Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/137.0.0.0 Mobile Safari/537.36");
    chromeOptions.setPageLoadStrategy(PageLoadStrategy.NONE);

    WebDriver driver = new ChromeDriver(chromeOptions);
    driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(6));
    try {
      driver.get(url);
      SiteParser parser = siteParsers.stream()
          .filter(p -> p.supports(url))
          .findFirst()
          .orElseThrow(() -> {
            log.debug("[Fail Extracting Cloth] Site Not Support - URL: {}", url);
            return new NotSupportSiteException(url);
          });
      parser.waitUntilReady(driver);
      return parser.extract(driver);
    } catch (TimeoutException e) {
      log.warn("Fail Extracting Cloth] Page load timed out - URL: {}", url, e);
      throw new ExtractionTimeOutException(url);
    } catch (NoSuchElementException e) {
      log.warn("[Fail Extracting Cloth] Not Found Infomation - URL: {}", url, e);
      throw new ExtractionNotFoundException(url);
    } catch (Exception e) {
      log.warn("[Fail Extracting Cloth] - URL: {}", url);
      throw new RuntimeException("Fail Extracting Cloth From URL", e);
    } finally {
      driver.quit();
    }
  }

  /**
   * 의상 수정
   *
   * @param clothesId 수정 요청 ID
   * @param request   수정 요청 DTO
   * @return 의상 DTO
   */
  @Override
  public ClothesDto update(UUID clothesId, ClothesUpdateRequest request, MultipartFile image) {
    log.info("[Start Updating Cloth] ID: {}, Cloth Name: {}", clothesId, request.name());
    Cloth cloth = clothRepository.findByIdWithAttributes(clothesId)
        .orElseThrow(() -> {
          log.warn("[Fail Updating Cloth] ID: {}, Cloth Name: {}", clothesId, request.name());
          return new ClothNotFoundException();
        });

    // 썸네일 이미지가 수정사항에 있다면 새로 업로드 후 갱신
    if (image != null && !image.isEmpty()) {
      //기존 이미지 삭제
      String oldImageUrl = cloth.getClothesImageUrl();
      String uploadKey = thumbnailImageStorage.upload(image);
      String uploadUrl = thumbnailImageStorage.get(uploadKey);
      if (oldImageUrl != null) {
        try {
          thumbnailImageStorage.delete(oldImageUrl);
          log.info("[Updating Cloth] Delete Old Image: {}", oldImageUrl);
        } catch (Exception e) {
          log.warn("[Fail Updating Cloth] Fail Deleting Old Image: {}", oldImageUrl);
          thumbnailImageStorage.delete(uploadUrl);
          throw new S3DeleteException();
        }
        log.info("[Updating Cloth] Change ThumbNail Image: {}", uploadUrl);
        cloth.updateImageUrl(uploadUrl);
      }
    }

    String imageUrl =
        cloth.getClothesImageUrl() != null ? thumbnailImageStorage.get(cloth.getClothesImageUrl())
            : null;

    //이름을 수정할 경우
    if (request.name() != null) {
      cloth.updateName(request.name());
    }
    //타입을 수정할 경우
    if (request.type() != null) {
      cloth.updateType(request.type());
    }
    //속성 수정 경우
    List<UUID> attrIds = request.attributes().stream()
        .map(ClothesAttributeDto::definitionId)
        .toList();
    List<Attribute> attributes = attributeRepository.findAllById(attrIds);

    if (request.attributes() != null) {
      cloth.clearAttributes();
      Map<UUID, Attribute> attributeMap = attributes.stream()
          .collect(Collectors.toMap(Attribute::getId, Function.identity()));

      applyAttributesToCloth(request.attributes(), attributeMap, cloth);

    }
    User user = cloth.getUser();
    if (user != null) {
      aiRecommendationService.evictRecommendationCache(user);
    }
    log.info("[Updating Cloth Completed] ID : {}, Name: {}", clothesId, cloth.getName());
    return clothMapper.toDto(cloth, imageUrl);
  }

  @Override
  @Transactional(readOnly = true)
  public PageResponse<ClothesDto> searchClothes(ClothesSearchRequest request) {
    log.info("[Start Searching Cloth] ownerId: {}, typeEqual: {}, limit: {}",
        request.ownerId(), request.typeEqual(), request.limit());
    Instant cursor = null;
    if (request.cursor() != null && !request.cursor().isBlank()) {
      try {
        cursor = Instant.parse(request.cursor());
      } catch (DateTimeParseException e) {
        throw new IllegalArgumentException("Unsupported cursor: " + request.cursor());
      }
    }

    UUID idAfter = request.idAfter();
    int limit = request.limit();
    ClothType typeEqual = request.typeEqual();
    UUID ownerId = request.ownerId();

    Slice<Cloth> clothes = clothRepository.searchCloths(cursor, idAfter, limit, typeEqual, ownerId);
    List<Cloth> clothesList = clothes.getContent();
    log.debug("[Query Result] Total Count: {}, hasNext: {}", clothesList.size(), clothes.hasNext());

    //toDto : N+1문제 해결위해 한번에 갖고오기
    List<UUID> ids = clothesList.stream()
        .map(Cloth::getId)
        .toList();
    List<Cloth> clothesWithAttrs = clothRepository.findAllByIdWithAttributes(ids);

    List<ClothesDto> data = clothesWithAttrs.stream()
        .map(cloth -> {
          String imageUrl =
              cloth.getClothesImageUrl() != null
                  ? thumbnailImageStorage.get(cloth.getClothesImageUrl())
                  : null;
          return clothMapper.toDto(cloth, imageUrl);
        })
        .toList();
    log.debug("[Response Result] Count Changed To ClothesDto: {}", data.size());

    Cloth last =
        (clothesList.size() > 0) ? clothesList.get(clothesList.size() - 1) : null;

    Instant nextCursor = (last != null) ? last.getCreatedAt() : null;
    UUID nextIdAfter = (last != null) ? last.getId() : null;

    Long totalCount = clothRepository.getTotalCount(ownerId, typeEqual);

    return new PageResponse<>(
        data,
        nextCursor,
        nextIdAfter,
        clothes.hasNext(),
        totalCount,
        Cloth.FIELD_CREATED_AT,
        SortDirection.DESCENDING.name()
    );
  }

  /**
   * 의상 삭제
   *
   * @param clothesId 의상 ID
   */
  @Override
  public void delete(UUID clothesId) {
    log.info("[Start Deleting Cloth] ID: {}", clothesId);
    Cloth cloth = clothRepository.findById(clothesId)
        .orElseThrow(() -> {
          log.warn("[Fail Deleting Cloth] Cloth Not Found ID: {}", clothesId);
          return new ClothNotFoundException();
        });

    // 썸네일 이미지가 있다면 S3에서 삭제
    if (cloth.getClothesImageUrl() != null) {
      log.debug("[Request Deleting S3 Image] Key: {}", cloth.getClothesImageUrl());
      try {
        thumbnailImageStorage.delete(cloth.getClothesImageUrl());
        log.info("[Delete Cloth] Deleting S3 ThumbNail Completed: {}", cloth.getClothesImageUrl());
      } catch (Exception e) {
        log.warn("[Fail Deleting Cloth] Fail Deleting S3 ThumbNail: {}",
            cloth.getClothesImageUrl());
        throw new S3DeleteException();
      }
    }
    clothRepository.delete(cloth);
    User user = cloth.getUser();
    if (user != null) {
      aiRecommendationService.evictRecommendationCache(user);
    }
    log.info("[Deleting Cloth Completed] ID: {}", clothesId);
  }

  private static void applyAttributesToCloth(List<ClothesAttributeDto> attributeDtos,
      Map<UUID, Attribute> attrMap,
      Cloth cloth) {
    for (ClothesAttributeDto dto : attributeDtos) {
      Attribute attribute = attrMap.get(dto.definitionId());
      if (attribute == null) {
        log.warn("[Fail Applying Attributes To Cloth] Attribute Not Found, ID : {}",
            dto.definitionId());
        throw new AttributeNotFoundException();
      }
      if (!attribute.getSelectableValues().contains(dto.value())) {
        log.warn("[Fail Applying Attributes To Cloth] Value Not Found, ID : {}",
            dto.definitionId());
        throw new InvalidAttributeValueException();
      }

      ClothWithAttributes attr = ClothWithAttributes.builder()
          .value(dto.value())
          .attribute(attribute)
          .cloth(cloth)
          .build();

      cloth.addAttribute(attr);
    }
    log.debug("[Applying Attributes Completed]");

  }

}

