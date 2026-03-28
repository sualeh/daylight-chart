/*
 * Daylight Chart
 * http://sualeh.github.io/DaylightChart
 * Copyright (c) 2007-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package org.geoname.data;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.geoname.parser.UnicodeReader;

/**
 * In-memory database of locations.
 *
 * @author Sualeh Fatehi
 */
public final class Countries {

  private static final Map<String, Country> iso3166_alpha2_CountryCodeMap;
  private static final Map<String, Country> iso3166_alpha3_CountryCodeMap;
  private static final Map<String, Country> fips10CountryCodeMap;
  private static final Map<String, Country> countryNameMap = new HashMap<>();

  /** Loads data from internal database. */
  static {
    iso3166_alpha2_CountryCodeMap = readISOCountryData("iso_countries_alpha2.data");
    iso3166_alpha3_CountryCodeMap = readISOCountryData("iso_countries_alpha3.data");
    fips10CountryCodeMap = readFips10CountryData("fips10_countries.data");

    final Collection<Country> countries = new HashSet<>(iso3166_alpha2_CountryCodeMap.values());
    countries.addAll(fips10CountryCodeMap.values());

    for (final Country country : countries) {
      countryNameMap.put(country.getName(), country);
    }
  }

  /**
   * Gets a collection of all countries.
   *
   * @return All countries.
   */
  public static List<Country> getAllCountries() {
    final ArrayList<Country> countries = new ArrayList<>(iso3166_alpha2_CountryCodeMap.values());
    Collections.sort(countries);
    return countries;
  }

  /**
   * Looks up a country from the provided string - whether a country code or a country name.
   *
   * @param countryString Country
   * @return Country ojbect, or null
   */
  public static Country lookupCountry(final String countryString) {
    Country country;
    if (countryString == null) {
      country = null;
    } else if (countryString.length() == 2) {
      if (iso3166_alpha2_CountryCodeMap.containsKey(countryString)) {
        country = lookupIso3166CountryCode2(countryString);
      } else {
        country = lookupFips10CountryCode(countryString);
        if (country == null) {
          final Collection<Country> countries = fips10CountryCodeMap.values();
          for (final Country fips10Country : countries) {
            if (fips10Country.getCode().equals(countryString)) {
              country = fips10Country;
              break;
            }
          }
        }
      }
    } else {
      country = lookupCountryName(countryString);
    }
    return country;
  }

  /**
   * Looks up a country from the country name.
   *
   * @param countryName Country name
   * @return Country ojbect, or null
   */
  public static Country lookupCountryName(final String countryName) {
    return countryNameMap.get(countryName);
  }

  /**
   * Looks up a country from the FIPS-10 country code.
   *
   * @param fips10CountryCode FIPS-10 country code
   * @return Country ojbect, or null
   */
  public static Country lookupFips10CountryCode(final String fips10CountryCode) {
    return fips10CountryCodeMap.get(fips10CountryCode);
  }

  /**
   * Looks up a country from the two-letter ISO 3166 country code.
   *
   * @param iso3166CountryCode2 ISO 3166 country code
   * @return Country ojbect, or null
   */
  public static Country lookupIso3166CountryCode2(final String iso3166CountryCode2) {
    return iso3166_alpha2_CountryCodeMap.get(iso3166CountryCode2);
  }

  /**
   * Looks up a country from the two-letter ISO 3166 country code.
   *
   * @param iso3166CountryCode3 ISO 3166 country code
   * @return Country ojbect, or null
   */
  public static Country lookupIso3166CountryCode3(final String iso3166CountryCode3) {
    return iso3166_alpha3_CountryCodeMap.get(iso3166CountryCode3);
  }

  private static Map<String, Country> readFips10CountryData(final String dataResource) {
    final Map<String, Country> countryCodeMap = new HashMap<>();
    final CSVFormat format =
        CSVFormat.DEFAULT
            .builder()
            .setHeader()
            .setSkipHeaderRecord(true)
            .setIgnoreEmptyLines(true)
            .get();
    try (CSVParser csvParser =
        format.parse(
            new UnicodeReader(
                Countries.class.getClassLoader().getResourceAsStream(dataResource), "UTF-8"))) {
      for (final CSVRecord record : csvParser) {
        final String fips10CountryCode = record.get("fips_code");
        final String iso3166CountryCode = record.get("iso_code");
        if (fips10CountryCode == null
            || iso3166CountryCode == null
            || fips10CountryCode.length() > 2
            || iso3166CountryCode.isEmpty()) {
          continue;
        }
        final Country country;
        if (iso3166_alpha2_CountryCodeMap.containsKey(iso3166CountryCode)) {
          country = iso3166_alpha2_CountryCodeMap.get(iso3166CountryCode);
        } else {
          final String countryName = record.get("country_name");
          if (iso3166CountryCode.length() == 2) {
            country = new Country(iso3166CountryCode, countryName);
          } else if (fips10CountryCode.length() == 2) {
            country = new Country(fips10CountryCode, countryName);
          } else {
            country = null;
          }
        }
        if (country != null) {
          countryCodeMap.put(fips10CountryCode, country);
        }
      }
    } catch (final IOException e) {
      throw new IllegalStateException("Cannot read data from internal database", e);
    }
    return countryCodeMap;
  }

  private static Map<String, Country> readISOCountryData(final String dataResource) {
    final Map<String, Country> countryCodeMap = new HashMap<>();
    final CSVFormat format =
        CSVFormat.DEFAULT
            .builder()
            .setHeader()
            .setSkipHeaderRecord(true)
            .setIgnoreEmptyLines(true)
            .get();
    try (CSVParser csvParser =
        format.parse(
            new UnicodeReader(
                Countries.class.getClassLoader().getResourceAsStream(dataResource), "UTF-8"))) {
      for (final CSVRecord record : csvParser) {
        final String code = record.get("code");
        final String name = record.get("name");
        if (code == null || name == null || name.isEmpty()) {
          continue;
        }
        final Country country = new Country(code, name);
        countryCodeMap.put(country.getCode(), country);
      }
    } catch (final IOException e) {
      throw new IllegalStateException("Cannot read data from internal database", e);
    }
    return countryCodeMap;
  }
}
