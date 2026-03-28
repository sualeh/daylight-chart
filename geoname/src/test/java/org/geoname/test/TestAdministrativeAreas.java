/*
 * Daylight Chart
 * http://sualeh.github.io/DaylightChart
 * Copyright (c) 2007-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package org.geoname.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

import org.geoname.data.AdministrativeArea;
import org.geoname.data.AdministrativeAreaSource;
import org.geoname.data.AdministrativeAreas;
import org.junit.jupiter.api.Test;

public class TestAdministrativeAreas {

  @Test
  public void afghanistandRegionSampleCount() {
    final String[] afCodes = {
      "BDS", "BGL", "BAL", "BDG", "BAM", "DAY", "FRA", "FYB", "GHA", "GHO", "HEL", "HER", "JOW",
      "KAN", "KHO", "KNR", "KDZ", "KAB", "KAP", "LAG"
    };
    long found =
        java.util.Arrays.stream(afCodes)
            .map(code -> AdministrativeAreas.lookupAdministrativeArea("AF-" + code))
            .filter(a -> a != null)
            .count();
    assertThat("Afghanistan region sample", (int) found, is(greaterThan(15)));
  }

  @Test
  public void blankCodeReturnsNull() {
    assertThat(AdministrativeAreas.lookupAdministrativeArea("  "), is(nullValue()));
  }

  @Test
  public void fips10Lookup() {
    final AdministrativeArea area = AdministrativeAreas.lookupAdministrativeArea("US01");
    assertThat(area, is(notNullValue()));
    assertThat(area.getName(), is("Alabama"));
    assertThat(area.getSource(), is(AdministrativeAreaSource.FIPS10));
  }

  @Test
  public void isoLookupByFullCode() {
    final AdministrativeArea area = AdministrativeAreas.lookupAdministrativeArea("AF-BDS");
    assertThat(area, is(notNullValue()));
    assertThat(area.getName(), is("Badakhshān"));
    assertThat(area.getType(), is("Province"));
    assertThat(area.getSource(), is(AdministrativeAreaSource.ISO_3166_2));
  }

  @Test
  public void isoLookupGbEntry() {
    final AdministrativeArea area = AdministrativeAreas.lookupAdministrativeArea("GB-ABE");
    assertThat(area, is(notNullValue()));
    assertThat(area.getName(), is("Aberdeen City"));
    assertThat(area.getSource(), is(AdministrativeAreaSource.ISO_3166_2));
  }

  @Test
  public void isoLookupUsState() {
    final AdministrativeArea area = AdministrativeAreas.lookupAdministrativeArea("US-AL");
    assertThat(area, is(notNullValue()));
    assertThat(area.getName(), is("Alabama"));
    assertThat(area.getType(), is("state"));
    assertThat(area.getSource(), is(AdministrativeAreaSource.ISO_3166_2));
  }

  @Test
  public void mapContainsBothSources() {
    long isoCount =
        java.util.stream.Stream.of("US-AL", "US-CA", "US-NY", "GB-ABE", "AF-BDS", "DE-BY", "FR-ARA")
            .map(AdministrativeAreas::lookupAdministrativeArea)
            .filter(a -> a != null && a.getSource() == AdministrativeAreaSource.ISO_3166_2)
            .count();
    assertThat("ISO entries loaded", (int) isoCount, is(greaterThan(5)));

    long fipsCount =
        java.util.stream.Stream.of("US01", "US02", "AF01", "GB01")
            .map(AdministrativeAreas::lookupAdministrativeArea)
            .filter(a -> a != null && a.getSource() == AdministrativeAreaSource.FIPS10)
            .count();
    assertThat("FIPS10 entries loaded", (int) fipsCount, is(greaterThan(2)));
  }

  @Test
  public void nullCodeReturnsNull() {
    assertThat(AdministrativeAreas.lookupAdministrativeArea(null), is(nullValue()));
  }

  @Test
  public void partialCodeWithoutCountryPrefixReturnsNull() {
    assertThat(AdministrativeAreas.lookupAdministrativeArea("BDS"), is(nullValue()));
  }

  @Test
  public void unknownCodeReturnsNull() {
    assertThat(AdministrativeAreas.lookupAdministrativeArea("ZZ-ZZZZ"), is(nullValue()));
  }
}
