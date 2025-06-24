package com.codeit.weatherwear.clothes.service;

import static org.assertj.core.api.Assertions.*;
import static org.awaitility.Awaitility.given;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.codeit.weatherwear.clothes.attributes.dto.ClothesAttributeDefCreateRequest;
import com.codeit.weatherwear.clothes.attributes.entity.Attributes;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class AttributeDefServiceTest {

    @Mock
    private AttributesRepository attributesRepository;

    @Mock
    private AttributesMapper attributesMapper;

    @InjectMocks
    private AttributesServiceImpl sut;

    @Nested
    @DisplayName("속성 등록 테스트")
    class RegisterAttributeDef {
        @Test
        void createAttributes_Success(){
            //given
            ClothesAttributeDefCreateRequest dto= new ClothesAttributeDefCreateRequest(
                UUID.randomUUID(),
                "색상",
                List.of("빨강", "파랑"));
            Attributes attributes = Attributes.builder()
                .id(UUID.randomUUID())
                .createdAt(Instant.now())
                .name("색상")
                .selectable_values(List.of("빨강","파랑")).build();
            given(attributesRepository.save(any(Attributes.class))).willReturn(attributes);
            given(attributesMapper.toDto(any(Attributes.class))).willReturn(dto);
            //when
            ClothesAttributeDefDto result=sut.create(request);
            //then
            assertThat(result.getName()).isEqualTo("색상");
            assertThat(result.getSelectableValues()).containExactly("빨강", "파랑");
            verify(attributesRepository, times(1)).save(any(Attributes.class));
        }
    }

}
