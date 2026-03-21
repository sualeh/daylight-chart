package daylightchart.sunchart.calculation;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.geoname.data.Location;
import org.geoname.parser.LocationsListParser;
import org.geoname.parser.ParserException;
import org.junit.Test;

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

    assertEquals(12, sunChartYear.getSunPositionsList().size());
    for (final SunPositions sunPositions: sunChartYear.getSunPositionsList())
    {
      assertFalse(sunPositions.iterator().hasNext() == false);
      int count = 0;
      for (@SuppressWarnings("unused")
      final SunPosition ignored: sunPositions)
      {
        count++;
      }
      assertEquals(24, count);
    }
  }

}
