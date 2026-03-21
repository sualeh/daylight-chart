package daylightchart.sunchart.calculation;


import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.geoname.data.Location;
import org.geoname.parser.LocationsListParser;
import org.geoname.parser.ParserException;
import org.junit.jupiter.api.Test;

public class SunChartUtilityTest
{

  @Test
  public void shouldCreateSunChartDataWithoutSunpositionDependency()
    throws ParserException
  {
    final Location location = LocationsListParser
      .parseLocation("Boston, MA;US;America/New_York;+4232-07104/");

    final SunChartYearData sunChartYear = SunChartUtility
      .createSunChartYear(location, 2024);

    assertThat(sunChartYear.getSunPositionsList().size(), is(12));
    for (final SunPositions sunPositions: sunChartYear.getSunPositionsList())
    {
      assertThat(sunPositions.iterator().hasNext(), is(true));
      int count = 0;
      for (@SuppressWarnings("unused")
      final SunPosition ignored: sunPositions)
      {
        count++;
      }
      assertThat(count, is(24));
    }
  }

}
