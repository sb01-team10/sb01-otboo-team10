package com.codeit.weatherwear.domain.clothes.service;

import com.codeit.weatherwear.domain.clothes.dto.request.AttributesSearchRequest;
import com.codeit.weatherwear.domain.clothes.dto.request.AttributesSortDirection;
import com.codeit.weatherwear.domain.clothes.dto.request.ClothesAttributeDefCreateRequest;
import com.codeit.weatherwear.domain.clothes.dto.response.ClothesAttributeDefDto;
import com.codeit.weatherwear.domain.clothes.dto.request.ClothesAttributeDefUpdateRequest;
import com.codeit.weatherwear.domain.clothes.entity.Attributes;
import com.codeit.weatherwear.domain.clothes.mapper.AttributesMapper;
import com.codeit.weatherwear.domain.clothes.repository.AttributesRepository;
import com.codeit.weatherwear.global.response.PageResponse;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
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
        if(attributesRepository.existsByName(request.name())){
            throw new IllegalArgumentException("이미 등록된 속성입니다.");
        }
        Set<String> uniqueValues = new HashSet<>(request.selectValues());
        if (uniqueValues.size() != request.selectValues().size()) {
            throw new IllegalArgumentException("중복된 값이 입력되었습니다.");
        }

        Attributes attributes = Attributes.builder()
            .name(request.name())
            .selectableValues(request.selectValues())
            .build();

        Attributes save = attributesRepository.save(attributes);
        return attributesMapper.toDto(save);
    }

    /**
     * 속성 수정
     * @param id
     * @param request 속성 수정 요청 DTO
     * @return 속성 DTO
     */
    @Override
    @Transactional
    public ClothesAttributeDefDto update(UUID id, ClothesAttributeDefUpdateRequest request) {
        Attributes attributes = attributesRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 속성입니다"));
        attributes.update(request);
        return attributesMapper.toDto(attributes);
    }

    /**
     * 속성 삭제
     * @param id
     */
    @Override
    @Transactional
    public void delete(UUID id) {
        Attributes attributes = attributesRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 속성입니다"));
        attributesRepository.deleteById(attributes.getId());
    }

    /**
     * 속성 조회
     * @param request 조회 조건
     * @return List<ClothesAttributeDefDto> 결과 리스트
     */
    @Override
    public PageResponse<ClothesAttributeDefDto> searchAttributes(AttributesSearchRequest request) {
        String cursor = request.cursor();
        UUID idAfter = request.idAfter();
        int limit = request.limit();
        String sortBy = request.sortBy();
        AttributesSortDirection sortDirection = request.sortDirection();
        String keywordLike = request.keywordLike();

        Slice<Attributes> attributes = attributesRepository.searchAttributes(cursor, idAfter, limit,
            sortBy, sortDirection, keywordLike);

        List<Attributes> attributesList = attributes.getContent();
        List<ClothesAttributeDefDto> result = attributesList.stream()
            .map(attributesMapper::toDto)
            .toList();

        Attributes last =
            (attributesList.size() > 0) ? attributesList.get(attributesList.size() - 1) : null;
        Object nextCursor = null;
        UUID nextIdAfter = null;
        if(last != null) {
            switch (sortBy) {
                case "name":
                    nextCursor = last.getName();
                    break;
                case "createdAt":
                    nextCursor = last.getCreatedAt();
                    break;
                default:
                    throw new IllegalArgumentException("지원하지 않는 정렬 기준입니다.");
            }
            nextIdAfter = last.getId();
        }
        return new PageResponse<>(
            result,
            nextCursor,
            nextIdAfter,
            attributes.hasNext(),
            attributesRepository.getTotalCount(keywordLike),
            sortBy,
            sortDirection.name()
        );
    }


}
