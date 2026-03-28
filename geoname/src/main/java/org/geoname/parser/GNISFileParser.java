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
import org.geoname.data.Location;
import org.geoname.parser.resources.ResourceRef;

/**
 * Parses data files.
 *
 * @author Sualeh Fatehi
 */
public final class GNISFileParser extends BaseDelimitedLocationsFileParser {

  private static final Country usa = Countries.lookupCountry("US");

  public GNISFileParser(final ResourceRef resourceRef) throws ParserException {
    super(resourceRef, "\\|");
  }

  @Override
  protected Location parseLocation(final Map<String, String> locationDataMap) {
    if (locationDataMap == null) {
      return null;
    }

    final String featureClass = locationDataMap.get("FEATURE_CLASS");
    if (featureClass == null || !"Populated Place".equals(featureClass)) {
      return null;
    }
    try {
      // City name is in the form: city, state
      final String city =
          locationDataMap.get("FEATURE_NAME") + ", " + locationDataMap.get("STATE_ALPHA");

      return getLocation(locationDataMap, city, usa, "PRIM_LAT_DEC", "PRIM_LONG_DEC", "ELEVATION");
    } catch (final ParserException e) {
      return null;
    }
  }
}
