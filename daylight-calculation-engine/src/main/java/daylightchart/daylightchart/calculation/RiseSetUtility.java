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
package daylightchart.daylightchart.calculation;


import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import org.geoname.data.Location;
import org.geoname.parser.DefaultTimezones;

import daylightchart.daylightchart.chart.TimeZoneOption;
import daylightchart.options.Options;
import net.e175.klaus.solarpositioning.DeltaT;
import net.e175.klaus.solarpositioning.SPA;
import net.e175.klaus.solarpositioning.SunriseResult;

/**
 * Calculator for sunrise and sunset times for a year.
 *
 * @author Sualeh Fatehi
 */
public final class RiseSetUtility
{

  /**
   * Calculator for sunrise and sunset times for a year.
   *
   * @param location
   *        Location
   * @param year
   *        Year
   * @param options
   *        Options
   * @return Full years sunset and sunrise times for a location
   */
  public static RiseSetYearData createRiseSetYear(final Location location,
                                                  final int year,
                                                  final Options options)
  {
    final TimeZoneOption timeZoneOption = options.getTimeZoneOption();
    final ZoneId zoneId;
    if (location != null)
    {
      final String timeZoneId;
      if (timeZoneOption != null
          && timeZoneOption == TimeZoneOption.USE_TIME_ZONE)
      {
        timeZoneId = location.getTimeZoneId();
      }
      else
      {
        timeZoneId = DefaultTimezones
          .createGMTTimeZoneId(location.getPointLocation().getLongitude());
      }
      zoneId = ZoneId.of(timeZoneId);
    }
    else
    {
      zoneId = ZoneId.systemDefault();
    }
    final boolean timeZoneUsesDaylightTime = !zoneId.getRules()
      .getTransitionRules().isEmpty();
    final boolean useDaylightTime = timeZoneUsesDaylightTime
                                    && timeZoneOption != TimeZoneOption.USE_LOCAL_TIME;
    boolean wasDaylightSavings = false;

    final TwilightType twilight = options.getTwilightType();
    final RiseSetYearData riseSetYear = new RiseSetYearData(location,
                                                            twilight,
                                                            year);
    riseSetYear.setUsesDaylightTime(useDaylightTime);
    for (final LocalDate date: getYearsDates(year))
    {
      final boolean inDaylightSavings = zoneId.getRules()
        .isDaylightSavings(date.atStartOfDay().atZone(zoneId).toInstant());
      if (wasDaylightSavings != inDaylightSavings)
      {
        if (!wasDaylightSavings)
        {
          riseSetYear.setDstStart(date);
        }
        else
        {
          riseSetYear.setDstEnd(date);
        }
      }
      wasDaylightSavings = inDaylightSavings;

      final RawRiseSet riseSet = calculateRiseSet(location,
                                                  date,
                                                  zoneId,
                                                  inDaylightSavings,
                                                  TwilightType.NO);
      riseSetYear.addRiseSet(riseSet);

      if (twilight != null)
      {
        final RawRiseSet twilights = calculateRiseSet(location,
                                                      date,
                                                      zoneId,
                                                      inDaylightSavings,
                                                      twilight);
        riseSetYear.addTwilight(twilights);
      }

    }

    // Create band for twilight, clock-shift taken into account
    createBands(riseSetYear, DaylightBandType.twilight);
    // Create band, clock-shift taken into account
    createBands(riseSetYear, DaylightBandType.with_clock_shift);
    // Create band, without clock shift
    createBands(riseSetYear, DaylightBandType.without_clock_shift);

    return riseSetYear;

  }

  static void createBands(final RiseSetYearData riseSetYear,
                          final DaylightBandType daylightSavingsMode)
  {
    final List<DaylightBand> bands;
    if (daylightSavingsMode == DaylightBandType.twilight)
    {
      bands = RiseSetUtility.createDaylightBands(riseSetYear.getTwilights(),
                                                 daylightSavingsMode);
    }
    else
    {
      bands = RiseSetUtility.createDaylightBands(riseSetYear
        .getRiseSets(daylightSavingsMode.isAdjustedForDaylightSavings()),
                                                 daylightSavingsMode);
    }

    riseSetYear.addDaylightBands(bands);
  }

