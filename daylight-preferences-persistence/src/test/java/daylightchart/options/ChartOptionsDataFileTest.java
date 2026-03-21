package daylightchart.options;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import java.awt.Color;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;

class ChartOptionsDataFileTest {

  @Test
  void shouldLoadFallbackPersistAndCreateChartOptionsFile() throws Exception {
    final Path settingsDirectory = Files.createTempDirectory("daylight-persistence-chart-options");

    final ChartOptionsDataFile chartOptionsDataFile = new ChartOptionsDataFile(settingsDirectory);

    assertThat(chartOptionsDataFile.getData(), is(notNullValue()));
    assertThat(Files.exists(settingsDirectory.resolve("chart-options.yaml")), is(true));
    assertThat(
        chartOptionsDataFile.getData().getPlotOptions().getBackgroundPaint(),
        is(instanceOf(Color.class)));

    final ChartOptionsDataFile reloaded = new ChartOptionsDataFile(settingsDirectory);
    assertThat(reloaded.getData(), is(notNullValue()));
    assertThat(
        reloaded.getData().getPlotOptions().getBackgroundPaint(),
        is(instanceOf(Color.class)));
  }
}
