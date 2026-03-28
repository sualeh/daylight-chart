/*
 * Daylight Chart
 * http://sualeh.github.io/DaylightChart
 * Copyright (c) 2007-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package org.geoname.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.geoname.data.Country;
import org.geoname.data.Location;
import org.geoname.parser.resources.ResourceRef;
import us.fatehi.pointlocation6709.Angle;
import us.fatehi.pointlocation6709.Latitude;
import us.fatehi.pointlocation6709.Longitude;
import us.fatehi.pointlocation6709.PointLocation;

abstract class BaseDelimitedLocationsFileParser implements LocationsParser {
  private static final Logger LOGGER =
      Logger.getLogger(BaseDelimitedLocationsFileParser.class.getName());

  private final ResourceRef resourceRef;
  private final String delimiter;

  protected BaseDelimitedLocationsFileParser(final ResourceRef resourceRef, final String delimiter)
      throws ParserException {
    if (resourceRef == null) {
      throw new ParserException("Cannot read locations");
    }
    this.resourceRef = resourceRef;

    if (delimiter == null) {
      throw new ParserException("No delimiter provided");
    }
    this.delimiter = delimiter;
  }

  /**
   * {@inheritDoc}
   *
   * @see org.geoname.parser.LocationsParser#parseLocations()
   */
  @Override
  public final Collection<Location> parseLocations() throws ParserException {
    final Set<Location> locations = new HashSet<>();
    try (InputStream stream = resourceRef.openStream();
        BufferedReader reader = new BufferedReader(new UnicodeReader(stream, "UTF-8"))) {
      final String[] header = readHeader(reader);
      final Map<String, String> locationDataMap = new HashMap<>();
      String line;
      while ((line = reader.readLine()) != null) {
        final String[] fields = line.split(delimiter);
        locationDataMap.clear();
        for (int i = 0; i < header.length; i++) {
          final String data;
          if (fields.length > i) {
            data = fields[i];
          } else {
            data = null;
          }
          locationDataMap.put(header[i], data);
        }
        final Location location = parseLocation(locationDataMap);
        if (location != null) {
          locations.add(location);
        }
      }
    } catch (final IOException e) {
      throw new ParserException("Invalid locations", e);
    }

    LOGGER.log(Level.INFO, "Loaded " + locations.size() + " locations");
    return locations;
  }

  protected final double getDouble(
      final Map<String, String> locationDataMap, final String key, final double defaultValue) {
    double doubleValue = defaultValue;
    try {
      doubleValue = getDouble(locationDataMap, key);
    } catch (final Exception e) {
      doubleValue = defaultValue;
    }
    return doubleValue;
  }

  protected final int getInteger(
      final Map<String, String> locationDataMap, final String key, final int defaultValue) {
    int integerValue = defaultValue;
    if (locationDataMap != null && locationDataMap.containsKey(key)) {
      try {
        integerValue = Integer.parseInt(locationDataMap.get(key));
      } catch (final NumberFormatException e) {
        integerValue = defaultValue;
      }
    }
    return integerValue;
  }

  protected final Location getLocation(
      final Map<String, String> locationDataMap,
      final String city,
      final Country country,
      final String latitudeKey,
      final String longitudeKey,
      final String altitudeKey)
      throws ParserException {
    final Latitude latitude =
        new Latitude(Angle.fromDegrees(getDouble(locationDataMap, latitudeKey)));
    final Longitude longitude =
        new Longitude(Angle.fromDegrees(getDouble(locationDataMap, longitudeKey)));
    final double altitude = getDouble(locationDataMap, altitudeKey, 0D);

    final PointLocation pointLocation = new PointLocation(latitude, longitude, altitude, "");

    final String timeZoneId =
        DefaultTimezones.attemptTimeZoneMatch(city, country, pointLocation.getLongitude());

    try {
      return new Location(city, country, timeZoneId, pointLocation);
    } catch (final IllegalArgumentException e) {
      throw new ParserException("Could not get location", e);
    }
  }

  protected abstract Location parseLocation(final Map<String, String> locationDataMap);

  private final double getDouble(final Map<String, String> locationDataMap, final String key)
      throws ParserException {
    if (locationDataMap == null || !locationDataMap.containsKey(key)) {
      throw new ParserException("No value for key " + key);
    }

    try {
      return Double.parseDouble(locationDataMap.get(key));
    } catch (final NumberFormatException e) {
      throw new ParserException("Bad value for key " + key, e);
    }
  }

  private String[] readHeader(final BufferedReader reader) throws ParserException {
    try {
      String[] header = null;
      final String line = reader.readLine();
      if (line != null) {
        header = line.split(delimiter);
      }
      if (header == null || header.length == 0) {
        throw new ParserException("No header row provided");
      }
      LOGGER.log(Level.FINE, "Loaded header");
      return header;
    } catch (final IOException e) {
      throw new ParserException("Could not load locations", e);
    }
  }
}
