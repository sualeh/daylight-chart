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
package daylightchart.options.service;

import daylightchart.options.persistence.LocationsDataFile;
import daylightchart.options.persistence.OptionsDataFile;
import daylightchart.options.persistence.RecentLocationsDataFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * User preferences for the GUI.
 *
 * @author Sualeh Fatehi
 */
public final class UserPreferences {

  private static final Logger LOGGER = Logger.getLogger(UserPreferences.class.getName());

  private static final Path scratchDirectory;

  private static LocationsDataFile locationsFile;
  private static RecentLocationsDataFile recentLocationsFile;
  private static OptionsDataFile optionsFile;

  static {
    scratchDirectory = Path.of(System.getProperty("java.io.tmpdir"), ".");
    validateDirectory(scratchDirectory);

    initialize(PersistenceConfigurationService.configuration().resolvePreferencesDirectory());
  }

  /** Clears all user preferences. */
  public static void clear() {
    optionsFile.delete();
    locationsFile.delete();
    recentLocationsFile.delete();

    initialize(optionsFile.getDirectory());
  }

  /**
   * Set the location of the settings directory.
   *
   * @param settingsDir Location of the settings directory
   */
  public static void initialize(final Path settingsDir) {
    final Path settingsDirectory;
    if (settingsDir == null) {
      settingsDirectory =
          PersistenceConfigurationService.configuration().resolvePreferencesDirectory();
    } else {
      settingsDirectory = settingsDir;
    }

    try {
      Files.createDirectories(settingsDirectory);
      validateDirectory(settingsDirectory);
      LOGGER.fine("Created settings directory " + settingsDirectory);
    } catch (final IOException e) {
      LOGGER.log(Level.SEVERE, "Cannot create settings directory " + settingsDirectory, e);
    }

    optionsFile = new OptionsDataFile(settingsDirectory);
    locationsFile = new LocationsDataFile(settingsDirectory);
    recentLocationsFile = new RecentLocationsDataFile(settingsDirectory);
  }

  /**
   * Locations file.
   *
   * @return Locations file.
   */
  public static LocationsDataFile locationsFile() {
    return locationsFile;
  }

  /**
   * Options file.
   *
   * @return Options file.
   */
  public static OptionsDataFile optionsFile() {
    return optionsFile;
  }

  /**
   * Recent locations file.
   *
   * @return Recent locations file.
   */
  public static RecentLocationsDataFile recentLocationsFile() {
    return recentLocationsFile;
  }

  private static void validateDirectory(final Path directory) {
    final boolean isDirectoryValid =
        directory != null && Files.isDirectory(directory) && Files.isWritable(directory);
    if (!isDirectoryValid) {
      throw new IllegalArgumentException("Directory is not writable - " + directory);
    }
  }

  private UserPreferences() {
    // Prevent external instantiation
  }
}
