package org.syaku.springboot.web.utils;

import java.text.DecimalFormat;

import lombok.Getter;

/**
 * @author Seok Kyun. Choi. 최석균 (Syaku)
 * @since 2018. 7. 3.
 */

public final class UnitConverter {
  private UnitConverter() {

  }

  public enum DataUnit {
    B("B", 1), KB("KB", 1024), MB("MB", 1048576), GB("GB", 1073741824), TB("TB", 1099511627776L);

    @Getter
    private String unit;
    @Getter
    private long size;

    DataUnit(String unit, long size) {
      this.unit = unit;
      this.size = size;
    }
  }

  public static String byteToUnit(long byteSize, DataUnit unit) {
    double result = byteSize;

    switch (unit.unit) {
      case "B" :
      break;
      case "KB" :
        result =  result / 1024;
      break;
      case "MB" :
        result =  result / 1024 / 1024;
      break;
      case "GB" :
        result =  result / 1024 / 1024 / 1024;
      break;
      case "TB" :
        result =  result / 1024 / 1024 / 1024 / 1024;
      break;
      default :
        if (DataUnit.TB.size <= byteSize) {
          return byteToUnit(byteSize, DataUnit.TB);
        } else if (DataUnit.GB.size <= byteSize) {
          return byteToUnit(byteSize, DataUnit.GB);
        } else if (DataUnit.MB.size <= byteSize) {
          return byteToUnit(byteSize, DataUnit.MB);
        } else if (DataUnit.KB.size <= byteSize) {
          return byteToUnit(byteSize, DataUnit.KB);
        } else {
          return byteToUnit(byteSize, DataUnit.B);
        }
    }

    return new DecimalFormat("#.##").format(result) + " " + unit.unit;
  }
}