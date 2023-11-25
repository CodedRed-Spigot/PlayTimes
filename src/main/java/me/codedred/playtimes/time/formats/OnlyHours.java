package me.codedred.playtimes.time.formats;

import me.codedred.playtimes.time.TimeConstants;
import me.codedred.playtimes.time.Timings;

public class OnlyHours implements Timings {

  @Override
  public String buildFormat(long time) {
    return (
      (Math.round((time / 3600.0) * 100d)) / 100d + TimeConstants.getHours()
    );
  }
}
