package daylightchart.service;

/** Application service registry for desktop and future web clients. */
public final class DaylightApplicationServices {

  private static final UserPreferencesService USER_PREFERENCES_SERVICE =
      new UserPreferencesService();

  public static UserPreferencesService preferences() {
    return USER_PREFERENCES_SERVICE;
  }

  private DaylightApplicationServices() {
    // Prevent instantiation
  }
}
