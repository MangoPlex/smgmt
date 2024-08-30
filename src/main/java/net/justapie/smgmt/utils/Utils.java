package net.justapie.smgmt.utils;

import java.time.Duration;
import java.time.format.DateTimeParseException;

public class Utils {
  public static long parseDuration(String s) {
    try {
      return Duration.parse("P" + s).toMillis();
    } catch (ArithmeticException | DateTimeParseException e) {
      return 0;
    }
  }
}