  /**
   * Creates daylight bands for plotting.
   *
   * @param riseSetData
   *        Rise/ set data for the year
   * @param daylightSavingsMode
   *        The daylight savings mode
   * @return List of daylight bands
   */
  static List<DaylightBand> createDaylightBands(final List<RiseSet> riseSetData,
                                                final DaylightBandType daylightSavingsMode)
  {
    final List<DaylightBand> bands = new ArrayList<DaylightBand>();

    DaylightBand baseBand = null;
    DaylightBand wrapBand = null;

    for (int i = 0; i < riseSetData.size(); i++)
    {
      final RiseSet riseSet = riseSetData.get(i);
      RiseSet riseSetYesterday = null;
      if (i > 0)
      {
        riseSetYesterday = riseSetData.get(i - 1);
      }
      RiseSet riseSetTomorrow = null;
      if (i < riseSetData.size() - 1)
      {
        riseSetTomorrow = riseSetData.get(i + 1);
      }

      final RiseSet[] riseSets = splitAtMidnight(riseSet);
      if (riseSets.length == 2)
      {
        // Create a new wrap band if necessary
        if (wrapBand == null)
        {
          wrapBand = new DaylightBand(daylightSavingsMode, bands.size());
          bands.add(wrapBand);
        }

        if (baseBand == null)
        {
          baseBand = new DaylightBand(daylightSavingsMode, bands.size());
          bands.add(baseBand);
        }

        // Split the daylight hours across two series
        baseBand.add(riseSets[0]);
        wrapBand.add(riseSets[1]);

        // Add a special "smoothing" value to the wrap band, if
        // necessary
        if (riseSetYesterday != null
            && riseSetYesterday.getRiseSetType() == RiseSetType.all_daylight)
        {
          wrapBand
            .add(riseSets[1].withNewRiseSetDate(riseSetYesterday.getDate()));
        }
      }
      else if (riseSets.length == 1)
      {
        // End the wrap band, if necessary
        if (wrapBand != null)
        {
          // Add a special "smoothing" value to the wrap band, if
          // necessary
          if (riseSetTomorrow != null
              && riseSetTomorrow.getRiseSetType() == RiseSetType.all_daylight
              && wrapBand.size() > 0)
          {
            wrapBand.add(wrapBand.getLastRiseSet()
              .withNewRiseSetDate(riseSetTomorrow.getDate()));
          }
          wrapBand = null;
        }

        // Create a new band if we are entering a period of
        // all-night-time from a period where there was daylight
        if (baseBand == null
            && riseSet.getRiseSetType() != RiseSetType.all_nighttime)
        {
          baseBand = new DaylightBand(daylightSavingsMode, bands.size());
          bands.add(baseBand);
        }
        else
        // Close the band if we are entering a period of all-night-time
        if (baseBand != null
            && riseSet.getRiseSetType() == RiseSetType.all_nighttime)
        {
          baseBand = null;
        }

        // Add sunset and sunrise as usual
        if (baseBand != null)
        {
          baseBand.add(riseSet);
        }
      }
    }

    return bands;

  }

