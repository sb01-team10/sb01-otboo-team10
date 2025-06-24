package com.codeit.weatherwear.clothes.service;

import static org.assertj.core.api.Assertions.*;
import static org.awaitility.Awaitility.given;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

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
public class AttributesServiceTest {

    @Mock
    private AttributesRepository attributesRepository;

    @Mock
    private AttributesMapper attributesMapper;

    @InjectMocks
    private AttributesServiceImpl sut;

    @Nested
    @DisplayName("속성 등록 테스트")
    class RegisterAttributes {
        @Test
        void createAttributes_Success(){
            //given
            ClothesAttributeWithDefDto dto= new ClothesAttributeWithDefDto(UUID.randomUUID(),"색상",List.of("빨강", "파랑"),null);
            Attributes attributes = new Attributes(UUID.randomUUID(), "색상", List.of("빨강", "파랑"));
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
