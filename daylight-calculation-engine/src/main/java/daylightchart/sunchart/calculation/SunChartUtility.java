/*
 *
 * Daylight Chart
 * http://sualeh.github.io/DaylightChart
 * Copyright (c) 2007-2016, Sualeh Fatehi.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 *
 */
package daylightchart.sunchart.calculation;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import org.geoname.data.Location;

import net.e175.klaus.solarpositioning.DeltaT;
import net.e175.klaus.solarpositioning.SPA;
import net.e175.klaus.solarpositioning.SolarPosition;

/**
 * Calculator for sunrise and sunset times for a year.
 *
 * @author Sualeh Fatehi
 */
public final class SunChartUtility
{

  /**
   * Calculator for sunrise and sunset times for a year.
   *
   * @param location
   *        Location
   * @param year
   *        Year
   * @return Full years sunset and sunrise times for a location
   */
  public static SunChartYearData createSunChartYear(final Location location,
                                                    final int year)
  {
    final SunChartYearData sunChartYear = new SunChartYearData(location, year);
    final ZoneId zoneId = getZoneId(location);
    final double latitude = getLatitude(location);
    final double longitude = getLongitude(location);

    for (final LocalDate date: getYearsDates(year))
    {
      final SunPositions sunPositions = new SunPositions(date);
      final double deltaT = DeltaT.estimate(date);
      for (int hour = 0; hour < 24; hour++)
      {
        final LocalDateTime dateTime = LocalDateTime
          .of(date.getYear(),
              date.getMonthValue(),
              date.getDayOfMonth(),
              hour,
              0,
              0);
        final ZonedDateTime zonedDateTime = dateTime.atZone(zoneId);
        final SPA.SpaTimeDependent timeDependent = SPA
          .calculateSpaTimeDependentParts(zonedDateTime, deltaT);
        final SolarPosition solarPosition = SPA
          .calculateSolarPositionWithTimeDependentParts(latitude,
                                                        longitude,
                                                        0D,
                                                        timeDependent);
        final SunPosition sunPosition = new SunPosition(dateTime,
                                                        90D
                                                            - solarPosition
                                                              .zenithAngle(),
                                                        solarPosition.azimuth());
        sunPositions.add(sunPosition);
      }
      sunChartYear.add(sunPositions);
    }
    return sunChartYear;
  }

  /**
   * Generate a year's worth of dates
   *
   * @return All the dates for the year
   */
  private static List<LocalDate> getYearsDates(final int year)
  {
    final List<LocalDate> dates = new ArrayList<>();
    for (int month = 1; month <= 12; month++)
    {
      LocalDate date;
      switch (month)
      {
        case 3:
          date = LocalDate.of(year, month, 20);
          break;
        case 6:
          date = LocalDate.of(year, month, 21);
          break;
        case 9:
          date = LocalDate.of(year, month, 22);
          break;
        case 12:
          date = LocalDate.of(year, month, 21);
          break;
        default:
          date = LocalDate.of(year, month, 15);
          break;
      }
      dates.add(date);
    }
    return dates;
  }

  private static double getLatitude(final Location location)
  {
    if (location == null)
    {
      return 0D;
    }
    return location.getPointLocation().getLatitude().getDegrees();
  }

  private static double getLongitude(final Location location)
  {
    if (location == null)
    {
      return 0D;
    }
    return location.getPointLocation().getLongitude().getDegrees();
  }

  private static ZoneId getZoneId(final Location location)
  {
    if (location == null)
    {
      return ZoneId.systemDefault();
    }
    return ZoneId.of(location.getTimeZoneId());
  }

  private SunChartUtility()
  {
    // Prevent instantiation
  }

}
