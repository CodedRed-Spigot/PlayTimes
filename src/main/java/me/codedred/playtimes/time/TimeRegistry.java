package me.codedred.playtimes.time;

import me.codedred.playtimes.data.DataManager;

public interface TimeRegistry {

    final static String SECOND = DataManager.getInstance().getConfig().getString("playtime.name.second");
    final static String SECONDS = DataManager.getInstance().getConfig().getString("playtime.name.seconds");
    final static String MINUTE = DataManager.getInstance().getConfig().getString("playtime.name.minute");
    final static String MINUTES = DataManager.getInstance().getConfig().getString("playtime.name.minutes");
    final static String HOUR = DataManager.getInstance().getConfig().getString("playtime.name.hour");
    final static String HOURS = DataManager.getInstance().getConfig().getString("playtime.name.hours");
    final static String DAY = DataManager.getInstance().getConfig().getString("playtime.name.day");
    final static String DAYS = DataManager.getInstance().getConfig().getString("playtime.name.days");

}
