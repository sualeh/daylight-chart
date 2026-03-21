package daylightchart.options;


import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import java.nio.file.Files;
import java.nio.file.Path;

import org.geoname.data.LocationsSortOrder;
import org.junit.jupiter.api.Test;

import daylightchart.daylightchart.calculation.TwilightType;
import daylightchart.daylightchart.chart.ChartOrientation;
import daylightchart.daylightchart.chart.TimeZoneOption;

class OptionsTest
{

  @Test
  void shouldProvideStableDefaultOptions()
  {
    final Options options = new Options();

    assertThat(options.getChartOptions(), is(notNullValue()));
    assertThat(options.getLocationsSortOrder(), is(LocationsSortOrder.BY_NAME));
    assertThat(options.getTimeZoneOption(), is(TimeZoneOption.USE_TIME_ZONE));
    assertThat(options.getChartOrientation(), is(ChartOrientation.STANDARD));
    assertThat(options.getTwilightType(), is(TwilightType.CIVIL));
    assertThat(options.isShowChartLegend(), is(true));
  }

  @Test
  void shouldOnlyAcceptExistingWorkingDirectories()
    throws Exception
  {
    final Options options = new Options();
    final Path workingDirectory = Files.createTempDirectory("daylight-core-options");

    options.setWorkingDirectory(workingDirectory);
    assertThat(options.getWorkingDirectory(), is(workingDirectory));

    options.setWorkingDirectory(workingDirectory.resolve("missing"));
    assertThat(options.getWorkingDirectory(), is(workingDirectory));
  }

}
