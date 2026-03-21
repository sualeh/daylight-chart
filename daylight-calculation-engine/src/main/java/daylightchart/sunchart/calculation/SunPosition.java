package daylightchart.sunchart.calculation;


import java.io.Serializable;
import java.util.Objects;
import java.time.LocalDate;
import java.time.LocalDateTime;

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

  /**
   * Solar ephemerides at a given date and time.
   *
   * @param dateTime
   * @param altitude
   * @param azimuth
   */
  public SunPosition(final LocalDateTime dateTime,
                     final double altitude,
                     final double azimuth)
  {
    this.dateTime = dateTime;
    this.altitude = altitude;
    this.azimuth = azimuth;
  }

  /**
   * {@inheritDoc}
   *
   * @see java.lang.Comparable#compareTo(java.lang.Object)
   */
  @Override
  public int compareTo(final SunPosition o)
  {
    return dateTime.compareTo(o.dateTime);
  }

  /**
   * {@inheritDoc}
   *
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(final Object obj)
  {
    if (this == obj)
    {
      return true;
    }
    if (!(obj instanceof SunPosition))
    {
      return false;
    }
    final SunPosition other = (SunPosition) obj;
    return Objects.equals(dateTime, other.dateTime)
           && Double.compare(altitude, other.altitude) == 0
           && Double.compare(azimuth, other.azimuth) == 0;
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

  /**
   * {@inheritDoc}
   *
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode()
  {
    return Objects.hash(dateTime, altitude, azimuth);
  }

  /**
   * {@inheritDoc}
   *
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString()
  {
    return "SunPosition[dateTime=" + dateTime + ", altitude=" + altitude
           + ", azimuth=" + azimuth + "]";
  }

}
