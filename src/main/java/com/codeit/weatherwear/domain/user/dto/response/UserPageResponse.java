package com.codeit.weatherwear.domain.user.dto.response;

import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserPageResponse<T> {

    private final List<T> data;

    private final Object nextCursor;

    private final UUID nextIdAfter;

    private final Boolean hasNext;

    private final Long totalCount;

    private final String sortBy;

    private final String sortDirection;
}
