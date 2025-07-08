package com.codeit.weatherwear.domain.clothes.repository;


import static org.assertj.core.api.Assertions.assertThat;

import com.codeit.weatherwear.domain.clothes.entity.Attribute;
import com.codeit.weatherwear.domain.clothes.entity.Cloth;
import com.codeit.weatherwear.domain.clothes.entity.ClothType;
import com.codeit.weatherwear.domain.clothes.entity.ClothWithAttributes;
import com.codeit.weatherwear.domain.user.entity.User;
import com.codeit.weatherwear.domain.user.repository.UserRepository;
import com.codeit.weatherwear.global.config.JpaConfig;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Slice;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
@Import(JpaConfig.class)
public class ClothRepositoryTest {

  @Autowired
  private ClothRepository sut;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private AttributeRepository attributeRepository;
  User user;
  Cloth thinScarf;
  Cloth whiteScarf;
  Cloth redScarf;
  Attribute color;
  Attribute thick;

  @BeforeEach
  void setUp() throws InterruptedException {
    user = User.builder()
        .email("user@test.com")
        .name("user")
        .password("user1234!")
        .build();
    userRepository.save(user);

    color = attributeRepository.save(Attribute.builder()
        .name("색상")
        .selectableValues(List.of("빨강", "파랑", "하양"))
        .build());

    thick = attributeRepository.save(Attribute.builder()
        .name("두께")
        .selectableValues(List.of("두꺼움", "얇음")).build());

    ClothWithAttributes white = ClothWithAttributes.builder()
        .value("하양")
        .attribute(color)
        .build();

    ClothWithAttributes red = ClothWithAttributes.builder()
        .value("빨강")
        .attribute(color)
        .build();

    ClothWithAttributes thin = ClothWithAttributes.builder()
        .value("얇음")
        .attribute(thick)
        .build();

    thinScarf = Cloth.builder()
        .name("얇은 스카프")
        .clothType(ClothType.SCARF)
        .user(user)
        .build();
    thinScarf.addAttribute(white);
    thinScarf.addAttribute(thin);

    whiteScarf = Cloth.builder()
        .name("하얀 스카프")
        .clothType(ClothType.SCARF)
        .user(user)
        .build();
    whiteScarf.addAttribute(white);

    redScarf = Cloth.builder()
        .name("빨간 스카프")
        .clothType(ClothType.SCARF)
        .user(user)
        .build();
    redScarf.addAttribute(red);

    // todo: 테스트 통과를 위해 수정한 부분
    sut.save(thinScarf);
    Thread.sleep(10);
    sut.save(whiteScarf);
    Thread.sleep(10);
    sut.save(redScarf);
  }

  @Test
  @DisplayName("저장 성공")
  void save_success() {
    // given
    Cloth clothes = Cloth.builder()
        .name("후드티")
        .clothType(ClothType.TOP)
        .user(user)
        .build();

    ClothWithAttributes attr = ClothWithAttributes.builder()
        .value("파랑")
        .attribute(color)
        .build();

    clothes.addAttribute(attr);

    // when
    Cloth saved = sut.save(clothes);

    // then
    assertThat(saved.getId()).isNotNull();
    assertThat(saved.getName()).isEqualTo("후드티");
    assertThat(saved.getClothesWithAttributes()).hasSize(1);
    assertThat(saved.getClothesWithAttributes().get(0).getValue()).isEqualTo("파랑");
    assertThat(saved.getUser().getId()).isEqualTo(user.getId());
  }

  @Test
  @DisplayName("옷 조회 성공 - hasNext = false")
  void search_success() {
    //given
    int limit = 20;

    //when
    Slice<Cloth> clothList = sut.searchCloths(null, null, limit, ClothType.SCARF, user.getId());
    List<Cloth> content = clothList.getContent();
    //then
    assertThat(clothList.hasNext()).isFalse();
    assertThat(content).hasSize(3);
    assertThat(content.get(0)).isEqualTo(redScarf);
    assertThat(content.get(1)).isEqualTo(whiteScarf);
    assertThat(content.get(2)).isEqualTo(thinScarf);
  }

  @Test
  @DisplayName("옷 조회 성공 - hasNext=true")
  void search_success_hasNext() {
    //given
    int limit = 2;

    //when
    Slice<Cloth> clothList = sut.searchCloths(null, null, limit, ClothType.SCARF, user.getId());
    List<Cloth> content = clothList.getContent();

    //then
    assertThat(clothList.hasNext()).isTrue();
    assertThat(content).hasSize(2);
    assertThat(content.get(0)).isEqualTo(redScarf);
    assertThat(content.get(1)).isEqualTo(whiteScarf);
  }

  @Test
  @DisplayName("속성 조회 성공 - 커서 존재")
  void search_success_cursor() {
    //given
    int limit = 2;
    Instant cursor = thinScarf.getCreatedAt();

    //when
    Slice<Cloth> clothList = sut.searchCloths(cursor, null, limit, ClothType.SCARF, user.getId());
    List<Cloth> content = clothList.getContent();

    //then
    assertThat(clothList.hasNext()).isTrue();
    assertThat(content).hasSize(2);
    assertThat(content.get(0)).isEqualTo(redScarf);
    assertThat(content.get(1)).isEqualTo(whiteScarf);
  }

}
