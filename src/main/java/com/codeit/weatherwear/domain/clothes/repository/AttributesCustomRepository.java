package com.codeit.weatherwear.domain.clothes.repository;

import com.codeit.weatherwear.domain.clothes.dto.request.AttributesSortDirection;
import com.codeit.weatherwear.domain.clothes.entity.Attributes;

import java.util.UUID;
import org.springframework.data.domain.Slice;

public interface AttributesCustomRepository {
    Slice<Attributes> searchAttributes(
        String cursor,
        UUID idAfter,
        int limit,
        String sortBy,
        AttributesSortDirection sortDirection,
        String keywordLike
    );
    Long getTotalCount(String keywordLike);
}