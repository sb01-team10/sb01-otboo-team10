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
import com.codeit.weatherwear.domain.clothes.exception.AttributeNotFoundException;
import com.codeit.weatherwear.domain.clothes.exception.ClothNotFoundException;
import com.codeit.weatherwear.domain.clothes.exception.InvalidAttributeNameException;
import com.codeit.weatherwear.domain.clothes.mapper.ClothMapper;
import com.codeit.weatherwear.domain.clothes.repository.AttributeRepository;
import com.codeit.weatherwear.domain.clothes.repository.ClothRepository;
import com.codeit.weatherwear.domain.clothes.service.parser.SiteParser;
import com.codeit.weatherwear.domain.user.entity.User;
import com.codeit.weatherwear.domain.user.exception.UserNotFoundException;
import com.codeit.weatherwear.domain.user.repository.UserRepository;
import com.codeit.weatherwear.global.request.SortDirection;
import com.codeit.weatherwear.global.response.PageResponse;
import com.codeit.weatherwear.global.storage.ThumbnailImageStorage;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.List;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.PageLoadStrategy;
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

    /**
     * 의상 등록
     *
     * @param request 의상 생성 요청 DTO
     * @return 의상 DTO
     */
    @Override
    public ClothesDto create(ClothesCreateRequest request, Optional<MultipartFile> image) {
        //사용자 찾기
        User user = userRepository.findById(request.ownerId())
            .orElseThrow(()-> {
                log.warn("[옷 등록 실패] 존재하지 않는 사용자 : {}", request.ownerId());
              return new UserNotFoundException();
            });

        // 썸네일 S3 업로드
        log.debug("[썸네일 업로드 시작]");
        String thumbnailKey =
            image.filter(file -> !file.isEmpty())
                .map(thumbnailImageStorage::upload)
                .orElse(null);
        log.info("[이미지 업로드] S3에 업로드 완료 S3 Key: {}", thumbnailKey);

        Cloth cloth=Cloth.builder()
            .name(request.name())
            .clothType(request.type())
            .clothesImageUrl(thumbnailKey)
            .user(user)
            .build();

        List<UUID> attributesIds = request.attributes().stream()
            .map(ClothesAttributeDto::definitionId).toList();

        //속성 찾기
        List<Attribute> attributesList=attributeRepository.findAllById(attributesIds);


        //의상에 속성 적용
        Map<UUID, Attribute> attrMap = attributesList.stream()
            .collect(Collectors.toMap(Attribute::getId, Function.identity()));

        applyAttributesToCloth(request.attributes(), attrMap, cloth);

        Cloth saveCloth = clothRepository.save(cloth);
        log.info("[옷 등록 완료] id: {}, 옷 이름: {}", saveCloth.getId(), saveCloth.getName());
        String imageUrl = thumbnailKey != null ? thumbnailImageStorage.get(thumbnailKey) : null;
        return clothMapper.toDto(saveCloth,imageUrl);
    }

    /**
     * 구매링크로 의상 등록
     *
     * @param url 구매 링크
     * @return 의상 DTO
     * @throws IOException
     */
    @Override
    public ClothesDto createFromUrl(String url) {

        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.addArguments("--headless=new");
        chromeOptions.addArguments("--lang=ko");
        chromeOptions.addArguments("--no-sandbox");
        chromeOptions.addArguments("--disable-gpu");
        chromeOptions.addArguments("--user-agent=Mozilla/5.0 (Linux; Android 10) Chrome/90.0.4430.85 Mobile Safari/537.36");
        chromeOptions.setPageLoadStrategy(PageLoadStrategy.NONE);

        WebDriver driver = new ChromeDriver(chromeOptions);
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(6));
        try {
            driver.get(url);
            SiteParser parser = siteParsers.stream()
                .filter(p -> p.supports(url))
                .findFirst()
                //TODO: 추후 커스텀 예외 고민
                .orElseThrow(() -> new IllegalArgumentException("지원하지 않는 사이트입니다: " + url));
            parser.waitUntilReady(driver);
            return parser.extract(driver);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            driver.quit();
        }
    }

    /**
     * 의상 수정
     *
     * @param clothesId 수정 요청 ID
     * @param request 수정 요청 DTO
     * @return 의상 DTO
     */
    @Override
    public ClothesDto update(UUID clothesId,ClothesUpdateRequest request,Optional<MultipartFile> image) {
        Cloth cloth = clothRepository.findByIdWithAttributes(clothesId)
            .orElseThrow(()->{
                log.warn("[옷 수정 실패] id: {}, 수정 요청한 옷 이름: {}", clothesId, request.name());
                return new ClothNotFoundException();
            });

        // 썸네일 이미지가 있다면 새로 업로드 후 갱신
        image.filter(file -> !file.isEmpty())
            .ifPresent(file -> {
                String uploadUrl = thumbnailImageStorage.upload(file);
                log.info("[옷 수정] 썸네일 이미지 변경됨: {}", uploadUrl);
                cloth.updateImageUrl(uploadUrl);
            });

        String imageUrl =
            cloth.getClothesImageUrl() != null ? thumbnailImageStorage.get(cloth.getClothesImageUrl()) : null;


        List<UUID> attrIds = request.attributes().stream()
            .map(ClothesAttributeDto::definitionId)
            .toList();
        List<Attribute> attributes = attributeRepository.findAllById(attrIds);

        cloth.clearAttributes();
        cloth.updateCloth(request.name(),request.type());

        Map<UUID, Attribute> attributeMap = attributes.stream()
            .collect(Collectors.toMap(Attribute::getId, Function.identity()));

        applyAttributesToCloth(request.attributes(), attributeMap, cloth);

        log.info("[옷 수정 완료] ID : {}, name: {}", clothesId, cloth.getName());
        return clothMapper.toDto(cloth,imageUrl);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ClothesDto> searchClothes(ClothesSearchRequest request) {
        Instant cursor = null;
        if(request.cursor() != null && !request.cursor().isBlank()){
            try {
                cursor = Instant.parse(request.cursor());
            } catch (DateTimeParseException e) {
                throw new IllegalArgumentException("잘못된 커서 형식입니다.");
            }
        }

        UUID idAfter=request.idAfter();
        int limit=request.limit();
        ClothType typeEqual=request.typeEqual();
        UUID ownerId=request.ownerId();

        Slice<Cloth> clothes = clothRepository.searchCloths(cursor,idAfter,limit,typeEqual,ownerId);
        List<Cloth> clothesList = clothes.getContent();
        log.debug("[쿼리 실행 결과] 전체 개수: {}, hasNext: {}", clothesList.size(), clothes.hasNext());

        //toDto : N+1문제 해결위해 한번에 갖고오기
        List<UUID> ids = clothesList.stream()
            .map(Cloth::getId)
            .toList();
        List<Cloth> clothesWithAttrs=clothRepository.findAllByIdWithAttributes(ids);

        List<ClothesDto> data=clothesWithAttrs.stream()
            .map(cloth->{
                String imageUrl=
                    cloth.getClothesImageUrl() != null
                        ? thumbnailImageStorage.get(cloth.getClothesImageUrl())
                        : null;
                return clothMapper.toDto(cloth,imageUrl);
            })
            .toList();
        log.debug("[응답 변환] 변환된 ClothesDto 개수: {}", data.size());

        Cloth last =
            (clothesList.size() > 0) ? clothesList.get(clothesList.size() - 1) : null;

        Instant nextCursor = (last !=null) ? last.getCreatedAt() : null;
        UUID nextIdAfter = (last !=null) ? last.getId() : null;

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
        Cloth cloth = clothRepository.findById(clothesId)
            .orElseThrow(()->{
                log.warn("[옷 삭제 실패] 존재하지 않는 옷 ID: {}", clothesId);
                return new ClothNotFoundException();
            });
        clothRepository.delete(cloth);
        log.info("[옷 삭제 완료] ID: {}", clothesId);
    }


    private static void applyAttributesToCloth(List<ClothesAttributeDto> attributeDtos, Map<UUID, Attribute> attrMap,
        Cloth cloth) {
        for (ClothesAttributeDto dto : attributeDtos) {
            Attribute attribute = attrMap.get(dto.definitionId());
            if (attribute == null) {
                log.warn("[옷의 속성 값 적용 실패] 존재하지 않는 속성입니다. ID : {}", dto.definitionId());
                throw new AttributeNotFoundException();
            }
            if(!attribute.getSelectableValues().contains(dto.value())) {
                log.warn("[옷의 속성 값 적용 실패] 존재하지 않는 속성 값입니다. ID : {}", dto.definitionId());
                throw new InvalidAttributeNameException();
            }

            ClothWithAttributes attr = ClothWithAttributes.builder()
                .value(dto.value())
                .attribute(attribute)
                .cloth(cloth)
                .build();

            cloth.addAttribute(attr);
            log.debug("[옷 속성 값 적용 완료]");
        }
    }

}

