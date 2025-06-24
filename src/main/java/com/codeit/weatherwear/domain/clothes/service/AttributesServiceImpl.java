package com.codeit.weatherwear.domain.clothes.service;

import com.codeit.weatherwear.domain.clothes.dto.ClothesAttributeDefCreateRequest;
import com.codeit.weatherwear.domain.clothes.dto.ClothesAttributeDefDto;
import com.codeit.weatherwear.domain.clothes.entity.Attributes;
import com.codeit.weatherwear.domain.clothes.mapper.AttributesMapper;
import com.codeit.weatherwear.domain.clothes.repository.AttributesRepository;
import java.util.HashSet;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AttributesServiceImpl implements AttributesService{

    private final AttributesRepository attributesRepository;
    private final AttributesMapper attributesMapper;

    /**
     * 속성 등록
     * @param request 속성 생성 요청 DTO
     * @return 속성DTO
     */
    @Override
    @Transactional
    public ClothesAttributeDefDto create(ClothesAttributeDefCreateRequest request) {
        if(attributesRepository.existsByName(request.definitionName())){
            throw new IllegalArgumentException("이미 등록된 속성입니다.");
        }
        if(request.selectValues().isEmpty()){
            throw new IllegalArgumentException("값 입력은 필수입니다.");
        }
        Set<String> uniqueValues = new HashSet<>(request.selectValues());
        if (uniqueValues.size() != request.selectValues().size()) {
            throw new IllegalArgumentException("중복된 값이 입력되었습니다.");
        }

        Attributes attributes = Attributes.builder()
            .name(request.definitionName())
            .selectableValues(request.selectValues())
            .build();

        Attributes save = attributesRepository.save(attributes);
        return attributesMapper.toDto(save);
    }
}
