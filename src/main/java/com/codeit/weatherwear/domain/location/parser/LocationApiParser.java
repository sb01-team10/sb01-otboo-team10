package com.codeit.weatherwear.domain.location.parser;

import com.codeit.weatherwear.domain.location.exception.AddressJsonParseException;
import com.codeit.weatherwear.domain.location.exception.MissingAddressFieldException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class LocationApiParser {

  private static final List<String> ADDRESS_DEPTH_NAME_LIST = List.of(
      "region_1depth_name", "region_2depth_name", "region_3depth_name"
  );

  public List<String> parse(String responseBody) {
    try {
      ObjectMapper mapper = new ObjectMapper();
      // 응답 JSON에서 documents[0].address 노드를 추출
      JsonNode root = mapper.readTree(responseBody);
      JsonNode address = root.path("documents").get(0).path("address");

      if (address.isMissingNode()) {
        log.warn("Address Node Not Found");
        throw new MissingAddressFieldException();
      }

      // List 화 하고자 하는 데이터들만 가져와서 리스트에 넣어 반환
      return ADDRESS_DEPTH_NAME_LIST.stream()
          .map(key -> address.path(key).asText(""))
          .collect(Collectors.toList());
    } catch (JsonProcessingException jpe) {
      log.error("Failed JSON parse: {}", jpe.getMessage());
      throw new AddressJsonParseException();
    }
  }

}
