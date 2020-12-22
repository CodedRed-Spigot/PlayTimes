package me.codedred.playtimes.models;

import java.lang.management.ManagementFactory;
import java.util.concurrent.TimeUnit;

import me.codedred.playtimes.data.DataManager;

public class Clock extends TimeFormat {
	
	public int getDays(long l) {
		if (l < 60) {
			return 0;
		}
		int minutes = (int) (l / 60);
		if (minutes < 1440) 
			return 0;
		return minutes / 1440;
	}
	
	public int getHours(long l) {
		if (l < 60) {
			return 0;
		}
		int minutes = (int) (l / 60);
		if (minutes < 60) 
			return 0;
		return minutes / 60;
	}
	
	public int getMins(long l) {
		if (l < 60) {
			return 0;
		}
		return (int) (l / 60);
	}
	
	public int getSecs(long l) {
		if (l <= 0) {
			return 0;
		}
		return (int) (l/1);
	}

	public String getUptime() {
		return getTime((int) TimeUnit.MILLISECONDS.toSeconds(ManagementFactory.getRuntimeMXBean().getUptime()));
	}
	
	
	public String getTime(long l) {
		if (l < 60) {
			return l + ( l == 1 ? getSecs() : getSec());
		}
		int minutes = (int) (l / 60);
		int s = 60 * minutes;
		int secondsLeft = (int) (l - s);
		if (minutes < 60) {
			if (secondsLeft > 0) {
				if (allowSeconds())
					return String.valueOf(minutes + (minutes == 1 ? getMin() : getMins()) + " " + secondsLeft + (secondsLeft == 1 ? getSec() : getSecs()));
				else
					return String.valueOf(minutes + (minutes == 1 ? getMin() : getMins()));
			}
			return String.valueOf(minutes + (minutes == 1 ? getMin() : getMins()));
		}
		if (minutes < 1440) {
			String time = "";
			int hours = minutes / 60;
			time = hours + (hours == 1 ? getHour() : getHours());
			int inMins = 60 * hours;
			int left = minutes - inMins;
			if (left >= 1) {
				time = time + " " + left + (left == 1 ? getMin() : getMins());
			}
			if (secondsLeft > 0) {
				if (allowSeconds())
					time = time + " " + secondsLeft + (secondsLeft == 1 ? getSec() : getSecs());
			}
			return time;
		}
		if (!DataManager.getInstance().cfg.getConfig().getBoolean("playtime.show-days")) {
			String time = "";
			int hours = minutes / 60;
			time = hours + (hours == 1 ? getHour() : getHours());
			int inMins = 60 * hours;
			int left = minutes - inMins;
			if (left >= 1) {
				time = time + " " + left + (left == 1 ? getMin() : getMins());
			}
			if (secondsLeft > 0) {
				if (allowSeconds())
					time = time + " " + secondsLeft + (secondsLeft == 1 ? getSec() : getSecs());
			}
			return time;
		}
		String time = "";
		int days = minutes / 1440;
		time = days + (days == 1 ? getDay() : getDays());
		int inMins = 1440 * days;
		int leftOver = minutes - inMins;
		if (leftOver >= 1) {
			if (leftOver < 60) {
				time = time + " " + leftOver + (leftOver == 1 ? getMin() : getMins());
			} else {
				int hours = leftOver / 60;
				time = time + " " + hours + (hours == 1 ? getHour() : getHours());
				int hoursInMins = 60 * hours;
				int minsLeft = leftOver - hoursInMins;
				if (leftOver >= 1) {
					time = time + " " + minsLeft + (minsLeft == 1 ? getMin() : getMins());
				}
			}
		}
		if (secondsLeft > 0) {
			if (allowSeconds())
				time = time + " " + secondsLeft + (secondsLeft == 1 ? getSec() : getSecs());
		}
		return time;
	}

}
