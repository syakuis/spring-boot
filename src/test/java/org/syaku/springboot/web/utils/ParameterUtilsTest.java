package org.syaku.springboot.web.utils;

import static org.junit.Assert.assertEquals;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Test;

import lombok.extern.slf4j.Slf4j;

/**
 * http request parameter 를 제어하는 클래스 테스트
 * merge : 두개의 파라메터를 병합한다.
 * pick : 대상이 되는 파라메터에서 해당 요소만 가져온다.
 * @author Seok Kyun. Choi. 최석균 (Syaku)
 * @since 2018. 6. 29.
 */
public class ParameterUtilsTest {
  @Test
  public void 병합_테스트() {
    Map<String, String> params = new LinkedHashMap<>();
    params.put("page", "1");
    params.put("search", "choi");

    assertEquals(ParameterUtils.mapToString(params), "page=1&search=choi");
    assertEquals(ParameterUtils.stringToMap("page=1&search=choi"), params);

    Map<String, String> actual = new LinkedHashMap<>();
    actual.put("page", "2");
    actual.put("search", "");
    actual.put("mode", "save");
    assertEquals(ParameterUtils.union(params, "page=2&search=&mode=save"), actual);

    assertEquals(ParameterUtils.merge(params, "mode=save"), "page=1&search=choi&mode=save");
    assertEquals(ParameterUtils.merge(params, "page=1"), "page=1&search=choi");
    assertEquals(ParameterUtils.merge(params, "search="), "page=1");
    assertEquals(ParameterUtils.merge(params, "mode=save&search="), "page=1&mode=save");
    assertEquals(
      ParameterUtils.merge(params, "mode=save&search=", true), "page=1&search=&mode=save");
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

  /**
   * 문자열 파라메터를 맵 형식으로 만들어 반환한다. 순서를 유지하기 위해 {@link LinkedHashMap} 을 사용했다.
   * @param params string parameter type
   * @return parameter map
   */
  public static Map<String, String> stringToMap(String params) {
    Map<String, String> result = new LinkedHashMap<>();
    String[] pairs = params.split("&");

    try {
      for (String pair : pairs) {
        int idx = pair.indexOf("=");
        String value = URLDecoder.decode(pair.substring(idx + 1), ParameterUtils.charset);
        result.put(URLDecoder.decode(pair.substring(0, idx), ParameterUtils.charset), value);
      }
    } catch (UnsupportedEncodingException e) {
      log.error(e.getMessage());
    }

    return result;
  }

  /**
   * @param aParams 대상이될 파라메터 맵
   * @return string parameter
   */
  public static String mapToString(Map<String, String> aParams) {
    return mapToString(aParams, false);
  }

  /**
   * 파라메터 맵을 문자열로 만들어 반환한다. allowEmpty 가 true 인 경우 빈값도 이름을 만든다. 기본적으로 false 이다.
   * @param aParams 대상이될 파라메터맵
   * @param allowEmpty 빈값을 만들지 여부 기본적으로 만들지 않는 다.
   * @return parameter string
   */
  public static String mapToString(Map<String, String> aParams, boolean allowEmpty) {
    if (aParams.isEmpty()) {
      return "";
    }

    StringBuilder result = new StringBuilder();
    for (Map.Entry<String, String> param : aParams.entrySet()) {
      String value = param.getValue();
      if (!allowEmpty && isEmpty(value)) {
        continue;
      }
      result.append(param.getKey()).append("=").append(value).append("&");
    }

    return result.toString().replaceAll("&$", "");
  }

  /**
   * 두개의 맵을 병합한다.
   * @param target 원래 맵
   * @param value 새로운 맵
   * @return parameter map
   */
  public static Map<String, String> union(Map<String, String> target, String value) {
    if (target.isEmpty()) {
      return Collections.emptyMap();
    }

    Map<String, String> result = new LinkedHashMap<>(target);
    result.putAll(stringToMap(value));

    return result;
  }

  /**
   * 대상이 되는 파라매터 맵에 문자열 파라메터 구문을 이용하여 문자열 파라메터를 반환한다.
   * @param target parameter map
   * @param value parameter string
   * @return parameter string
   */
  public static String merge(Map<String, String> target, String value) {
    return merge(target, value, false);
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
   * @param value parameter string
   * @param allowEmpty mapToString 에서 사용될 매개변수
   * @return parameter string
   */
  public static String merge(Map<String, String> target, String value, boolean allowEmpty) {
    return mapToString(union(target, value), allowEmpty);
  }
}
