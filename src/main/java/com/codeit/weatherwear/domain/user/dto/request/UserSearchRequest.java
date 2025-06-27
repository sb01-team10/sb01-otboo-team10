package com.codeit.weatherwear.domain.user.dto.request;

import com.codeit.weatherwear.domain.user.entity.Role;
import com.codeit.weatherwear.global.request.SortDirection;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record UserSearchRequest(
    String cursor,
    UUID idAfter,

    @NotNull
    @Min(1)
    @Max(100)
    int limit,

    @NotBlank
    String sortBy,

    @NotNull
    SortDirection sortDirection,

    String emailLike,
    Role roleEqual,
    Boolean locked
) {

}