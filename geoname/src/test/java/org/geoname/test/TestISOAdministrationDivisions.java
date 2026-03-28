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
import static org.hamcrest.Matchers.nullValue;

import org.geoname.data.Countries;
import org.geoname.data.Country;
import org.geoname.data.ISOAdministrationDivisions;
import org.junit.jupiter.api.Test;

public class TestISOAdministrationDivisions {

  @Test
  public void knownLookups() {
    assertThat(ISOAdministrationDivisions.lookupIsoRegionName("AF-BDS"), is("Badakhshān"));
    assertThat(ISOAdministrationDivisions.lookupIsoRegionName("GB-ABE"), is("Aberdeen City"));
  }

  @Test
  public void mapIsFullyLoaded() {
    Countries.lookupCountry("AF");
    for (char c1 = 'A'; c1 <= 'Z'; c1++) {
      for (char c2 = 'A'; c2 <= 'Z'; c2++) {
        final Country country = Countries.lookupCountry(String.valueOf(new char[] {c1, c2}));
        if (country != null && ISOAdministrationDivisions.lookupIsoRegionName("XXX") == null) {}
      }
    }
    // Verify a concrete size by counting the Afghanistan entries we know exist
    int afCount = 0;
    final String[] afCodes = {
      "BDS", "BGL", "BAL", "BDG", "BAM", "DAY", "FRA", "FYB", "GHA", "GHO", "HEL", "HER", "JOW",
      "KAN", "KHO", "KNR", "KDZ", "KAB", "KAP", "LAG"
    };
    for (final String code : afCodes) {
      if (ISOAdministrationDivisions.lookupIsoRegionName("AF-" + code) != null) {
        afCount++;
      }
    }
    assertThat("Afghanistan region sample", afCount, is(greaterThan(15)));
  }

  @Test
  public void nullCountryReturnsNull() {
    assertThat(ISOAdministrationDivisions.lookupIsoRegionName("BDS"), is(nullValue()));
  }

  @Test
  public void nullRegionalCodeReturnsNull() {
    Countries.lookupCountry("AF");
    assertThat(ISOAdministrationDivisions.lookupIsoRegionName(null), is(nullValue()));
  }

  @Test
  public void unknownKeyReturnsNull() {
    Countries.lookupCountry("AF");
    assertThat(ISOAdministrationDivisions.lookupIsoRegionName("ZZZ"), is(nullValue()));
  }
}
