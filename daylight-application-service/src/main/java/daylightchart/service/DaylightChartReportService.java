package daylightchart.service;

import daylightchart.chart.options.ChartOptions;
import daylightchart.chart.report.ChartFileType;
import daylightchart.chart.report.DaylightChartReport;
import daylightchart.options.Options;
import java.nio.file.Path;
import org.geoname.data.Location;

/** Service facade for chart report creation and export. */
public class DaylightChartReportService {

  private final UserPreferencesService userPreferencesService = new UserPreferencesService();

  public DaylightChartReport createReport(final Location location, final Options options) {
    final ChartOptions chartOptions = userPreferencesService.loadChartOptions();
    return new DaylightChartReport(location, options, chartOptions);
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
