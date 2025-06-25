package com.codeit.weatherwear.domain.clothes.repository;

import static org.assertj.core.api.Assertions.*;

import com.codeit.weatherwear.domain.clothes.entity.Attributes;
import com.codeit.weatherwear.global.config.JpaConfig;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
@Import({JpaConfig.class})
class AttributesRepositoryTest {

    @Autowired
    private AttributesRepository sut;

    @Test
    @DisplayName("저장 성공")
    void save_success() {
        //given
        Attributes attributes = Attributes.builder()
            .name("색상")
            .selectableValues(List.of("빨강", "파랑")).build();
        //when
        Attributes result = sut.save(attributes);
        //then
        assertThat(result.getName()).isEqualTo("색상");
        assertThat(result.getSelectableValues()).containsExactly("빨강", "파랑");
    }
}