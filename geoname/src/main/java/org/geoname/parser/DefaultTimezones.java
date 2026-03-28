/*
 * Daylight Chart
 * http://sualeh.github.io/DaylightChart
 * Copyright (c) 2007-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package org.geoname.parser;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.geoname.data.Countries;
import org.geoname.data.Country;
import org.geoname.data.USState;
import org.geoname.data.USStates;
import us.fatehi.pointlocation6709.Longitude;

/**
 * Time zone utilities.
 *
 * @author Sualeh Fatehi
 */
public final class DefaultTimezones {

  private static final Logger LOGGER = Logger.getLogger(DefaultTimezones.class.getName());

  private static final Map<Country, List<String>> defaultTimezones = new HashMap<>();
  private static final Map<String, List<String>> allTimezoneIds = new HashMap<>();
  private static final Set<TimeZoneDisplay> allTimezones = new TreeSet<>();

  /**
   * Loads default time zones from the internal database. These are default time zone ids for every
   * time zone for each country.
   */
  static {
    final List<Country> allCountries = Countries.getAllCountries();
    for (final Country country : allCountries) {
      defaultTimezones.put(country, new ArrayList<>());
    }

    final CSVFormat format =
        CSVFormat.DEFAULT
            .builder()
            .setHeader()
            .setSkipHeaderRecord(true)
            .setCommentMarker('#')
            .setIgnoreEmptyLines(true)
            .get();
    try (CSVParser csvParser =
        format.parse(
            new org.geoname.parser.UnicodeReader(
                DefaultTimezones.class
                    .getClassLoader()
                    .getResourceAsStream("default.timezones.data"),
                "UTF-8"))) {
      for (final CSVRecord record : csvParser) {
        final String iso3166CountryCode2 = record.get("country_code");
        final String timezoneId = record.get("timezone");
        if (iso3166CountryCode2 == null
            || timezoneId == null
            || iso3166CountryCode2.length() != 2
            || timezoneId.isEmpty()) {
          continue;
        }
        final ZoneId defaultZoneId = ZoneId.of(timezoneId);
        final Country country = Countries.lookupIso3166CountryCode2(iso3166CountryCode2);
        final List<String> defaultTimezonesForCountry = defaultTimezones.get(country);
        if (defaultTimezonesForCountry != null) {
          defaultTimezonesForCountry.add(defaultZoneId.getId());
        }
      }
    } catch (final IOException e) {
      throw new IllegalStateException("Cannot read data from internal database", e);
    }
  }

  static {
    final Set<String> allTimeZoneIds = ZoneId.getAvailableZoneIds();
    for (final String timeZoneId : allTimeZoneIds) {
      if (timeZoneId.startsWith("Etc/")) {
        continue;
      }
      allTimezones.add(new TimeZoneDisplay(ZoneId.of(timeZoneId)));
      final List<String> timeZoneIdParts = splitTimeZoneId(timeZoneId);
      if (timeZoneIdParts.size() > 0) {
        allTimezoneIds.put(timeZoneId, timeZoneIdParts);
      }
    }
  }

  /**
   * Looks up a country from the provided string - whether a country code or a country name. *
   *
   * @param city City
   * @param country Country
   * @param longitude Longitude
   * @return Country ojbect, or null
   */
  public static String attemptTimeZoneMatch(
      final String city, final Country country, final Longitude longitude) {

    if (longitude == null) {
      return null;
    }

    // Timezone offset in hours
    double tzOffsetHours;
    tzOffsetHours = longitude.getDegrees() / 15D;
    tzOffsetHours = roundToNearestFraction(tzOffsetHours, 0.5D);

    // More than one timezone found
    String timeZoneId = null;
    // 1. Try a match by city name, in case there is a special
    // time zone for the city
    timeZoneId = findBestTimeZoneId(city, country);
    // 2. Check the country's default time zone
    if (timeZoneId == null) {
      final List<String> defaultTimezonesForCountry = defaultTimezones.get(country);
      if (defaultTimezonesForCountry == null || defaultTimezonesForCountry.size() == 0) {
        return createGMTTimeZoneId(tzOffsetHours);
      }
      if (defaultTimezonesForCountry.size() == 1) {
        return defaultTimezonesForCountry.toArray(new String[1])[0];
      }
      // 3. Try a match by longitude, if no good default for
      // the country is found
      double leastDifference = Double.MAX_VALUE;
      for (final String defaultTimeZoneId : defaultTimezonesForCountry) {
        final double difference =
            Math.abs(getStandardTimeZoneOffsetHours(defaultTimeZoneId) - tzOffsetHours);
        if (difference < leastDifference) {
          leastDifference = difference;
          timeZoneId = defaultTimeZoneId;
        }
      }
    }

    LOGGER.log(
        Level.INFO, "Time zone id for \"" + city + ", " + country + "\" is \"" + timeZoneId + "\"");

    return timeZoneId;
  }

