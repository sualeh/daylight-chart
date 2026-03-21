package daylightchart.sunchart.calculation;


import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import org.apache.commons.lang3.builder.CompareToBuilder;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * Solar ephemerides at a given date and time.
 *
 * @author sfatehi
 */
public class SunPosition
  implements Serializable, Comparable<SunPosition>
{

  private static final long serialVersionUID = -7394558865293598834L;

  private final LocalDateTime dateTime;
  private final double altitude;
  private final double azimuth;
  private final double declination;
  private final double equationOfTime;
  private final double hourAngle;
  private final double rightAscension;

  /**
   * Solar ephemerides at a given date and time.
   *
   * @param dateTime
   * @param altitude
   * @param azimuth
   * @param declination
   * @param equationOfTime
   * @param hourAngle
   * @param rightAscension
   */
  public SunPosition(final LocalDateTime dateTime,
                     final double altitude,
                     final double azimuth,
                     final double declination,
                     final double equationOfTime,
                     final double hourAngle,
                     final double rightAscension)
  {
    this.dateTime = dateTime;
    this.altitude = altitude;
    this.azimuth = azimuth;
    this.declination = declination;
    this.equationOfTime = equationOfTime;
    this.hourAngle = hourAngle;
    this.rightAscension = rightAscension;
  }

  /**
   * {@inheritDoc}
   *
   * @see java.lang.Comparable#compareTo(java.lang.Object)
   */
  @Override
  public int compareTo(final SunPosition o)
  {
    return CompareToBuilder.reflectionCompare(this, o);
  }

  /**
   * {@inheritDoc}
   *
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(final Object obj)
  {
    return EqualsBuilder.reflectionEquals(this, obj);
  }

  /**
   * @return the altitude
   */
  public double getAltitude()
  {
    return altitude;
  }

  /**
   * @return the azimuth
   */
  public double getAzimuth()
  {
    return azimuth;
  }

  /**
   * @return the date
   */
  public LocalDate getDate()
  {
    return dateTime.toLocalDate();
  }

  /**
   * @return the dateTime
   */
  public LocalDateTime getDateTime()
  {
    return dateTime;
  }

  public double getDeclination()
  {
    return declination;
  }

  public double getEquationOfTime()
  {
    return equationOfTime;
  }

  public double getHourAngle()
  {
    return hourAngle;
  }

  public double getRightAscension()
  {
    return rightAscension;
  }

  /**
   * @return the time
   */
  public LocalTime getTime()
  {
    return dateTime.toLocalTime();
  }

  /**
   * {@inheritDoc}
   *
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode()
  {
    return HashCodeBuilder.reflectionHashCode(this);
  }

  /**
   * {@inheritDoc}
   *
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString()
  {
    return ToStringBuilder.reflectionToString(this,
                                              ToStringStyle.MULTI_LINE_STYLE);
  }

}
