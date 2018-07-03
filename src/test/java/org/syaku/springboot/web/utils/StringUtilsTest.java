package org.syaku.springboot.web.utils;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * @author Seok Kyun. Choi. 최석균 (Syaku)
 * @since 2018. 7. 3.
 */
public class StringUtilsTest {
  @Test
  public void test() {
    assertEquals(StringUtils.abbreviate("ㄱㄴ", 1, ""), "ㄱ");
    assertEquals(StringUtils.abbreviate("가 나 다 1 2 3 4", 2, ""), "가 ");
    assertEquals(StringUtils.abbreviate("가123ㄱ나", 5, ""), "가123ㄱ");
  }
}
