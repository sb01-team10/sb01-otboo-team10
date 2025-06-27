package com.codeit.weatherwear.domain.ootd.service.impl;

import com.codeit.weatherwear.domain.feed.entity.Feed;
import com.codeit.weatherwear.domain.ootd.dto.response.OotdDto;
import com.codeit.weatherwear.domain.ootd.entity.Ootd;
import com.codeit.weatherwear.domain.ootd.mapper.OotdMapper;
import com.codeit.weatherwear.domain.ootd.repository.OotdRepository;
import com.codeit.weatherwear.domain.ootd.service.OotdService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class OotdServiceImpl implements OotdService {

  private final OotdRepository ootdRepository;
  private final OotdMapper ootdMapper;

  @Transactional
  @Override
  public List<OotdDto> createOotdList(Feed feed, List<UUID> clothIds) {

    if (clothIds == null || clothIds.isEmpty()) {
      return List.of();
    }

//    List<Clothes> clothesList = clothIds.stream()
//        .map(clothId -> clothesRepository.findById(clothId).orElseThrow()).toList();

    // todo: 추후 Clothes 객체가 있으면 위 주석처리 된 옷 객체로 바꾸면 됨!
    List<Ootd> ootdList = clothIds.stream().map(
            clothId -> ootdMapper.toEntity(feed, clothId)
        )
        .toList();

    List<Ootd> savedList = ootdRepository.saveAll(ootdList);

    return savedList.stream().map(ootdMapper::toDto).toList();
  }

  @Transactional
  @Override
  public List<OotdDto> findOotdByFeedId(UUID feedId) {

    List<Ootd> ootds = ootdRepository.findByFeedId(feedId);

    return ootds.stream().map(ootdMapper::toDto).toList();
  }

  @Transactional
  @Override
  public List<OotdDto> deleteOotdByFeedId(UUID feedId) {

    List<Ootd> ootds = ootdRepository.findByFeedId(feedId);
    ootdRepository.deleteAll(ootds);

    return ootds.stream().map(ootdMapper::toDto).toList();
  }
}
