package daylightchart.service;


import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import java.nio.file.Files;
import java.nio.file.Path;

import org.geoname.data.Location;
import org.geoname.parser.LocationsListParser;
import org.junit.jupiter.api.Test;

import daylightchart.daylightchart.layout.ChartFileType;
import daylightchart.options.Options;

class DaylightChartReportServiceTest
{

  @Test
  void shouldCreateAndWriteReports()
    throws Exception
  {
    final DaylightChartReportService service = new DaylightChartReportService();
    final Location location = LocationsListParser
      .parseLocation("Boston, MA;US;America/New_York;+4232-07104/");
    final Options options = new Options();
    final Path imageFile = Files.createTempFile("service-report", ".png");

    assertThat(service.createReport(location, options), is(notNullValue()));

    service.writeReport(location, options, imageFile, ChartFileType.png);

    assertThat(Files.exists(imageFile), is(true));
    assertThat(Files.size(imageFile), is(greaterThan(0L)));
  }

}
