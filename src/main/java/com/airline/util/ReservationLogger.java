package com.airline.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Utility class for logging reservation system activities.
 *
 * @author İshak Duran
 * @version 1.0
 */
public class ReservationLogger {

  private static final DateTimeFormatter TIMESTAMP_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

  private boolean enableLogging = true;

  /**
   * Logs an informational message.
   * 
   * @param message The message to log
   */
  public void logInfo(String message) {
    if (enableLogging) {
      System.out.println(formatLogMessage("INFO", message));
    }
  }

  /**
   * Logs a warning message.
   * 
   * @param message The message to log
   */
  public void logWarning(String message) {
    if (enableLogging) {
      System.out.println(formatLogMessage("WARN", message));
    }
  }

  /**
   * Logs an error message.
   * 
   * @param message The message to log
   */
  public void logError(String message) {
    if (enableLogging) {
      System.err.println(formatLogMessage("ERROR", message));
    }
  }

  /**
   * Logs an error message with exception details.
   * 
   * @param message   The message to log
   * @param throwable The exception to log
   */
  public void logError(String message, Throwable throwable) {
    if (enableLogging) {
      System.err.println(formatLogMessage("ERROR", message + " - " + throwable.getMessage()));
      throwable.printStackTrace();
    }
  }

  /**
   * Formats a log message with timestamp and level.
   * 
   * @param level   The log level
   * @param message The message
   * @return The formatted log message
   */
  private String formatLogMessage(String level, String message) {
    String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMAT);
    return String.format("[%s] %s - %s", timestamp, level, message);
  }

  /**
   * Enables or disables logging.
   * 
   * @param enabled true to enable logging, false to disable
   */
  public void setLoggingEnabled(boolean enabled) {
    this.enableLogging = enabled;
  }

  /**
   * Checks if logging is enabled.
   * 
   * @return true if logging is enabled
   */
  public boolean isLoggingEnabled() {
    return enableLogging;
  }
}
