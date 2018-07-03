package org.syaku.springboot.web.utils;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author Seok Kyun. Choi. 최석균 (Syaku)
 * @since 2018. 7. 3.
 */
public final class JsonUtils {
  private static final ObjectMapper mapper = new ObjectMapper();
  private JsonUtils() {
  }

  public static String toString(Object json) throws JsonProcessingException {
    return mapper.writeValueAsString(json);
  }

  public static <K,T> Map<K, T> toMap(String json) throws IOException {
    return mapper.readValue(json, new TypeReference<Map<K, T>>() {});
  }

  public <T> List<T> toList(String json) throws IOException {
    return mapper.readValue(json, new TypeReference<List<T>>() {});
  }
}
