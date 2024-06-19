package me.codedred.playtimes.time.formats;

import me.codedred.playtimes.time.TimeConstants;
import me.codedred.playtimes.time.Timings;

public class OnlyHours implements Timings {

  @Override
  public String buildFormat(long time) {
    boolean round = TimeConstants.getRounded();
    double hours = time / 3600.0;

    if (round) {
      int roundedHours = (int) Math.round(hours);
      return roundedHours + TimeConstants.getHours();
    }

    hours = (Math.round(hours * 100d)) / 100d;
    return hours + TimeConstants.getHours();
  }
}
