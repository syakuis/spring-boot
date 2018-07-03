package org.syaku.springboot.web.utils;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * @author Seok Kyun. Choi. 최석균 (Syaku)
 * @since 2018. 7. 2.
 */
public class UnitConverterTest {

  @Test
  public void 파일사이즈() {
    assertEquals(UnitConverter.byteToUnit(1099511627776L, UnitConverter.DataUnit.TB), "1 TB");
    assertEquals(UnitConverter.byteToUnit(1099511627776L, UnitConverter.DataUnit.GB), "1024 GB");
    assertEquals(UnitConverter.byteToUnit(1099511627776L, UnitConverter.DataUnit.MB), "1048576 MB");
    assertEquals(UnitConverter.byteToUnit(1099511627776L, UnitConverter.DataUnit.KB), "1073741824 KB");
    assertEquals(UnitConverter.byteToUnit(1099511627776L, UnitConverter.DataUnit.B), "1099511627776 B");

    assertEquals(UnitConverter.byteToUnit(10995116277L, UnitConverter.DataUnit.TB), "0.01 TB");
    assertEquals(UnitConverter.byteToUnit(10995116277L, UnitConverter.DataUnit.GB), "10.24 GB");
    assertEquals(UnitConverter.byteToUnit(10995116277L, UnitConverter.DataUnit.MB), "10485.76 MB");
    assertEquals(UnitConverter.byteToUnit(10995116277L, UnitConverter.DataUnit.KB), "10737418.24 KB");
    assertEquals(UnitConverter.byteToUnit(10995116277L, UnitConverter.DataUnit.B), "10995116277 B");
  }
}
