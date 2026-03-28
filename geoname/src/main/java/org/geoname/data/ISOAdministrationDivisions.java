/*
 * Daylight Chart
 * http://sualeh.github.io/DaylightChart
 * Copyright (c) 2007-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package org.geoname.data;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.geoname.parser.UnicodeReader;

/**
 * In-memory database of ISO 3166-2 administrative divisions, loaded from {@code iso_adm.data}.
 *
 * <p>The data file is semicolon-delimited with five columns and a header row:
 *
 * <pre>
 *   country_code ; region_name ; region_type ; regional_code ; regional_number_code
 * </pre>
 *
 * <p>The map key is {@code "COUNTRY_CODE-REGIONAL_CODE"} (e.g. {@code "AF-BDS"}), and the value is
 * the region name (e.g. {@code "Badakhshān"}).
 *
 * @author Sualeh Fatehi
 */
public final class ISOAdministrationDivisions {

  private static final CSVFormat FORMAT =
      CSVFormat.DEFAULT
          .builder()
          .setDelimiter(';')
          .setHeader()
          .setSkipHeaderRecord(true)
          .setIgnoreEmptyLines(true)
          .get();

  private static final Map<String, String> isoAdministrationDivisionMap = new HashMap<>();

  /** Loads data from the internal database. */
  static {
    try (CSVParser csvParser =
        new CSVParser(
            new UnicodeReader(
                ISOAdministrationDivisions.class
                    .getClassLoader()
                    .getResourceAsStream("iso_adm.data"),
                "UTF-8"),
            FORMAT)) {
      for (final CSVRecord record : csvParser) {
        final String countryCode = record.get("country_code").trim();
        final String regionName = record.get("region_name").trim();
        final String regionalCode = record.get("regional_code").trim();
        if (!countryCode.isEmpty() && !regionalCode.isEmpty() && !regionName.isEmpty()) {
          isoAdministrationDivisionMap.put(countryCode + "-" + regionalCode, regionName);
        }
      }
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
}
