package daylightchart.sunchart.chart;


import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import java.awt.image.BufferedImage;

import org.geoname.data.Location;
import org.geoname.parser.LocationsListParser;
import org.junit.jupiter.api.Test;

import daylightchart.sunchart.calculation.SunChartUtility;
import daylightchart.sunchart.calculation.SunChartYearData;

class SunChartTest
{

  @Test
  void shouldRenderBufferedImage()
    throws Exception
  {
    final Location location = LocationsListParser
      .parseLocation("Boston, MA;US;America/New_York;+4232-07104/");
    final SunChartYearData yearData = SunChartUtility.createSunChartYear(location,
                                                                         2024);
    final SunChart chart = new SunChart(yearData);

    final BufferedImage image = chart
      .createBufferedImage(400, 300, BufferedImage.TYPE_INT_RGB, null);

    assertThat(chart, is(notNullValue()));
    assertThat(image, is(notNullValue()));
    assertThat(image.getWidth(), is(400));
    assertThat(image.getHeight(), is(300));
  }

}
