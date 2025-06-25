package com.codeit.weatherwear.domain.user.dto.response;

import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class AuthorDto {

  private final UUID userId;
  private final String name;
  private final String profileImgUrl;

}
