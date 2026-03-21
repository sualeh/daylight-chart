package daylightchart.service;

import daylightchart.gui.actions.LocationFileType;
import daylightchart.options.BaseTypedFile;
import daylightchart.options.LocationsDataFile;
import daylightchart.options.Options;
import daylightchart.options.UserPreferences;
import java.nio.file.Path;
import java.util.Collection;
import org.geoname.data.Location;

/** Service facade for persisted user preferences and location lists. */
public class UserPreferencesService {

  public void addRecentLocation(final Location location) {
    UserPreferences.recentLocationsFile().add(location);
  }

  public void clear() {
    UserPreferences.clear();
  }

  public Collection<Location> getLocations() {
    return UserPreferences.locationsFile().getData();
  }

  public Collection<Location> getRecentLocations() {
    return UserPreferences.recentLocationsFile().getData();
  }

  public Path getScratchDirectory() {
    return UserPreferences.getScratchDirectory();
  }

  public Path getWorkingDirectory() {
    return loadOptions().getWorkingDirectory();
  }

  public void initialize() {
    UserPreferences.initialize(null);
  }

  public Collection<Location> loadLocations(
      final BaseTypedFile<LocationFileType> locationDataFile) {
    final LocationsDataFile locationsDataFile = new LocationsDataFile(locationDataFile);
    locationsDataFile.loadData();
    return locationsDataFile.getData();
  }

  public Options loadOptions() {
    return UserPreferences.optionsFile().getData();
  }

  public void saveLocations(
      final BaseTypedFile<LocationFileType> locationDataFile,
      final Collection<Location> locations) {
    final LocationsDataFile locationsDataFile = new LocationsDataFile(locationDataFile);
    locationsDataFile.save(locations);
  }

  public void saveLocations(final Collection<Location> locations) {
    UserPreferences.locationsFile().save(locations);
  }

  public void saveOptions(final Options options) {
    UserPreferences.optionsFile().save(options);
  }

  public void saveWorkingDirectory(final Path workingDirectory) {
    final Options options = loadOptions();
    options.setWorkingDirectory(workingDirectory);
    saveOptions(options);
  }
}
