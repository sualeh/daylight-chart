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
import java.util.Objects;

/**
 * An immutable administrative area (state, province, region, etc.) with a lookup code, display
 * name, type description, and data source.
 *
 * @author Sualeh Fatehi
 */
public final class AdministrativeArea implements Serializable {

  @Serial private static final long serialVersionUID = 1L;

  private final String code;
  private final String name;
  private final String type;
  private final AdministrativeAreaSource source;

  /**
   * Constructor.
   *
   * @param code Full lookup code (e.g. {@code "US-AL"} or {@code "US01"})
   * @param name Display name (e.g. {@code "Alabama"})
   * @param type Division type (e.g. {@code "state"}, {@code "province"}); may be empty
   * @param source Data source
   */
  public AdministrativeArea(
      final String code,
      final String name,
      final String type,
      final AdministrativeAreaSource source) {
    this.code = Objects.requireNonNull(code, "Code must not be null");
    this.name = Objects.requireNonNull(name, "Name must not be null");
    this.type = type != null ? type : "";
    this.source = Objects.requireNonNull(source, "Source must not be null");
  }

  /**
   * Full lookup code.
   *
   * @return Code (e.g. {@code "US-AL"})
   */
  public String getCode() {
    return code;
  }

  /**
   * Display name of the administrative area.
   *
   * @return Name (e.g. {@code "Alabama"})
   */
  public String getName() {
    return name;
  }

  /**
   * Type of administrative division (e.g. {@code "state"}, {@code "province"}).
   *
   * @return Type; never {@code null}, may be empty
   */
  public String getType() {
    return type;
  }

  /**
   * Data source from which this area was loaded.
   *
   * @return Source
   */
  public AdministrativeAreaSource getSource() {
    return source;
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof final AdministrativeArea other)) {
      return false;
    }
    return Objects.equals(code, other.code);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(code);
  }

  @Override
  public String toString() {
    return code + " (" + name + ")";
  }
}
