package com.codeit.weatherwear.domain.ootd.mapper;

import com.codeit.weatherwear.domain.feed.entity.Feed;
import com.codeit.weatherwear.domain.ootd.dto.response.OotdDto;
import com.codeit.weatherwear.domain.ootd.entity.Ootd;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OotdMapper {

  public Ootd toEntity(Feed feed, UUID clotheId) {
    return Ootd.builder()
        .feed(feed)
        .clothesId(clotheId)
        .build();
  }

  public OotdDto toDto(Ootd ootd) {
    // todo: name 부터 아래는 clothes의 요소라 추후 Clothes 추가 시 수정할 예정
    return OotdDto.builder()
        .clothesId(ootd.getClothesId())
        .name("임시 옷 이름")
        .imageUrl(null)
        .type(null)
        .attributes(null)
        .build();
  }

}
