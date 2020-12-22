package me.codedred.playtimes.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;


public class FirstJoinDate {
	
	public static String getJoinDate(Player p, String format) {
		if (p.hasPlayedBefore()) {
			String dateFormat = format;
		    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
			
			Calendar calendar = Calendar.getInstance();
	        calendar.setTimeInMillis(p.getFirstPlayed());
	        return simpleDateFormat.format(calendar.getTime());
		}
		else {
			// never joined
			return "";
		}
		
	}
	
	public static String getOfflineJoinDate(UUID p, String format) {
		if (Statistics.playerJoinedBefore(p)) {
			
			String dateFormat = format;
		    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
			
			Calendar calendar = Calendar.getInstance();
	        calendar.setTimeInMillis(Bukkit.getOfflinePlayer(p).getFirstPlayed());
	        return simpleDateFormat.format(calendar.getTime());
		}
		else {
			// never joined
			return "";
		}
		
	}

}
