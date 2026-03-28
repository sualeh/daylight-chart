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
 * Unified in-memory registry of administrative areas, loaded from both the FIPS 10 ({@code
 * fips10.data}) and ISO 3166-2 ({@code iso_adm.data}) data files.
 *
 * <p>FIPS 10 entries use 4-character keys (e.g. {@code "US01"}). ISO 3166-2 entries use hyphenated
 * keys (e.g. {@code "US-AL"}). The key spaces are disjoint, so both sets can coexist in one map.
 *
 * @author Sualeh Fatehi
 */
public final class AdministrativeAreas {

  private static final CSVFormat FIPS10_FORMAT =
      CSVFormat.DEFAULT
          .builder()
          .setDelimiter(';')
          .setHeader()
          .setSkipHeaderRecord(true)
          .setIgnoreEmptyLines(true)
          .get();

  private static final CSVFormat ISO_FORMAT =
      CSVFormat.DEFAULT
          .builder()
          .setDelimiter(';')
          .setHeader()
          .setSkipHeaderRecord(true)
          .setIgnoreEmptyLines(true)
          .get();

  private static final Map<String, AdministrativeArea> administrativeAreaMap = new HashMap<>();

  /** Loads both data files into the in-memory map. */
  static {
    loadFips10();
    loadIso3166();
  }

  /**
   * Looks up an administrative area by its full code.
   *
   * @param code Full lookup code (e.g. {@code "US-AL"} or {@code "US01"}); {@code null} or blank
   *     returns {@code null}
   * @return {@link AdministrativeArea}, or {@code null} if not found
   */
  public static AdministrativeArea lookupAdministrativeArea(final String code) {
    if (code == null || code.isBlank()) {
      return null;
    }
    return administrativeAreaMap.get(code.trim());
  }

  private static void loadFips10() {
    try (CSVParser csvParser =
        FIPS10_FORMAT.parse(
            new UnicodeReader(
                AdministrativeAreas.class.getClassLoader().getResourceAsStream("fips10.data"),
                "UTF-8"))) {
      for (final CSVRecord record : csvParser) {
        final String code = record.get("code").trim();
        final String type = record.get("type").trim();
        final String name = record.get("name").trim();
        if (code.length() == 4 && !name.isEmpty()) {
          administrativeAreaMap.put(
              code, new AdministrativeArea(code, name, type, AdministrativeAreaSource.FIPS10));
        }
      }
    } catch (final IOException e) {
      throw new IllegalStateException("Cannot read FIPS10 data from internal database", e);
    }
  }

  private static void loadIso3166() {
    try (CSVParser csvParser =
        ISO_FORMAT.parse(
            new UnicodeReader(
                AdministrativeAreas.class.getClassLoader().getResourceAsStream("iso_adm.data"),
                "UTF-8"))) {
      for (final CSVRecord record : csvParser) {
        final String countryCode = record.get("country_code").trim();
        final String regionName = record.get("region_name").trim();
        final String regionType = record.get("region_type").trim();
        final String regionalCode = record.get("regional_code").trim();
        if (!countryCode.isEmpty() && !regionalCode.isEmpty() && !regionName.isEmpty()) {
          final String key = countryCode + "-" + regionalCode;
          administrativeAreaMap.put(
              key,
              new AdministrativeArea(
                  key, regionName, regionType, AdministrativeAreaSource.ISO_3166_2));
        }
      }
    } catch (final IOException e) {
      throw new IllegalStateException(
          "Cannot read ISO administrative data from internal database", e);
    }
  }

  private AdministrativeAreas() {}
}