  /**
   * Splits the given rise/ set at midnight.
   *
   * @param riseSet
   *        Rise/ set to split
   * @return Split RiseSet(s)
   */
  static RiseSet[] splitAtMidnight(final RiseSet riseSet)
  {
    if (riseSet == null)
    {
      return new RiseSet[0];
    }

    final LocalDateTime sunrise = riseSet.getSunrise();
    final LocalDateTime sunset = riseSet.getSunset();

    if (riseSet.getRiseSetType() != RiseSetType.partial && sunset.getHour() < 9)
    {
      return new RiseSet[] {
                             riseSet.withNewRiseSetTimes(sunrise
                               .toLocalTime(), RiseSet.JUST_BEFORE_MIDNIGHT),
                             riseSet.withNewRiseSetTimes(
                                                         RiseSet.JUST_AFTER_MIDNIGHT,
                                                         sunset.toLocalTime())
      };
    }
    else if (riseSet.getRiseSetType() != RiseSetType.partial
             && sunrise.getHour() > 15)
    {
      return new RiseSet[] {
                             riseSet.withNewRiseSetTimes(
                                                         RiseSet.JUST_AFTER_MIDNIGHT,
                                                         sunset.toLocalTime()),
                             riseSet.withNewRiseSetTimes(sunrise
                               .toLocalTime(), RiseSet.JUST_BEFORE_MIDNIGHT)
      };
    }
    else
    {
      return new RiseSet[] {
                             riseSet
      };
    }
  }

  @SuppressWarnings("boxing")
  private static RawRiseSet calculateRiseSet(final Location location,
                                             final LocalDate date,
                                             final ZoneId zoneId,
                                             final boolean inDaylightSavings,
                                             final TwilightType twilight)
  {
    final ZonedDateTime dayStart = date.atStartOfDay(zoneId);
    final double latitude;
    final double longitude;
    if (location == null)
    {
      latitude = 0D;
      longitude = 0D;
    }
    else
    {
      latitude = location.getPointLocation().getLatitude().getDegrees();
      longitude = location.getPointLocation().getLongitude().getDegrees();
    }
    final SunriseResult sunriseResult = SPA
      .calculateSunriseTransitSet(dayStart,
                                  latitude,
                                  longitude,
                                  DeltaT.estimate(date),
                                  toHorizon(twilight));

    final boolean usesDaylightSavings = !zoneId.getRules()
      .getTransitionRules().isEmpty();
    if (sunriseResult instanceof SunriseResult.RegularDay regularDay)
    {
      return new RawRiseSet(location,
                            date,
                            usesDaylightSavings && inDaylightSavings,
                            toHour(dayStart, regularDay.sunrise()),
                            toHour(dayStart, regularDay.sunset()));
    }
    else if (sunriseResult instanceof SunriseResult.AllDay)
    {
      return new RawRiseSet(location,
                            date,
                            usesDaylightSavings && inDaylightSavings,
                            Double.POSITIVE_INFINITY,
                            Double.POSITIVE_INFINITY);
    }
    else
    {
      return new RawRiseSet(location,
                            date,
                            usesDaylightSavings && inDaylightSavings,
                            Double.NEGATIVE_INFINITY,
                            Double.NEGATIVE_INFINITY);
    }

  }

  private static SPA.Horizon toHorizon(final TwilightType twilight)
  {
    if (twilight == null || twilight == TwilightType.NO)
    {
      return SPA.Horizon.SUNRISE_SUNSET;
    }
    switch (twilight)
    {
      case ASTRONOMICAL:
        return SPA.Horizon.ASTRONOMICAL_TWILIGHT;
      case NAUTICAL:
        return SPA.Horizon.NAUTICAL_TWILIGHT;
      case CIVIL:
      default:
        return SPA.Horizon.CIVIL_TWILIGHT;
    }
  }

  private static double toHour(final ZonedDateTime dayStart,
                               final ZonedDateTime eventTime)
  {
    return Duration.between(dayStart, eventTime).getSeconds() / 3600D;
  }

  private static List<LocalDate> getYearsDates(final int year)
  {
    final List<LocalDate> dates = new ArrayList<LocalDate>();
    LocalDate date = LocalDate.of(year, 1, 1);
    do
    {
      dates.add(date);
      date = date.plusDays(1);
    } while (!(date.getMonthValue() == 1 && date.getDayOfMonth() == 1));
    return dates;
  }

  private RiseSetUtility()
  {
    // Prevent instantiation
  }

}
