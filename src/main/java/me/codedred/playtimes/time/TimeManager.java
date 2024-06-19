package me.codedred.playtimes.time;

import me.codedred.playtimes.data.DataManager;
import me.codedred.playtimes.time.formats.*;

public class TimeManager {

  private static final TimeManager instance = new TimeManager();

  public static TimeManager getInstance() {
    return instance;
  }

  private Timings timings;

  public void registerTimings() {
    DataManager data = DataManager.getInstance();

    if (data.getConfig().getBoolean("playtime.only-hours")) {
      timings = new OnlyHours();
      return;
    }

    if (!data.getConfig().getBoolean("playtime.show-seconds")) {
      if (!data.getConfig().getBoolean("playtime.show-days")) {
        timings = new NoDaysNoSeconds();
        return;
      }
      timings = new NoSeconds();
      return;
    }
    if (!data.getConfig().getBoolean("playtime.show-days")) {
      timings = new NoDays();
      return;
    }

    timings = new NativeFormat();
  }

  private Timings getTimings() {
    return timings;
  }

  public String buildFormat(long time) {
    return getTimings().buildFormat(time);
  }
}
