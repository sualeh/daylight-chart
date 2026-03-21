package daylightchart.daylightchart.layout;


import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import java.nio.file.Files;
import java.nio.file.Path;

import org.geoname.data.Location;
import org.geoname.parser.LocationsListParser;
import org.junit.jupiter.api.Test;

import daylightchart.options.Options;

class DaylightChartReportTest
{

  @Test
  void shouldCreateReportFileNameAndImage()
    throws Exception
  {
    final Location location = LocationsListParser
      .parseLocation("Boston, MA;US;America/New_York;+4232-07104/");
    final DaylightChartReport report = new DaylightChartReport(location,
                                                               new Options());
    final Path imageFile = Files.createTempFile("daylight-report", ".png");

    assertThat(report.getChart(), is(notNullValue()));
    assertThat(report.getReportFileName(ChartFileType.png), endsWith(".png"));

    report.write(imageFile, ChartFileType.png);

    assertThat(Files.exists(imageFile), is(true));
    assertThat(Files.size(imageFile), is(greaterThan(0L)));
  }

}
