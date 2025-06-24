package com.codeit.weatherwear.clothes.service;

import com.codeit.weatherwear.clothes.dto.ClothesAttributeDefCreateRequest;
import com.codeit.weatherwear.clothes.dto.ClothesAttributeDefDto;
import com.codeit.weatherwear.clothes.entity.Attributes;
import com.codeit.weatherwear.clothes.mapper.AttributesMapper;
import com.codeit.weatherwear.clothes.repository.AttributesRepository;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AttributesServiceImpl implements AttributesService{

    private final AttributesRepository attributesRepository;
    private final AttributesMapper mapper;

    @Override
    public ClothesAttributeDefDto create(ClothesAttributeDefCreateRequest request) {
        List<String> list = request.selectValues();
        Attributes attributes = Attributes.builder()
            .id(UUID.randomUUID())
            .createdAt(Instant.now())
            .name(request.definitionName())
            .selectableValues(list)
            .build();
        Attributes save = attributesRepository.save(attributes);
        return mapper.toDto(save);
    }
}
