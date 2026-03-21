package daylightchart.service;

/** Application service registry for desktop and future web clients. */
public final class DaylightApplicationServices {

  private static final UserPreferencesService USER_PREFERENCES_SERVICE =
      new UserPreferencesService();
  private static final DaylightChartReportService DAYLIGHT_CHART_REPORT_SERVICE =
      new DaylightChartReportService();

  public static UserPreferencesService preferences() {
    return USER_PREFERENCES_SERVICE;
  }

  public static DaylightChartReportService reports() {
    return DAYLIGHT_CHART_REPORT_SERVICE;
  }

  private DaylightApplicationServices() {
    // Prevent instantiation
  }
}
