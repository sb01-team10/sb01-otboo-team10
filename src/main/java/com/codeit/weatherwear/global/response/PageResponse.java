package com.codeit.weatherwear.global.response;

import java.util.UUID;

public record PageResponse<T>(
    T data,
    Object nextCursor,
    UUID nextIdAfter,
    boolean hasNext,
    long totalCount,
    String sortBy,
    String sortDirection
) {

}
