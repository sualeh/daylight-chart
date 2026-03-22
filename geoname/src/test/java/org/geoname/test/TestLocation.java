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
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import org.geoname.data.Location;
import org.geoname.parser.FormatterException;
import org.geoname.parser.GNISFileParser;
import org.geoname.parser.LocationFormatter;
import org.geoname.parser.LocationsListParser;
import org.geoname.parser.ParserException;
import org.junit.jupiter.api.Test;

public class TestLocation {

  private static final class CloseTrackingInputStream extends ByteArrayInputStream {

    private boolean closed;

    private CloseTrackingInputStream(final byte[] buffer) {
      super(buffer);
    }

    @Override
    public void close() {
      closed = true;
      try {
        super.close();
      } catch (final java.io.IOException e) {
        throw new IllegalStateException("Unexpected close failure", e);
      }
    }

    private boolean isClosed() {
      return closed;
    }
  }

  @Test
  public void delimitedParserClosesInputStreamOnHeaderFailure() throws ParserException {
    final CloseTrackingInputStream dataStream = new CloseTrackingInputStream(new byte[0]);

    assertThrows(ParserException.class, () -> new GNISFileParser(dataStream).parseLocations());
    assertThat(dataStream.isClosed(), is(true));
  }

  @Test
  public void location() throws ParserException, FormatterException {

    final String locationString = "Aberdeen;GB;Europe/London;+5710-00204/";
    final Location location = LocationsListParser.parseLocation(locationString);

    assertThat(LocationFormatter.formatLocation(location), is(locationString));
  }

  @Test
  public void locations() throws ParserException {
    final InputStream dataStream =
        this.getClass().getClassLoader().getResourceAsStream("locations.data");
    final Collection<Location> locations = new LocationsListParser(dataStream).parseLocations();

    assertThat(locations.size(), is(109));
  }

  @Test
  public void locationsParserClosesInputStream() throws ParserException {
    final CloseTrackingInputStream dataStream =
        new CloseTrackingInputStream(
            "# comment%nAberdeen;GB;Europe/London;+5710-00204/%n"
                .formatted()
                .getBytes(StandardCharsets.UTF_8));

    final Collection<Location> locations = new LocationsListParser(dataStream).parseLocations();

    assertThat(locations.size(), is(1));
    assertThat(dataStream.isClosed(), is(true));
  }
}
