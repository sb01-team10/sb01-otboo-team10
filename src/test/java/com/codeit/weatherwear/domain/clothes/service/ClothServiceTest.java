package com.codeit.weatherwear.domain.clothes.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


import com.codeit.weatherwear.domain.clothes.dto.request.ClothesAttributeDto;
import com.codeit.weatherwear.domain.clothes.dto.response.ClothesAttributeWithDefDto;
import com.codeit.weatherwear.domain.clothes.dto.request.ClothesCreateRequest;
import com.codeit.weatherwear.domain.clothes.dto.request.ClothesUpdateRequest;
import com.codeit.weatherwear.domain.clothes.dto.response.ClothesDto;
import com.codeit.weatherwear.domain.clothes.entity.Attribute;
import com.codeit.weatherwear.domain.clothes.entity.Cloth;
import com.codeit.weatherwear.domain.clothes.entity.ClothType;
import com.codeit.weatherwear.domain.clothes.exception.AttributeNotFoundException;
import com.codeit.weatherwear.domain.clothes.exception.ClothNotFoundException;
import com.codeit.weatherwear.domain.clothes.mapper.ClothMapper;
import com.codeit.weatherwear.domain.clothes.repository.AttributeRepository;
import com.codeit.weatherwear.domain.clothes.repository.ClothRepository;
import com.codeit.weatherwear.domain.clothes.service.parser.SiteParser;
import com.codeit.weatherwear.domain.user.entity.User;
import com.codeit.weatherwear.domain.user.repository.UserRepository;
import com.codeit.weatherwear.global.storage.ThumbnailImageStorage;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ClothServiceTest {

    @Mock
    private AttributeRepository attributeRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ClothRepository clothRepository;
    @Mock
    private ThumbnailImageStorage thumbnailImageStorage;
    @Mock
    private ClothMapper mapper;
    @Mock
    private List<SiteParser> siteParsers;

    @InjectMocks
    private ClothServiceImpl sut;

    @Nested
    @DisplayName("의상 등록 테스트 - 이미지 없음")
    class RegisterCloth {

        @Test
        @DisplayName("등록 성공")
        void create_success() {
            //given
            UUID ownerId = UUID.randomUUID();
            UUID clothesId = UUID.randomUUID();
            UUID colorId = UUID.randomUUID();
            UUID sizeId = UUID.randomUUID();

            ClothesAttributeDto color = new ClothesAttributeDto(colorId, "파랑");
            ClothesAttributeDto size = new ClothesAttributeDto(sizeId, "S");
            ClothesCreateRequest request = new ClothesCreateRequest(
                ownerId,
                "후드티",
                ClothType.TOP,
                List.of(color, size)
            );
            ClothesAttributeWithDefDto colorDto = new ClothesAttributeWithDefDto(colorId,
                "색상", List.of("빨강", "파랑"), "파랑");
            ClothesAttributeWithDefDto sizeDto = new ClothesAttributeWithDefDto(sizeId,
                "사이즈", List.of("S", "L"), "S");

            Attribute colorDef = Attribute.builder()
                .id(colorId)
                .name("색상")
                .selectableValues(List.of("빨강", "파랑"))
                .build();
            Attribute sizeDef = Attribute.builder()
                .id(sizeId)
                .name("사이즈")
                .selectableValues(List.of("S", "L"))
                .build();

            ClothesDto clothesDto = ClothesDto.builder()
                .id(clothesId)
                .ownerId(ownerId)
                .name("후드티")
                .imageUrl(null)
                .type(ClothType.TOP)
                .attributes(List.of(colorDto, sizeDto))
                .build();

            Cloth clothes = Cloth.builder()
                .id(clothesId)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .name("후드티")
                .clothType(ClothType.TOP)
                .clothesImageUrl(null)
                .user(User.builder().id(ownerId).build())
                .build();

            User mockUser = User.builder().id(ownerId).build();
            given(userRepository.findById(ownerId)).willReturn(Optional.of(mockUser));

            given(attributeRepository.findAllById(any())).willReturn(List.of(colorDef, sizeDef));
            given(clothRepository.save(any(Cloth.class))).willReturn(clothes);
            given(mapper.toDto(any(Cloth.class),any())).willReturn(clothesDto);
            //when
            ClothesDto result = sut.create(request,Optional.empty());
            //then
            assertThat(result.getName()).isEqualTo("후드티");
            assertThat(result.getType()).isEqualTo(ClothType.TOP);
            assertThat(result.getAttributes().get(0).value()).isEqualTo("파랑");
            assertThat(result.getAttributes().get(1).value()).isEqualTo("S");
            verify(clothRepository, times(1)).save(any(Cloth.class));
        }

        @Test
        @DisplayName("의상 등록 실패 - 속성ID가 존재하지 않을 경우")
        void create_fail() {
            //given
            UUID ownerId = UUID.randomUUID();
            UUID attributesId = UUID.randomUUID();
            ClothesCreateRequest request = new ClothesCreateRequest(ownerId, "후드티",
                ClothType.TOP,
                List.of(new ClothesAttributeDto(attributesId, "파랑"))
            );

            User mockUser = User.builder().id(ownerId).build();
            given(userRepository.findById(ownerId)).willReturn(Optional.of(mockUser));
            given(attributeRepository.findAllById(any())).willReturn(List.of()); // 속성 없음

            // when & then
            assertThatThrownBy(() -> sut.create(request,Optional.empty()))
                .isInstanceOf(AttributeNotFoundException.class)
                .hasMessageContaining("속성 확인 실패");

            verify(clothRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("의상 삭제 테스트")
    class DeleteCloth {

        @Test
        @DisplayName("삭제 성공")
        void delete_success() {
            //given
            UUID ownerId = UUID.randomUUID();
            UUID clothesId = UUID.randomUUID();
            Cloth clothes = Cloth.builder()
                .id(clothesId)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .name("후드티")
                .clothType(ClothType.TOP)
                .clothesImageUrl(null)
                .user(User.builder().id(ownerId).build())
                .build();

            given(clothRepository.findById(clothesId)).willReturn(Optional.of(clothes));

            //when
            sut.delete(clothesId);
            //then
            verify(clothRepository, times(1)).findById(clothesId);
            verify(clothRepository, times(1)).delete(clothes);
        }

        @Test
        @DisplayName("삭제 실패 - 존재하지 않는 의상")
        void delete_fail() {
            // given
            UUID id = UUID.randomUUID();
            given(clothRepository.findById(id)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> sut.delete(id))
                .isInstanceOf(ClothNotFoundException.class)
                .hasMessageContaining("옷 확인 실패");
            verify(clothRepository, times(1)).findById(id);
            verify(clothRepository, never()).delete(any());
        }
    }

    @Nested
    @DisplayName("의상 수정 테스트 - 이미지 없음")
    class UpdateCloth {

        @Test
        @DisplayName("수정 성공")
        void update_success() {
            //given
            UUID ownerId = UUID.randomUUID();
            UUID clothesId = UUID.randomUUID();
            UUID colorId = UUID.randomUUID();

            ClothesAttributeWithDefDto colorDto = new ClothesAttributeWithDefDto(colorId,
                "색상", List.of("빨강", "파랑"), "빨강");

            Attribute colorDef = Attribute.builder()
                .id(colorId)
                .name("색상")
                .selectableValues(List.of("빨강", "파랑"))
                .build();

            Cloth cloth = Cloth.builder()
                .id(clothesId)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .name("후드티")
                .clothType(ClothType.TOP)
                .clothesImageUrl(null)
                .user(User.builder().id(ownerId).build())
                .build();

            ClothesDto clothesDto = ClothesDto.builder()
                .id(clothesId)
                .ownerId(ownerId)
                .name("빨강 후드티")
                .imageUrl(null)
                .type(ClothType.TOP)
                .attributes(List.of(colorDto))
                .build();

            ClothesUpdateRequest request = new ClothesUpdateRequest(
                "빨강 후드티",
                ClothType.TOP,
                List.of(
                    new ClothesAttributeDto(colorId, "빨강")
                )
            );

            given(clothRepository.findByIdWithAttributes(clothesId)).willReturn(Optional.of(cloth));
            given(attributeRepository.findAllById(any())).willReturn(List.of(colorDef));
            given(mapper.toDto(any(Cloth.class),any())).willReturn(clothesDto);
            //when
            ClothesDto result=sut.update(clothesId,request,Optional.empty());

            //then
            assertThat(result.getName()).isEqualTo("빨강 후드티");
            assertThat(result.getType()).isEqualTo(ClothType.TOP);
            assertThat(result.getAttributes().get(0).value()).isEqualTo("빨강");
            verify(attributeRepository, times(1)).findAllById(any());
        }
    }
}
