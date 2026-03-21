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
package daylightchart;

import com.jgoodies.looks.plastic.PlasticLookAndFeel;
import com.jgoodies.looks.plastic.theme.LightGray;
import daylightchart.gui.DaylightChartGui;
import daylightchart.service.UserPreferencesService;
import java.util.Enumeration;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import javax.swing.UIManager;
import org.apache.commons.lang3.StringUtils;

/**
 * Main window.
 *
 * @author Sualeh Fatehi
 */
public final class Main {

  private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

  private static final String ENV_LOG_LEVEL = "DAYLIGHTCHART_LOG_LEVEL";

  /**
   * Main window.
   *
   * @param args Arguments
   */
  public static void main(final String[] args) {

    configureLogLevel();
    UserPreferencesService.preferences().initialize();

    // Set UI look and feel
    try {
      PlasticLookAndFeel.setPlasticTheme(new LightGray());
      UIManager.setLookAndFeel(new PlasticLookAndFeel());
    } catch (final Exception e) {
      LOGGER.log(Level.WARNING, "Cannot set look and feel");
    }

    new DaylightChartGui().setVisible(true);
  }

  private static void configureLogLevel() {
    final String logLevelString = System.getenv(ENV_LOG_LEVEL);
    if (StringUtils.isBlank(logLevelString)) {
      return;
    }

    try {
      setApplicationLogLevel(Level.parse(logLevelString.trim().toUpperCase()));
    } catch (final IllegalArgumentException e) {
      LOGGER.log(
          Level.WARNING, "Ignoring invalid log level in " + ENV_LOG_LEVEL + ": " + logLevelString);
    }
  }

  private static void setApplicationLogLevel(final Level logLevel) {
    final LogManager logManager = LogManager.getLogManager();
    for (final Enumeration<String> loggerNames = logManager.getLoggerNames();
        loggerNames.hasMoreElements(); ) {
      final String loggerName = loggerNames.nextElement();
      final Logger logger = logManager.getLogger(loggerName);
      if (logger == null) {
        continue;
      }
      logger.setLevel(null);
      for (final Handler handler : logger.getHandlers()) {
        handler.setLevel(logLevel);
      }
    }

    Logger.getLogger("").setLevel(logLevel);
  }

  private Main() {
    // No-op
  }
}
