package daylightchart.options;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import daylightchart.chart.options.ChartOptions;
import daylightchart.service.UserPreferencesService;
import java.nio.file.Files;
import java.nio.file.Path;
import org.geoname.data.Location;
import org.geoname.parser.LocationsListParser;
import org.junit.jupiter.api.Test;

class UserPreferencesServiceTest {

  @Test
  void shouldInitializeAndPersistOptions() throws Exception {
    final UserPreferencesService service = new UserPreferencesService();
    final Path settingsDirectory = Files.createTempDirectory("daylight-service-preferences");
    final Path workingDirectory = Files.createTempDirectory(settingsDirectory, "work");

    service.initialize(settingsDirectory);
    final Options options = service.loadOptions();
    options.setWorkingDirectory(workingDirectory);
    service.saveOptions(options);

    assertThat(service.loadOptions(), is(notNullValue()));
    assertThat(service.loadChartOptions(), is(notNullValue()));
    assertThat(service.loadOptions().getWorkingDirectory(), is(workingDirectory));
    assertThat(Files.exists(settingsDirectory.resolve("chart-options.yaml")), is(true));
    assertThat(service.getLocations().size(), is(greaterThan(0)));
  }

  @Test
  void shouldPersistChartOptions() throws Exception {
    final UserPreferencesService service = new UserPreferencesService();
    final Path settingsDirectory = Files.createTempDirectory("daylight-service-chart-options");
    service.initialize(settingsDirectory);

    final ChartOptions chartOptions = service.loadChartOptions();
    service.saveChartOptions(chartOptions);

    assertThat(service.loadChartOptions(), is(notNullValue()));
    assertThat(Files.exists(settingsDirectory.resolve("chart-options.yaml")), is(true));
  }

  @Test
  void shouldTrackRecentLocations() throws Exception {
    final UserPreferencesService service = new UserPreferencesService();
    service.initialize(Files.createTempDirectory("daylight-service-recent"));

    final Location location =
        LocationsListParser.parseLocation("Boston, MA;US;America/New_York;+4232-07104/");
    service.addRecentLocation(location);

    assertThat(service.getRecentLocations().contains(location), is(true));
  }
}
