package me.codedred.playtimes.time;

import me.codedred.playtimes.data.DataManager;

public interface TimeRegistry {

    String SECOND = DataManager.getInstance().getConfig().getString("playtime.name.second");
    String SECONDS = DataManager.getInstance().getConfig().getString("playtime.name.seconds");
    String MINUTE = DataManager.getInstance().getConfig().getString("playtime.name.minute");
    String MINUTES = DataManager.getInstance().getConfig().getString("playtime.name.minutes");
    String HOUR = DataManager.getInstance().getConfig().getString("playtime.name.hour");
    String HOURS = DataManager.getInstance().getConfig().getString("playtime.name.hours");
    String DAY = DataManager.getInstance().getConfig().getString("playtime.name.day");
    String DAYS = DataManager.getInstance().getConfig().getString("playtime.name.days");

}
