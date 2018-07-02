package org.syaku.springboot.web.utils;

import static org.junit.Assert.assertEquals;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.*;

import org.junit.Test;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Seok Kyun. Choi. 최석균 (Syaku)
 * @since 2018. 7. 2.
 */
public class ParameterUtilsTest {
  Map<String, String[]> params = new LinkedHashMap<>();

  @Test
  public void 기능_테스트() {
    // mapToString test
    params.put("page", new String[]{"1"});
    params.put("search", new String[]{"choi"});
    assertEquals(ParameterUtils.mapToString(params), "page=1&search=choi");
    params.put("search", new String[]{"choi","choi2"});
    assertEquals(ParameterUtils.mapToString(params), "page=1&search=choi&search=choi2");
    assertEquals(ParameterUtils.mapToString(Collections.emptyMap()), "");

    // stringToMap test
    // 문자열로 변경하여 비교한다.
    assertEquals(ParameterUtils.mapToString(ParameterUtils.stringToMap("page=1&search=choi&search=choi2")),
      "page=1&search=choi&search=choi2");

    assertEquals(ParameterUtils.mapToString(ParameterUtils.stringToMap("page=1&search="), true),
      "page=1&search=");
    assertEquals(ParameterUtils.mapToString(ParameterUtils.stringToMap("page=1&search=")),
      "page=1");

    assertEquals(ParameterUtils.stringToMap(""), Collections.emptyMap());
  }

  @Test
  public void 합집합_테스트() {
    // union test
    params.put("page", new String[]{"2"});
    params.put("search", new String[]{"choi"});
    params.put("mode", new String[]{"save"});

    Map<String, String[]> actual = new LinkedHashMap<>(params);
    actual.put("search", new String[]{});

    // 문자열로 변경하여 비교한다.
    assertEquals(ParameterUtils.mapToString(ParameterUtils.union(params, "page=2&search=&mode=save")),
      ParameterUtils.mapToString(actual));
    assertEquals(ParameterUtils.mapToString(ParameterUtils.union(null, "page=2&search=&mode=save")),
      ParameterUtils.mapToString(actual));
    assertEquals(ParameterUtils.mapToString(
      ParameterUtils.union(Collections.emptyMap(), "page=2&search=&mode=save")),
      ParameterUtils.mapToString(actual));
  }

  @Test
  public void 합치기_테스트() {
    params.put("page", new String[]{"1"});
    params.put("search", new String[]{"choi"});

    assertEquals(ParameterUtils.merge(params, "mode=save"), "page=1&search=choi&mode=save");
    assertEquals(ParameterUtils.merge(params, "page=1"), "page=1&search=choi");
    assertEquals(ParameterUtils.merge(params, "search="), "page=1");
    assertEquals(ParameterUtils.merge(params, "mode=save&search="), "page=1&mode=save");
    assertEquals(
      ParameterUtils.merge(params, "mode=save&search=", true), "page=1&search=&mode=save");

    assertEquals(ParameterUtils.merge(Collections.emptyMap(), "mode=save&search="), "mode=save");
    assertEquals(ParameterUtils.merge(null, "mode=save&search="), "mode=save");
  }

  @Test
  public void 꺼내기_테스트() {
    params.put("page", new String[]{"1"});
    params.put("search", new String[]{"choi"});

    assertEquals(ParameterUtils.pick(params, "page="), "page=1");
    assertEquals(ParameterUtils.pick(params, "page=&search=choi"), "page=1&search=choi");
    assertEquals(ParameterUtils.pick(params, "test=man&page=&search=choi2"), "test=man&page=1&search=choi2");
    assertEquals(ParameterUtils.pick(Collections.emptyMap(), "test=man&page=&search=choi"), "test=man&search=choi");
    assertEquals(ParameterUtils.pick(null, "test=man&page=&search=choi"), "test=man&search=choi");
  }
}


@Slf4j
final class ParameterUtils {
  private static String charset = "UTF-8";

  /**
   * {@link URLDecoder} 에 사용될 언어셋을 설정할 수 있다.
   * @param charset String default UTF-8
   */
  public static void setCharset(String charset) {
    if (!isEmpty(charset)) {
      ParameterUtils.charset = charset;
    }
  }

  /**
   * 값이 빈값인지 판단한다.
   * @param value 확인할 값
   * @return boolean false 빈값
   */
  private static boolean isEmpty(String value) {
    return value == null || value.length() == 0;
  }

  private static String decode(String value) throws UnsupportedEncodingException {
    return URLDecoder.decode(value, ParameterUtils.charset);
  }

  private static List<String> add(List<String> values, String value) {
    if (value != null && value.length() > 0) {
      values.add(value);
    }
    return values;
  }

