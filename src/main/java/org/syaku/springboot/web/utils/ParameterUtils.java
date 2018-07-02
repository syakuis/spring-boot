package org.syaku.springboot.web.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.*;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Seok Kyun. Choi. 최석균 (Syaku)
 * @since 2018. 7. 2.
 */

@Slf4j
public final class ParameterUtils {
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
  static Map<String, String[]> stringToMap(String params) {
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
//      log.error(e.getMessage());
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
  static String mapToString(Map<String, String[]> target) {
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
  static Map<String, String[]> union(Map<String, String[]> target, String value) {
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
//    log.debug(result);
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
