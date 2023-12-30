package me.codedred.playtimes.time.formats;

import me.codedred.playtimes.time.TimeConstants;
import me.codedred.playtimes.time.Timings;

public class OnlyHours implements Timings {

  @Override
  public String buildFormat(long time) {
    double hours = time / 3600.0;
    hours =
      TimeConstants.getRounded()
        ? Math.round(hours)
        : (Math.round(hours * 100d)) / 100d;

    return hours + TimeConstants.getHours();
  }
}
