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
 * In-memory database of FIPS 10 administration divisions.
 *
 * @author Sualeh Fatehi
 */
public final class FIPS10AdministrationDivisions {

  private static final CSVFormat FORMAT =
      CSVFormat.DEFAULT
          .builder()
          .setDelimiter(';')
          .setHeader()
          .setSkipHeaderRecord(true)
          .setIgnoreEmptyLines(true)
          .get();

  private static final Map<String, String> fips10AdministrationDivisionMap = new HashMap<>();

  /** Loads data from internal database. */
  static {
    try (CSVParser csvParser =
        new CSVParser(
            new UnicodeReader(
                FIPS10AdministrationDivisions.class
                    .getClassLoader()
                    .getResourceAsStream("fips10.data"),
                "UTF-8"),
            FORMAT)) {
      for (final CSVRecord record : csvParser) {
        final String code = record.get("code");
        if (code != null && code.length() == 4) {
          fips10AdministrationDivisionMap.put(code, record.get("name"));
        }
      }
    } catch (final IOException e) {
      throw new IllegalStateException("Cannot read data from internal database", e);
    }
  }

  /**
   * Looks up a FIPS-10 administration division name from the FIPS-10 administration division name.
   *
   * @param country Country
   * @param fips10AdministrationDivisionCode FIPS-10 administration division code
   * @return FIPS-10 administration division name, or null
   */
  public static String lookupFips10AdministrationDivisionName(
      final Country country, final String fips10AdministrationDivisionCode) {
    if (country != null && fips10AdministrationDivisionCode != null) {
      final String fips10FullAdministrationDivisionCode =
          country.getCode() + fips10AdministrationDivisionCode;
      return fips10AdministrationDivisionMap.get(fips10FullAdministrationDivisionCode);
    }
    return null;
  }
}
