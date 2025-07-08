package com.codeit.weatherwear.domain.recommendation.service;


import com.codeit.weatherwear.domain.clothes.dto.response.ClothesDto;
import com.codeit.weatherwear.domain.clothes.entity.Cloth;
import com.codeit.weatherwear.domain.clothes.entity.ClothType;
import com.codeit.weatherwear.domain.clothes.entity.ClothWithAttributes;
import com.codeit.weatherwear.domain.clothes.mapper.ClothMapper;
import com.codeit.weatherwear.domain.clothes.repository.ClothRepository;
import com.codeit.weatherwear.domain.recommendation.dto.RecommendationDto;
import com.codeit.weatherwear.domain.user.entity.User;
import com.codeit.weatherwear.domain.user.exception.UserNotFoundException;
import com.codeit.weatherwear.domain.user.repository.UserRepository;
import com.codeit.weatherwear.domain.weather.entity.Weather;
import com.codeit.weatherwear.domain.weather.entity.WindSpeedType;
import com.codeit.weatherwear.domain.weather.repository.WeatherRepository;
import com.codeit.weatherwear.global.storage.ThumbnailImageStorage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RecommendationServiceImpl implements RecommendationService {

  private final UserRepository userRepository;
  private final WeatherRepository weatherRepository;
  private final ClothRepository clothRepository;
  private final ThumbnailImageStorage thumbnailImageStorage;
  private final ClothMapper clothMapper;

  /**
   * 의상 추천
   *
   * @param weatherId 날씨 ID
   * @return 추천 DTO
   */
  @Override
  public RecommendationDto recommendClothes(UUID weatherId) {
    //사용자 조회
    String email = SecurityContextHolder.getContext().getAuthentication().getName();
    User user = userRepository.findByEmail(email).orElseThrow(() -> {
      log.warn("[추천 조회 실패] 존재하지 않는 사용자 email: {}", email);
      return new UserNotFoundException();
    });
    Weather weather = weatherRepository.findById(weatherId).orElseThrow(() -> {
      log.warn("[추천 조회 실패] 존재하지 않는 날씨 id: {}", weatherId);
      //TODO: 날씨 기능 완료 후 변경하기
      return new IllegalArgumentException("존재하지 않는 날씨 입니다.");
    });

    List<Cloth> clothes = clothRepository.findAllWithAttributesByUserId(user.getId());

    List<Cloth> filtered = filterCloth(user, weather, clothes);

    //타입별 1개씩 선택
    List<ClothesDto> recommendedClothes = new ArrayList<>();
    // 타입별로 필터링된 옷을 그룹화
    Map<ClothType, List<Cloth>> groupedFilteredClothes = filtered.stream()
        .collect(Collectors.groupingBy(Cloth::getClothType));

    // DRESS 타입 처리: DRESS가 선택되면 TOP과 BOTTOM은 제외
    List<Cloth> dresses = groupedFilteredClothes.getOrDefault(ClothType.DRESS, Collections.emptyList());
    if (!dresses.isEmpty()) {
      // DRESS 중 무작위로 하나 선택하여 추천 목록에 추가
      Cloth randomDress = getRandomCloth(dresses);
      String imageUrl = randomDress.getClothesImageUrl() != null
          ? thumbnailImageStorage.get(randomDress.getClothesImageUrl())
          : null;

      recommendedClothes.add(clothMapper.toDto(randomDress, imageUrl));
      // DRESS가 선택되었으므로 TOP과 BOTTOM은 추천 대상에서 제외
      groupedFilteredClothes.remove(ClothType.TOP);
      groupedFilteredClothes.remove(ClothType.BOTTOM);
      groupedFilteredClothes.remove(ClothType.DRESS); // DRESS는 이미 선택했으니 다시 고려하지 않음
    }

    // 나머지 타입별로 하나씩 선택하여 추천 목록에 추가
    // TOP, BOTTOM은 DRESS 선택 여부에 따라 이미 제거되었을 수 있음
    for (Map.Entry<ClothType, List<Cloth>> entry : groupedFilteredClothes.entrySet()) {
      List<Cloth> clothsOfType = entry.getValue();
      if (!clothsOfType.isEmpty()) {
        Cloth randomCloth = getRandomCloth(clothsOfType);
        String imageUrl = randomCloth.getClothesImageUrl() != null
            ? thumbnailImageStorage.get(randomCloth.getClothesImageUrl())
            : null;
        recommendedClothes.add(clothMapper.toDto(randomCloth, imageUrl));
      }
    }

    return RecommendationDto.builder()
        .weatherId(weatherId)
        .userId(user.getId())
        .clothes(recommendedClothes)
        .build();
  }

  private List<Cloth> filterCloth(User user,Weather weather,List<Cloth> cloths) {
    //체감온도 계산 t:온도 v:풍속
    double t = weather.getTemperature().getCurrent();
    double v = weather.getWindSpeed().getSpeed();
    double apparent = 13.12 + 0.6215 * t - 11.37 * Math.pow(v, 0.16) + 0.3965 * Math.pow(v, 0.16) * t;

    //민감도 보정
    int sensitivity = Optional.ofNullable(user.getTemperatureSensitivity()).orElse(2);
    double adjusted = apparent + (sensitivity - 2) * 2;
    
    //날씨 조건 추출
    double rainProb = weather.getPrecipitation().getProbability();
    WindSpeedType windSpeedType = weather.getWindSpeed().getSpeedAsWord();

    //옷 필터링
    return cloths.stream()
        .filter(c -> isSuitable(c, adjusted, rainProb, windSpeedType))
        .toList();
  }

  private boolean isSuitable(Cloth cloth, double temp, double rainProb,
      WindSpeedType windSpeedType) {
    // 옷의 속성 리스트를 가져와 Map 형태로 변환하여 쉽게 접근
    List<ClothWithAttributes> clothesWithAttributes = cloth.getClothesWithAttributes();
    Map<String, String> attributeMap = clothesWithAttributes.stream()
        .collect(Collectors.toMap(
            attr -> attr.getAttribute().getName().toLowerCase(), // 속성 정의 이름
            attr -> attr.getValue().toLowerCase()// 속성 값
        ));

    String thickness = attributeMap.get("두께");
    String season = attributeMap.get("계절");
    String waterproof = attributeMap.get("방수");

    if (season != null) {
      switch (season) {
        case "봄":
          if (temp < 5 || temp > 20)
            return false;
          break;
        case "여름":
          if (temp < 20)
            return false;
          break;
        case "가을":
          if (temp < 10 || temp > 25)
            return false;
          break;
        case "겨울":
          if (temp > 10)
            return false;
          break;
      }
    }

    if (thickness != null) {
      switch (thickness) {
        case "아주 두꺼움":
          if (temp > 5)
            return false; // 5도 초과면 아주 두꺼운 옷은 부적합
          break;
        case "약간 두꺼움":
          if (temp > 12 || temp < 0)
            return false; // 0~12도 범위
          break;
        case "약간 얇음":
          if (temp < 15 || temp > 25)
            return false; // 15~25도 범위
          break;
        case "아주 얇음":
          if (temp < 20)
            return false; // 20도 미만이면 아주 얇은 옷은 부적합
          break;
      }
    }

    // 3. 강수 확률 필터링: 비 올 확률이 50% 이상인데 방수 속성이 없으면 부적합
    if (rainProb > 50 && waterproof.isBlank()) {
      return false;
    }

    return true;
  }

  private Cloth getRandomCloth(List<Cloth> clothes) {
    if (clothes == null || clothes.isEmpty()) {
      return null; // 선택할 옷이 없음
    }
    return clothes.get(ThreadLocalRandom.current().nextInt(clothes.size()));
  }
}
