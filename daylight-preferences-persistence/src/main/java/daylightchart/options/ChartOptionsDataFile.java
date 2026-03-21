package daylightchart.options;

import daylightchart.chart.options.ChartOptions;
import daylightchart.chart.options.ChartOptionsService;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.geoname.parser.UnicodeReader;
import tools.jackson.dataformat.yaml.YAMLMapper;

/** Persists chart options in a dedicated YAML file. */
public final class ChartOptionsDataFile extends BaseDataFile<ChartOptionsFileType, ChartOptions> {

  private static final YAMLMapper YAML_MAPPER = new YAMLMapper();
  private static final Logger LOGGER = Logger.getLogger(ChartOptionsDataFile.class.getName());
  private static final ChartOptionsService CHART_OPTIONS_SERVICE = new ChartOptionsService();

  public ChartOptionsDataFile(final Path settingsDirectory) {
    super(settingsDirectory, "chart-options.yaml", new ChartOptionsFileType());
  }

  @Override
  protected void load() {
    if (!exists()) {
      return;
    }

    final Path file = getFile();
    try {
      load(Files.newInputStream(file));
    } catch (final IOException e) {
      LOGGER.log(Level.WARNING, "Could not open chart options file, " + file, e);
    }
  }

  @Override
  protected void load(final InputStream... input) {
    if (input == null || input.length == 0) {
      return;
    }

    try (Reader reader = new UnicodeReader(input[0], "UTF-8")) {
      data = YAML_MAPPER.readValue(reader, ChartOptions.class);
    } catch (final Exception e) {
      LOGGER.log(Level.WARNING, "Could not read chart options", e);
      data = null;
    }
  }

  @Override
  protected void loadWithFallback() {
    load();
    if (data == null) {
      data = CHART_OPTIONS_SERVICE.createDefaultChartOptions();
      save();
    }
  }

  @Override
  protected void save() {
    try {
      delete();
      try (Writer writer = getFileWriter(getFile())) {
        if (writer == null) {
          return;
        }
        YAML_MAPPER.writeValue(writer, data);
      }
    } catch (final Exception e) {
      LOGGER.log(Level.WARNING, "Could not save chart options to " + getFile(), e);
    }
  }
}
