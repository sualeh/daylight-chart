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

  public GNSCountryFileParser(final ResourceRef resourceRef) throws ParserException {
    super(resourceRef, "\t");
  }

  @Override
  protected Location parseLocation(final Map<String, String> locationDataMap) {
    if (locationDataMap == null) {
      return null;
    }

    final String featureClassification = locationDataMap.get("FC");
    final String nameType = locationDataMap.get("NT");
    if (!"P".equals(featureClassification) || !"C".equals(nameType) && !"N".equals(nameType)) {
      return null;
    }
    try {
      final Country country = Countries.lookupFips10CountryCode(locationDataMap.get("CC1"));

      final int fips10AdministrationDivisionCode = getInteger(locationDataMap, "ADM1", 0);
      final String fips10AdministrationDivisionName;
      if (fips10AdministrationDivisionCode > 0) {
        fips10AdministrationDivisionName =
            FIPS10AdministrationDivisions.lookupFips10AdministrationDivisionName(
                country, "%02d".formatted(fips10AdministrationDivisionCode));
      } else {
        fips10AdministrationDivisionName = null;
      }

      String city;
      if (locationDataMap.containsKey("FULL_NAME_RO")) {
        city = locationDataMap.get("FULL_NAME_RO");
      } else if (locationDataMap.containsKey("FULL_NAME")) {
        city = locationDataMap.get("FULL_NAME");
      } else {
        return null;
      }
      if (fips10AdministrationDivisionName != null) {
        city = city + ", " + fips10AdministrationDivisionName;
      }

      return getLocation(locationDataMap, city, country, "LAT", "LONG", "ELEV");
    } catch (final ParserException e) {
      return null;
    }
  }
}
