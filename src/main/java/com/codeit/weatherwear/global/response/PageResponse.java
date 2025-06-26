package com.codeit.weatherwear.global.response;

import java.util.List;
import java.util.UUID;

public record PageResponse<T>(
    List<T> data,
    Object nextCursor,
    UUID nextIdAfter,
    boolean hasNext,
    long totalCount,
    String sortBy,
    String sortDirection
) {

}
