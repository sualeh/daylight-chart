package daylightchart.service;

import daylightchart.daylightchart.layout.ChartFileType;
import daylightchart.daylightchart.layout.DaylightChartReport;
import daylightchart.options.Options;
import java.nio.file.Path;
import org.geoname.data.Location;

/** Service facade for chart report creation and export. */
public class DaylightChartReportService {

  public DaylightChartReport createReport(final Location location, final Options options) {
    return new DaylightChartReport(location, options);
  }

  public String createReportFileName(
      final Location location, final Options options, final ChartFileType chartFileType) {
    return createReport(location, options).getReportFileName(chartFileType);
  }

  public void writeReport(
      final Location location,
      final Options options,
      final Path file,
      final ChartFileType chartFileType) {
    createReport(location, options).write(file, chartFileType);
  }
}
