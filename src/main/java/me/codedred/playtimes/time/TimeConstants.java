package me.codedred.playtimes.time;

import me.codedred.playtimes.data.DataManager;

public class TimeConstants {

  private static String second, seconds, minute, minutes, hour, hours, day, days;

  static {
    reload();
  }

  public static void reload() {
    second =
      DataManager.getInstance().getConfig().getString("playtime.name.second");
    seconds =
      DataManager.getInstance().getConfig().getString("playtime.name.seconds");
    minute =
      DataManager.getInstance().getConfig().getString("playtime.name.minute");
    minutes =
      DataManager.getInstance().getConfig().getString("playtime.name.minutes");
    hour =
      DataManager.getInstance().getConfig().getString("playtime.name.hour");
    hours =
      DataManager.getInstance().getConfig().getString("playtime.name.hours");
    day = DataManager.getInstance().getConfig().getString("playtime.name.day");
    days =
      DataManager.getInstance().getConfig().getString("playtime.name.days");
  }

  public static String getSecond() {
    return second;
  }

  public static String getSeconds() {
    return seconds;
  }

  public static String getMinute() {
    return minute;
  }

  public static String getMinutes() {
    return minutes;
  }

  public static String getHour() {
    return hour;
  }

  public static String getHours() {
    return hours;
  }

  public static String getDay() {
    return day;
  }

  public static String getDays() {
    return days;
  }
}
