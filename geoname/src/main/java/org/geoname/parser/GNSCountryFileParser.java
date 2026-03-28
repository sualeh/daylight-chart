/*
 * Daylight Chart
 * http://sualeh.github.io/DaylightChart
 * Copyright (c) 2007-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package org.geoname.parser;

import java.util.Map;
import org.geoname.data.Countries;
import org.geoname.data.Country;
import org.geoname.data.FIPS10AdministrationDivisions;
import org.geoname.data.Location;
import org.geoname.parser.resources.ResourceRef;

/**
 * Parses objects from strings.
 *
 * @author Sualeh Fatehi
 */
public final class GNSCountryFileParser extends BaseDelimitedLocationsFileParser {

  private static final String LONGITUDE_DECIMAL_DEGREES = "long_dd";
  private static final String LATITUDE_DECIMAL_DEGREES = "lat_dd";
  private static final String FULL_NAME = "full_name";
  private static final String FIRST_ORDER_ADMINISTRATIVE_SUBDIVISION_CODE = "adm1";
  private static final String COUNTRY_CODE = "cc_ft";
  private static final String NAME_TYPE = "nt";
  private static final String FEATURE_CLASSIFICATION_CODE = "fc";

  public GNSCountryFileParser(final ResourceRef resourceRef) throws ParserException {
    super(resourceRef, "\t");
  }

  @Override
  protected Location parseLocation(final Map<String, String> locationDataMap) {
    if (locationDataMap == null) {
      return null;
    }

    final String featureClassification = locationDataMap.get(FEATURE_CLASSIFICATION_CODE);
    final String nameType = locationDataMap.get(NAME_TYPE);
    if (!"P".equals(featureClassification) || !"C".equals(nameType) && !"N".equals(nameType)) {
      // Skip populated places that have conventional or native names
      return null;
    }
    try {
      final String alpha3CountryCode = locationDataMap.get(COUNTRY_CODE);
      final Country country = Countries.lookupIso3166CountryCode3(alpha3CountryCode);

      final int fips10AdministrationDivisionCode =
          getInteger(locationDataMap, FIRST_ORDER_ADMINISTRATIVE_SUBDIVISION_CODE, 0);
      final String fips10AdministrationDivisionName;
      if (fips10AdministrationDivisionCode > 0) {
        fips10AdministrationDivisionName =
            FIPS10AdministrationDivisions.lookupFips10AdministrationDivisionName(
                country, "%02d".formatted(fips10AdministrationDivisionCode));
      } else {
        fips10AdministrationDivisionName = null;
      }

      if (!locationDataMap.containsKey(FULL_NAME)) {
        return null;
      }
      final StringBuilder city = new StringBuilder().append(locationDataMap.get(FULL_NAME));
      if (fips10AdministrationDivisionName != null) {
        city.append(", ").append(fips10AdministrationDivisionName);
      }

      return getLocation(
          locationDataMap,
          city.toString(),
          country,
          LATITUDE_DECIMAL_DEGREES,
          LONGITUDE_DECIMAL_DEGREES,
          "elev");
    } catch (final ParserException e) {
      return null;
    }
  }
}
