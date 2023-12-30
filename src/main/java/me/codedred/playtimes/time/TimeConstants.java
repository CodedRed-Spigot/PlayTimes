package me.codedred.playtimes.time;

import me.codedred.playtimes.data.DataManager;
import org.bukkit.configuration.file.FileConfiguration;

public class TimeConstants {

  private static String second, seconds, minute, minutes, hour, hours, day, days;
  private static boolean rounded;

  static {
    reload();
  }

  public static void reload() {
    FileConfiguration data = DataManager.getInstance().getConfig();
    second = data.getString("playtime.name.second");
    seconds = data.getString("playtime.name.seconds");
    minute = data.getString("playtime.name.minute");
    minutes = data.getString("playtime.name.minutes");
    hour = data.getString("playtime.name.hour");
    hours = data.getString("playtime.name.hours");
    day = data.getString("playtime.name.day");
    days = data.getString("playtime.name.days");
    rounded = data.getBoolean("playtime.round-numbers");
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

  public static boolean getRounded() {
    return rounded;
  }
}
