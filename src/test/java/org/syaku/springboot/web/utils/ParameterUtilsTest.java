package org.syaku.springboot.web.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Test;

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

    assertEquals(ParameterUtils.merge(params, "?mode=save"), "?page=1&search=choi&mode=save");
    assertEquals(ParameterUtils.merge(params, "&search="), "&page=1");
    assertEquals(ParameterUtils.merge(null, "&mode=save&search="), "&mode=save");
    assertEquals(ParameterUtils.merge(null, "?mode=save&search="), "?mode=save");
    assertEquals(ParameterUtils.merge(null, "?"), "");
    assertEquals(ParameterUtils.merge(null, "&"), "");
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

    assertEquals(ParameterUtils.pick(params, "?page="), "?page=1");
    assertEquals(ParameterUtils.pick(params, "&page="), "&page=1");
    assertEquals(ParameterUtils.pick(null, "?test=man&page=&search=choi"), "?test=man&search=choi");
    assertEquals(ParameterUtils.pick(null, "?"), "");
    assertEquals(ParameterUtils.pick(null, "&"), "");
  }
}
