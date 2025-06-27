package com.codeit.weatherwear.domain.ootd.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;

import com.codeit.weatherwear.domain.feed.entity.Feed;
import com.codeit.weatherwear.domain.location.entity.Location;
import com.codeit.weatherwear.domain.ootd.dto.response.OotdDto;
import com.codeit.weatherwear.domain.ootd.entity.Ootd;
import com.codeit.weatherwear.domain.ootd.mapper.OotdMapper;
import com.codeit.weatherwear.domain.ootd.repository.OotdRepository;
import com.codeit.weatherwear.domain.user.entity.Gender;
import com.codeit.weatherwear.domain.user.entity.Role;
import com.codeit.weatherwear.domain.user.entity.User;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class OotdServiceImplTest {

  @Mock
  private OotdRepository ootdRepository;

  @Mock
  private OotdMapper ootdMapper;

  @InjectMocks
  private OotdServiceImpl ootdService;

  private Feed mockFeed;

  private List<UUID> clothIds;
  private UUID clothId1;
  private UUID clothId2;

  private List<Ootd> ootdList;
  private Ootd mockOotd1;
  private OotdDto mockOotdDto1;
  private Ootd mockOotd2;
  private OotdDto mockOotdDto2;

  private UUID authorId;
  private String mockContent;

  private Location mockLocation;
  private User mockAuthor;
  private UUID feedId;

  @BeforeEach
  void setUp() {

    clothId1 = UUID.randomUUID();
    clothId2 = UUID.randomUUID();
    clothIds = List.of(clothId1, clothId2);

    mockOotd1 = mock(Ootd.class);
    mockOotdDto1 = OotdDto.builder()
        .clothesId(clothId1)
        .name("cloth1")
        .imageUrl(null)
        .type("상의")
        .attributes(null)
        .build();
    mockOotd2 = mock(Ootd.class);
    mockOotdDto1 = OotdDto.builder()
        .clothesId(clothId2)
        .name("cloth2")
        .imageUrl(null)
        .type("하의")
        .attributes(null)
        .build();
    ootdList = List.of(mockOotd1, mockOotd2);

    mockLocation = new Location(37.513068, 127.102570, 961159, 1953082, "서울 송파구 신천동");

    authorId = UUID.randomUUID();
    mockAuthor = User.builder()
        .id(authorId)
        .email("test@example.com")
        .name("홍길동")
        .password("!password1234")
        .role(Role.USER)
        .locked(false)
        .gender(Gender.FEMALE)
        .birthDate(LocalDate.of(2000, 1, 1))
        .temperatureSensitivity(10)
        .profileImageUrl(null)
        .location(mockLocation)
        .createdAt(Instant.now())
        .updatedAt(Instant.now())
        .build();

    mockContent = "Mock Feed Content";

    feedId = UUID.randomUUID();
    mockFeed = Feed.builder()
        .author(mockAuthor)
        .content(mockContent)
        .commentCount(0)
        .likeCount(0)
        .build();
    ReflectionTestUtils.setField(mockFeed, "id", feedId);

  }

  @Test
  @DisplayName("전달받은 Feed 객체와 clothIds를 통해 Ootd 등록을 성공한다.")
  void createOotd_success() {
    // given
    given(ootdMapper.toEntity(mockFeed, clothId1)).willReturn(mockOotd1);
    given(ootdMapper.toEntity(mockFeed, clothId2)).willReturn(mockOotd2);
    given(ootdRepository.saveAll(ootdList)).willReturn(ootdList);
    given(ootdMapper.toDto(mockOotd1)).willReturn(mockOotdDto1);
    given(ootdMapper.toDto(mockOotd2)).willReturn(mockOotdDto2);

    // when
    List<OotdDto> result = ootdService.createOotdList(mockFeed, clothIds);

    // then
    then(ootdMapper).should().toEntity(mockFeed, clothId1);
    then(ootdMapper).should().toEntity(mockFeed, clothId2);
    then(ootdRepository).should().saveAll(ootdList);
    then(ootdMapper).should(times(1)).toDto(mockOotd1);
    then(ootdMapper).should(times(1)).toDto(mockOotd2);

    assertThat(result)
        .hasSize(2)
        .containsExactly(mockOotdDto1, mockOotdDto2);

  }

  @Test
  @DisplayName("전달받은 clothIds가 빈 리스트이다.")
  void createOotd_no_content() {
    // given
    List<UUID> noClotheIds = List.of();

    // when
    List<OotdDto> result = ootdService.createOotdList(mockFeed, noClotheIds);

    // then
    then(ootdMapper).shouldHaveNoInteractions();
    then(ootdRepository).shouldHaveNoInteractions();

    assertThat(result)
        .isNotNull()
        .isEmpty();
  }

  @Test
  @DisplayName("feedId를 받아 성공적으로 값을 가져온다.")
  void findOotd_success() {
    // given
    given(ootdRepository.findByFeedId(feedId)).willReturn(ootdList);
    given(ootdMapper.toDto(mockOotd1)).willReturn(mockOotdDto1);
    given(ootdMapper.toDto(mockOotd2)).willReturn(mockOotdDto2);

    // when
    List<OotdDto> result = ootdService.findOotdByFeedId(feedId);

    // then
    then(ootdRepository).should(times(1)).findByFeedId(feedId);
    then(ootdMapper).should(times(1)).toDto(mockOotd1);
    then(ootdMapper).should(times(1)).toDto(mockOotd2);

    assertThat(result)
        .hasSize(2)
        .containsExactly(mockOotdDto1, mockOotdDto2);
  }

  @Test
  @DisplayName("feedId를 받았을 때, OOTD가 없을 때 빈 리스트를 응답한다.")
  void findOotd_no_content() {
    // given
    given(ootdRepository.findByFeedId(feedId)).willReturn(List.of());

    // when
    List<OotdDto> result = ootdService.findOotdByFeedId(feedId);

    // then
    then(ootdRepository).should(times(1)).findByFeedId(feedId);
    then(ootdMapper).shouldHaveNoInteractions();

    assertThat(result)
        .isNotNull()
        .isEmpty();
  }


}