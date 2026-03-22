/*
 * Daylight Chart
 * http://sualeh.github.io/DaylightChart
 * Copyright (c) 2007-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package org.geoname.data;

import java.io.Serial;
import java.io.Serializable;

/**
 * Country, with ISO 3166 country code, and FIPS 10 country code.
 *
 * @author Sualeh Fatehi
 */
public final class Country implements Serializable, Comparable<Country> {

  /** Unknown country. */
  public static final Country UNKNOWN = new Country("", "");

  @Serial private static final long serialVersionUID = -5625327893850178062L;

  private final String name;
  private final String countryCode;

  /**
   * Constructor.
   *
   * @param countryCode Two letter ISO 3166 country code
   * @param name Country name
   */
  public Country(final String countryCode, final String name) {
    this.name = name;
    this.countryCode = countryCode;
  }

  /**
   * {@inheritDoc}
   *
   * @see java.lang.Comparable#compareTo(java.lang.Object)
   */
  @Override
  public int compareTo(final Country otherCountry) {
    return name.compareTo(otherCountry.name);
  }

  /**
   * {@inheritDoc}
   *
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if ((obj == null) || (getClass() != obj.getClass())) {
      return false;
    }
    final Country other = (Country) obj;
    if (name == null) {
      if (other.name != null) {
        return false;
      }
    } else if (!name.equals(other.name)) {
      return false;
    }
    if (countryCode == null) {
      if (other.countryCode != null) {
        return false;
      }
    } else if (!countryCode.equals(other.countryCode)) {
      return false;
    }
    return true;
  }

  /**
   * Gets the ISO 3166 2-letter country code.
   *
   * @return ISO 3166 2-letter country code
   */
  public String getCode() {
    return countryCode;
  }

  /**
   * @return the country name
   */
  public String getName() {
    return name;
  }

  /**
   * {@inheritDoc}
   *
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + (name == null ? 0 : name.hashCode());
    result = prime * result + (countryCode == null ? 0 : countryCode.hashCode());
    return result;
  }

  /**
   * {@inheritDoc}
   *
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return name;
  }
}
