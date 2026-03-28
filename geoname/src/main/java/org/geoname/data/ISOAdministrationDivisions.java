/*
 * Daylight Chart
 * http://sualeh.github.io/DaylightChart
 * Copyright (c) 2007-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package org.geoname.data;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.geoname.parser.UnicodeReader;

/**
 * In-memory database of ISO 3166-2 administrative divisions, loaded from {@code iso_adm.data}.
 *
 * <p>The data file is semicolon-delimited with five fields per line:
 *
 * <pre>
 *   COUNTRY_SHORT_CODE ; REGION_NAME ; REGION_TYPE ; REGIONAL_CODE ; REGIONAL_NUMBER_CODE
 * </pre>
 *
 * <p>Region name and regional code fields are surrounded by double-quotes in the file. The map key
 * is {@code "COUNTRY_SHORT_CODE-REGIONAL_CODE"} (e.g. {@code "AF-BDS"}), and the value is the
 * unquoted region name (e.g. {@code "Badakhshān"}).
 *
 * @author Sualeh Fatehi
 */
public final class ISOAdministrationDivisions {

  private static final Map<String, String> isoAdministrationDivisionMap = new HashMap<>();

  /** Loads data from the internal database. */
  static {
    try (BufferedReader reader =
        new BufferedReader(
            new UnicodeReader(
                ISOAdministrationDivisions.class
                    .getClassLoader()
                    .getResourceAsStream("iso_adm.data"),
                "UTF-8"))) {
      reader
          .lines()
          .map(line -> line.split(";"))
          .filter(fields -> fields.length == 5)
          .forEach(
              fields -> {
                final String countryCode = fields[0].trim();
                final String regionName = unquote(fields[1]);
                final String regionalCode = unquote(fields[3]);
                if (!countryCode.isEmpty() && !regionalCode.isEmpty() && !regionName.isEmpty()) {
                  final String key = countryCode + "-" + regionalCode;
                  isoAdministrationDivisionMap.put(key, regionName);
                }
              });
    } catch (final IOException e) {
      throw new IllegalStateException("Cannot read data from internal database", e);
    }
  }

  /**
   * Looks up an ISO 3166-2 region name by country and regional code.
   *
   * @param isoRegionalCode ISO 3166-2 regional code (e.g. {@code "BDS"}); if {@code null}, returns
   *     {@code null}
   * @return region name, or {@code null} if not found
   */
  public static String lookupIsoRegionName(final String isoRegionalCode) {
    if (isoRegionalCode == null) {
      return null;
    }
    return isoAdministrationDivisionMap.get(isoRegionalCode);
  }

  private static String unquote(final String value) {
    if (value == null) {
      return "";
    }
    return value.trim().replace("\"", "");
  }
}
