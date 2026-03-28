/*
 * Daylight Chart
 * http://sualeh.github.io/DaylightChart
 * Copyright (c) 2007-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package org.geoname.data;

import java.io.IOException;
import java.io.Serial;
import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

/**
 * In-memory database of US states.
 *
 * @author Sualeh Fatehi
 */
public final class USStates implements Serializable {

  @Serial private static final long serialVersionUID = 7433823852132316904L;

  private static final Map<String, USState> alphaCodeMap = new HashMap<>();
  private static final Map<Integer, USState> numericCodeMap = new HashMap<>();
  private static final Map<String, USState> stateNameMap = new HashMap<>();

  /** Loads data from internal database. */
  static {
    final CSVFormat format =
        CSVFormat.DEFAULT
            .builder()
            .setHeader()
            .setSkipHeaderRecord(true)
            .setIgnoreEmptyLines(true)
            .get();
    try (CSVParser csvParser =
        new CSVParser(
            new org.geoname.parser.UnicodeReader(
                USStates.class.getClassLoader().getResourceAsStream("us.states.data"), "UTF-8"),
            format)) {
      for (final CSVRecord record : csvParser) {
        final String number = record.get("number");
        final String abbreviation = record.get("abbreviation");
        final String name = record.get("name");
        if (abbreviation == null || name == null || name.isEmpty()) {
          throw new IllegalArgumentException("Invalid US state record: " + record);
        }
        final USState state = new USState(name, abbreviation, Integer.parseInt(number));
        alphaCodeMap.put(state.getFips5_2AlphaCode(), state);
        numericCodeMap.put(state.getFips5_2NumericCode(), state);
        stateNameMap.put(state.getName(), state);
      }
    } catch (final IOException e) {
      throw new IllegalStateException("Cannot read data from internal database", e);
    }
  }

  /**
   * Gets a collection of all US states.
   *
   * @return All US states.
   */
  public static Set<USState> getAllUSStates() {
    return new HashSet<>(alphaCodeMap.values());
  }

  /**
   * Looks up a state from the provided string - whether a state code or a state name.
   *
   * @param stateString USState
   * @return USState ojbect, or null
   */
  public static USState lookupUSState(final String stateString) {
    USState state = null;
    if (stateString != null) {
      if (stateString.length() == 2) {
        if (alphaCodeMap.containsKey(stateString)) {
          state = lookupUSStateAlphaCode(stateString);
        }
      } else {
        state = lookupUSStateName(stateString);
      }
    }
    return state;
  }

  /**
   * Looks up a state from the two-letter ISO 3166 state code.
   *
   * @param iso3166USStateCode2 ISO 3166 state code
   * @return USState ojbect, or null
   */
  public static USState lookupUSStateAlphaCode(final String iso3166USStateCode2) {
    return alphaCodeMap.get(iso3166USStateCode2);
  }

  /**
   * Looks up a state from the state name.
   *
   * @param stateName US state name
   * @return USState ojbect, or null
   */
  public static USState lookupUSStateName(final String stateName) {
    return stateNameMap.get(stateName);
  }

  /**
   * Looks up a state from the FIPS-10 state code.
   *
   * @param usStateNumericCode FIPS-10 state code
   * @return USState ojbect, or null
   */
  public static USState lookupUSStateNumericCode(final int usStateNumericCode) {
    return numericCodeMap.get(usStateNumericCode);
  }
}
