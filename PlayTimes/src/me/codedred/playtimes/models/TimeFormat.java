package me.codedred.playtimes.models;

import me.codedred.playtimes.PlayTimes;

public class TimeFormat {

	private PlayTimes plugin;
	public TimeFormat() {
		plugin = PlayTimes.getPlugin(PlayTimes.class);
	}
	
	
	public String getSec() {
		return plugin.getConfig().getString("playtime.name.second");
	}
	
	
	public String getSecs() {
		return plugin.getConfig().getString("playtime.name.seconds");
	}
	
	
	public String getMin() {
		return plugin.getConfig().getString("playtime.name.minute");
	}
	
	
	public String getMins() {
		return plugin.getConfig().getString("playtime.name.minutes");
	}
	
	
	public String getHour() {
		return plugin.getConfig().getString("playtime.name.hour");
	}
	
	
	public String getHours() {
		return plugin.getConfig().getString("playtime.name.hours");
	}
	
	
	public String getDay() {
		return plugin.getConfig().getString("playtime.name.day");
	}
	
	
	public String getDays() {
		return plugin.getConfig().getString("playtime.name.days");
	}
	
	
	public boolean allowSeconds() {
		if (plugin.getConfig().getBoolean("playtime.show-seconds"))
			return true;
		return false;
	}
	
}
