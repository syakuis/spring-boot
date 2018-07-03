package org.syaku.springboot.web.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Seok Kyun. Choi. 최석균 (Syaku)
 * @since 2018. 7. 3.
 */
@Slf4j
public final class StringUtils {
  private static String charset = "UTF-8";

  /**
   * {@link URLDecoder} 에 사용될 언어셋을 설정할 수 있다.
   * @param charset String default UTF-8
   */
  public static void setCharset(String charset) {
    if (charset == null || charset.length() == 0) {
      StringUtils.charset = charset;
    }
  }

  public static String abbreviate(String text, int limit, String ending) {
    return abbreviate(text, limit, ending, charset);
  }

  public static String abbreviate(String text, int limit, String ending, String charset) {
    if (limit < 1 || text == null || text.length() == 0) {
      return text;
    }

    try {
      int aLimit = limit - 1;
      String aText = new String(text.getBytes(), charset);
      int length = aText.length();

      if (aLimit > length) {
        return aText;
      }

      StringBuffer string = new StringBuffer();
      for (int i = 0; i < length; i++) {
        string.append(aText.charAt(i));
        if (i == aLimit) {
          break;
        }
      }

      if (ending != null) {
        string.append(ending);
      }
      return string.toString();
    } catch(UnsupportedEncodingException e) {
      log.error(e.getMessage(), e);
      return text;
    }
  }
}