  /**
   * 문자열 파라메터를 맵 형식으로 만들어 반환한다. 순서를 유지하기 위해 {@link LinkedHashMap} 을 사용했다.
   * @param params string parameter type
   * @return parameter map
   */
  public static Map<String, String[]> stringToMap(String params) {
    if (params == null || params.length() == 0) {
      return Collections.emptyMap();
    }
    Map<String, List<String>> store = new LinkedHashMap<>();
    String[] pairs = params.split("&");

    try {
      for (String pair : pairs) {
        int idx = pair.indexOf("=");
        String name = decode(pair.substring(0, idx));
        String value = decode(pair.substring(idx + 1));


        List<String> values = store.containsKey(name) ? store.get(name) : new LinkedList<>();
        store.put(name, add(values, value));
      }
    } catch (UnsupportedEncodingException e) {
      log.error(e.getMessage());
    }

    Map<String, String[]> result = new LinkedHashMap<>();

    for (Map.Entry<String, List<String>> values: store.entrySet()) {
      result.put(values.getKey(), values.getValue().toArray(new String[values.getValue().size()]));
    }

    return result;
  }

  /**
   * @param target 대상이될 파라메터 맵
   * @return string parameter
   */
  public static String mapToString(Map<String, String[]> target) {
    return mapToString(target, false);
  }

  /**
   * 파라메터 맵을 문자열로 만들어 반환한다. allowEmpty 가 true 인 경우 빈값도 이름을 만든다. 기본적으로 false 이다.
   * @param target 대상이될 파라메터맵
   * @param allowEmpty 빈값을 만들지 여부 기본적으로 만들지 않는 다.
   * @return parameter string
   */
  public static String mapToString(Map<String, String[]> target, boolean allowEmpty) {
    if (target == null || target.isEmpty()) {
      return "";
    }

    StringBuilder result = new StringBuilder();
    for (Map.Entry<String, String[]> map : target.entrySet()) {
      String[] values = map.getValue();
      if (!allowEmpty && (values == null || values.length == 0)) {
        continue;
      }

      if (values == null || values.length == 0) {
        result.append(map.getKey()).append("=&");
        continue;
      }

      for (String value : values) {
        result.append(map.getKey()).append("=").append(value).append("&");
      }
    }

    return result.toString().replaceAll("&$", "");
  }

  /**
   * 두개의 맵을 병합한다.
   * @param target 원래 맵
   * @param value 새로운 맵
   * @return parameter map
   */
  public static Map<String, String[]> union(Map<String, String[]> target, String value) {
    Map<String, String[]> result;

    if (target == null || target.isEmpty()) {
      result = new LinkedHashMap<>();
    } else {
      result = new LinkedHashMap<>(target);
    }
    result.putAll(stringToMap(value));

    return result;
  }

  /**
   * 대상이 되는 파라매터 맵에 문자열 파라메터 구문을 이용하여 문자열 파라메터를 반환한다.
   * @param target parameter map
   * @param query parameter query string
   * @return parameter string
   */
  public static String merge(Map<String, String[]> target, String query) {
    return merge(target, query, false);
  }

  /**
   * 대상이 되는 파라매터 맵에 문자열 파라메터 구문을 이용하여 문자열 파라메터를 반환한다.
   * 빈값을 가진 파라메터는 대상이 되는 파라메터에서 삭제한다. allowEmpty = false 인 경우에 해당한다.
   * value 파라메터 일부가 target 파라메터에 있는 경우 값을 수정한다.
   * value 파라메터 일부가 target 파라메터에 없는 경우 이름과 값을 추가한다.
   *
   * (page=1&search=choi , page=2&search=) return "page=2"
   * (page=1&search=choi , page=2&search=&mode=save) return "page=2&mode=save"
   * (page=1&search=choi , mode=save) return "page=1&search=choi&mode=save"
   * @param target parameter map
   * @param query parameter query string
   * @param allowEmpty 빈값을 가진 파라메터를 제거할지 여부를 설정한다. mapToString 에서 사용된다.
   * @return parameter string
   */
  public static String merge(Map<String, String[]> target, String query, boolean allowEmpty) {
    return mapToString(union(target, query), allowEmpty);
  }

  private static String mapToLog(Map<String, String[]> target) {
    StringBuilder builder = new StringBuilder();
    for (Map.Entry<String, String[]> map : target.entrySet()) {
      if (map.getValue() == null || map.getValue().length == 0) {
        builder.append(map.getKey()).append("=&");
      } else {
        for (String value : map.getValue()) {
          builder.append(map.getKey()).append("=").append(value).append("&");
        }
      }
    }

    String result = builder.toString().replaceAll("&$", "");
    log.debug(result);
    return result;
  }

  /**
   * 대상이 되는 파라메터 맵에서 쿼리에 맞는 파라메터만 추출한다.
   *
   * (page=1&search=choi , page=) return "page=2"
   * (page=1&search=choi , page=2&mode=save) return "page=2&mode=save"
   * (page=1&search=choi , mode=save) return "mode=save"
   * @param target parameter map
   * @param query parameter query string
   * @return parameter string
   */
  public static String pick(Map<String, String[]> target, String query) {
    Map<String, String[]> result = stringToMap(query);

    if (target == null || target.isEmpty()) {
      return mapToString(result);
    }

    for (Map.Entry<String, String[]> map : result.entrySet()) {
      if (map.getValue() == null || map.getValue().length == 0) {
        result.put(map.getKey(), target.get(map.getKey()));
      }
    }

    return mapToString(result);
  }
}