  /**
   * Create a STANDARD GMT-based timezone id.
   *
   * @param longitude Longitude
   * @return Time zone id string
   */
  public static String createGMTTimeZoneId(final Longitude longitude) {
    if (longitude == null) {
      return ZoneId.systemDefault().getId();
    }

    final double tzOffsetHours = longitude.getDegrees() / 15D;
    String timeZoneId = "GMT";
    if (tzOffsetHours < 0) {
      timeZoneId = timeZoneId + "-";
    } else {
      timeZoneId = timeZoneId + "+";
    }

    final int[] hourFields = Utility.sexagesimalSplit(Math.abs(tzOffsetHours));

    final NumberFormat numberFormat = NumberFormat.getIntegerInstance();
    numberFormat.setMinimumIntegerDigits(2);

    timeZoneId =
        timeZoneId + numberFormat.format(hourFields[0]) + ":" + numberFormat.format(hourFields[1]);
    return timeZoneId;
  }

  /**
   * Gets all time zones.
   *
   * @return All time zones
   */
  public static List<TimeZoneDisplay> getAllTimeZonesForDisplay() {
    final ArrayList<TimeZoneDisplay> allTimeZonesList = new ArrayList<>(allTimezones);
    Collections.sort(allTimeZonesList);
    return allTimeZonesList;
  }

  /**
   * Calculates the STANDARD time zone offset, in hours.
   *
   * @param timeZoneId Time zone id
   * @return Time zone offset, in hours
   */
  public static double getStandardTimeZoneOffsetHours(final String timeZoneId) {
    if (timeZoneId == null) {
      return 0D;
    }
    final ZoneId zoneId = ZoneId.of(timeZoneId);
    return zoneId.getRules().getStandardOffset(Instant.now()).getTotalSeconds() / (60D * 60D);
  }

  /**
   * Utility to round a number to the nearest half.
   *
   * @param number Number to round
   * @param fraction Fraction to round to
   * @return Rounded numbers
   */
  public static double roundToNearestFraction(final double number, final double fraction) {
    return new BigDecimal(number / fraction)
            .round(new MathContext(1, RoundingMode.HALF_UP))
            .doubleValue()
        * fraction;
  }

  /**
   * Create a STANDARD GMT-based timezone id.
   *
   * @param tzOffsetHours Time zone offset, in hours
   * @return Time zone id string
   */
  private static String createGMTTimeZoneId(final double tzOffsetHours) {
    String timeZoneId = "GMT";
    if (tzOffsetHours < 0) {
      timeZoneId = timeZoneId + "-";
    } else {
      timeZoneId = timeZoneId + "+";
    }

    final int[] hourFields = Utility.sexagesimalSplit(Math.abs(tzOffsetHours));

    final NumberFormat numberFormat = NumberFormat.getIntegerInstance();
    numberFormat.setMinimumIntegerDigits(2);

    timeZoneId =
        timeZoneId + numberFormat.format(hourFields[0]) + ":" + numberFormat.format(hourFields[1]);
    return timeZoneId;
  }

  /**
   * Gets best possible timezone ID for a location. Returns a null if no good match is found.
   *
   * @param city City.
   * @param strCountry Country.
   * @return String, best possible time zone id.
   */
  private static String findBestTimeZoneId(final String city, final Country country) {

    if (city == null || city.length() == 0 || country == null) {
      return null;
    }

    final List<String> locationParts = new ArrayList<>();
    for (final String locationPart : city.split(",")) {
      locationParts.add(locationPart.trim().replaceAll(" |-", "_"));
    }
    if ("US".equals(country.getCode()) && locationParts.size() >= 2) {
      final String stateString = locationParts.getLast();
      final USState state = USStates.lookupUSState(stateString);
      if (state != null) {
        // Replace state code with full state name
        locationParts.remove(stateString);
        locationParts.add(state.getName());
      }
    }
    locationParts.add(country.getName());

    String bestTimeZoneId = null;

    for (final Entry<String, List<String>> entry : allTimezoneIds.entrySet()) {
      final String timeZoneId = entry.getKey();
      final List<String> timeZoneParts = entry.getValue();
      final String locationPart1 = locationParts.getFirst().toLowerCase(Locale.ENGLISH);
      final String timeZonePart1 = timeZoneParts.getFirst().toLowerCase(Locale.ENGLISH);
      if (locationPart1.equals(timeZonePart1)) {
        if (timeZoneParts.size() <= 1) {
          bestTimeZoneId = timeZoneId;
          break;
        }
        final String locationPart2 = locationParts.get(1).toLowerCase(Locale.ENGLISH);
        final String timeZonePart2 = timeZoneParts.get(1).toLowerCase(Locale.ENGLISH);
        if (locationPart2.equals(timeZonePart2)) {
          bestTimeZoneId = timeZoneId;
          break;
        }
      }
    }

    return bestTimeZoneId;
  }

  private static List<String> splitTimeZoneId(final String timeZoneId) {
    final List<String> timeZoneParts = new ArrayList<>();
    timeZoneParts.addAll(Arrays.asList(timeZoneId.split("/")));
    // If the first part is not a country, it is a continent, so
    // remove it
    final String firstPart = timeZoneParts.getFirst();
    final Country country = Countries.lookupCountry(firstPart);
    if (country == null) {
      timeZoneParts.removeFirst(); // Remove the continent
    }
    Collections.reverse(timeZoneParts);
    return timeZoneParts;
  }
}
