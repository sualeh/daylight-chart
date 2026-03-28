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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.geoname.data.AdministrativeArea;
import org.geoname.data.AdministrativeAreas;
import org.geoname.data.Countries;
import org.geoname.data.Country;
import org.geoname.data.Location;
import org.geoname.parser.resources.ResourceRef;
import us.fatehi.pointlocation6709.PointLocation;
import us.fatehi.pointlocation6709.parse.PointLocationParser;

/**
 * Parses locations.
 *
 * @author Sualeh Fatehi
 */
public final class LocationsListParser implements LocationsParser {
  private static final Logger LOGGER = Logger.getLogger(LocationsListParser.class.getName());

  /**
   * Parses a string representation of a location.
   *
   * @param representation String representation of a location
   * @return Location
   * @throws ParserException On a parse exception
   */
  public static Location parseLocation(final String representation) throws ParserException {
    if (representation == null || representation.length() == 0) {
      throw new ParserException("No location provided");
    }

    final String[] fields = representation.split(";");
    if (fields.length != 5) {
      throw new ParserException("Invalid location format: " + representation);
    }

    try {
      final String city = fields[0];
      final String admCode = fields[1].trim();
      final AdministrativeArea administrativeArea =
          admCode.isEmpty() ? null : AdministrativeAreas.lookupAdministrativeArea(admCode);
      final Country country = Countries.lookupCountry(fields[2]);
      final String timeZoneId = fields[3];
      final PointLocation pointLocation = PointLocationParser.parsePointLocation(fields[4]);

      final Location location =
          new Location(city, administrativeArea, country, timeZoneId, pointLocation);
      return location;
    } catch (final us.fatehi.pointlocation6709.parse.ParserException e) {
      throw new ParserException("Invalid location: " + representation, e);
    }
  }

  private final ResourceRef resourceRef;

  public LocationsListParser(final ResourceRef resourceRef) throws ParserException {
    if (resourceRef == null) {
      throw new ParserException("Cannot read locations");
    }
    this.resourceRef = resourceRef;
  }

  /**
   * {@inheritDoc}
   *
   * @see org.geoname.parser.LocationsParser#parseLocations()
   */
  @Override
  public Collection<Location> parseLocations() throws ParserException {

    final List<Location> locations = new ArrayList<>();
    try (InputStream stream = resourceRef.openStream();
        BufferedReader reader = new BufferedReader(new UnicodeReader(stream, "UTF-8"))) {
      final Set<String> seen = new HashSet<>();
      String line;
      while ((line = reader.readLine()) != null) {
        line = line.trim();
        if (!line.startsWith("#")) {
          final Location location = parseLocation(line);
          final String admCode =
              location.getAdministrativeArea() != null
                  ? location.getAdministrativeArea().getCode()
                  : "";
          if (seen.add(
              location.getCity() + "|" + admCode + "|" + location.getCountry().getCode())) {
            locations.add(location);
          }
        }
      }
    } catch (final IOException e) {
      throw new ParserException("Invalid locations", e);
    }

    LOGGER.log(Level.INFO, "Loaded " + locations.size() + " locations");
    return locations;
  }
}
