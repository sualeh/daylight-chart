/*
 * Daylight Chart
 * http://sualeh.github.io/DaylightChart
 * Copyright (c) 2007-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package org.geoname.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Handler;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.geoname.data.Location;
import org.geoname.parser.DefaultTimezones;
import org.geoname.parser.GNISFileParser;
import org.geoname.parser.GNSCountryFileParser;
import org.geoname.parser.ParserException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class TestGeoNameFiles {

  @BeforeAll
  public static void turnOffLogs() {
    final Logger[] loggers = {
      Logger.getLogger(DefaultTimezones.class.getName()),
      Logger.getLogger("org.geoname.parser.BaseDelimitedLocationsFileParser")
    };
    for (final Logger logger : loggers) {
      logger.setUseParentHandlers(false);
      final Handler[] handlers = logger.getHandlers();
      for (final Handler handler : handlers) {
        logger.removeHandler(handler);
      }
    }
  }

  @Test
  public void GNISUSStates() throws ParserException, IOException {
    final String date = "20100607";
    parseGNISUSStates("MA", date, 2422);
    parseGNISUSStates("HI", date, 541);
  }

  @Test
  public void GNSCountries() throws ParserException, IOException {
    parseGNSCountryFile("uz.zip", 3756);
    parseGNSCountryFile("lo.zip", 4969);
  }

  private void parseGNISUSStates(final String state, final String date, final int numLocations)
      throws ParserException, IOException {
    final String filename = state + "_Features_" + date + ".zip";
    Collection<Location> locations = new ArrayList<>();

    final InputStream dataStream = this.getClass().getClassLoader().getResourceAsStream(filename);
    final ZipInputStream zis = new ZipInputStream(dataStream);
    final ZipEntry ze = zis.getNextEntry();
    if (ze != null) {
      final GNISFileParser parser = new GNISFileParser(zis);
      locations = parser.parseLocations();
    }
    zis.close();

    assertThat(
        "Number of locations in file %s:%s".formatted(filename, ze),
        locations.size(),
        is(numLocations));
  }

  private void parseGNSCountryFile(final String filename, final int numLocations)
      throws ParserException, IOException {
    Collection<Location> locations = new ArrayList<>();

    final InputStream dataStream = this.getClass().getClassLoader().getResourceAsStream(filename);
    final ZipInputStream zis = new ZipInputStream(dataStream);
    final ZipEntry ze = zis.getNextEntry();
    if (ze != null) {
      final GNSCountryFileParser parser = new GNSCountryFileParser(zis);
      locations = parser.parseLocations();
    }
    zis.close();

    assertThat(
        "Number of locations in file %s:%s".formatted(filename, ze),
        locations.size(),
        is(numLocations));
  }
}
