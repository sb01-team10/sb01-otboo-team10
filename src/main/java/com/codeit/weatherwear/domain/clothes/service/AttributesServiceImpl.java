package com.codeit.weatherwear.domain.clothes.service;

import com.codeit.weatherwear.domain.clothes.dto.ClothesAttributeDefCreateRequest;
import com.codeit.weatherwear.domain.clothes.dto.ClothesAttributeDefDto;
import com.codeit.weatherwear.domain.clothes.entity.Attributes;
import com.codeit.weatherwear.domain.clothes.mapper.AttributesMapper;
import com.codeit.weatherwear.domain.clothes.repository.AttributesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AttributesServiceImpl implements AttributesService{

    private final AttributesRepository attributesRepository;
    private final AttributesMapper mapper;

    @Override
    public ClothesAttributeDefDto create(ClothesAttributeDefCreateRequest request) {
        if(attributesRepository.existsByName(request.definitionName())){
            throw new IllegalArgumentException("이미 등록된 속성입니다.");
        }
        if(request.selectValues().isEmpty()){
            throw new IllegalArgumentException("값 입력은 필수입니다.");
        }
        if (request.selectValues().size() != request.selectValues().stream().distinct().count()) {
            throw new IllegalArgumentException("중복된 값을 입력하셨습니다.");
        }

        Attributes attributes = Attributes.builder()
            .name(request.definitionName())
            .selectableValues(request.selectValues())
            .build();

        Attributes save = attributesRepository.save(attributes);
        return mapper.toDto(save);
    }
}
