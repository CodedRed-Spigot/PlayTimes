package me.codedred.playtimes.api;

import java.lang.reflect.Method;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;

import me.codedred.playtimes.models.Clock;
import me.codedred.playtimes.utils.Statistics;

public class TimelessPlayer {
	
	private Player player;
	private UUID uuid;
	private Clock clock;
	
	public TimelessPlayer(Player player) {
		this.player = player;
		this.uuid = player.getUniqueId();
		this.clock = new Clock();
	}
	
	public TimelessPlayer(UUID player) {
		this.uuid = player;
		this.player = null;
		this.clock = new Clock();
	}
	
	public String getPlayTime() {
		if (isNewerVersion() && player != null)
			return clock.getTime((player.getStatistic(Statistic.valueOf("PLAY_ONE_MINUTE"))/20));
		return clock.getTime((Statistics.getPlayerStatistic(uuid, "PLAYTIME")/20));
	}
	
	public int getDays() {
		if (isNewerVersion() && player != null)
			return clock.getDays(player.getStatistic(Statistic.valueOf("PLAY_ONE_MINUTE"))/20);
		return clock.getDays((Statistics.getPlayerStatistic(uuid, "PLAYTIME")/20));
	}

	public int getHours() {
		if (isNewerVersion() && player != null)
			return clock.getHours(player.getStatistic(Statistic.valueOf("PLAY_ONE_MINUTE"))/20);
		return clock.getHours((Statistics.getPlayerStatistic(uuid, "PLAYTIME")/20));
	}

	public int getMins() {
		if (isNewerVersion() && player != null)
			return clock.getMins(player.getStatistic(Statistic.valueOf("PLAY_ONE_MINUTE"))/20);
		return clock.getMins((Statistics.getPlayerStatistic(uuid, "PLAYTIME")/20));
	}
	
	public int getSecs() {
		if (isNewerVersion() && player != null)
			return clock.getSecs(player.getStatistic(Statistic.valueOf("PLAY_ONE_MINUTE"))/20);
		return clock.getSecs((Statistics.getPlayerStatistic(uuid, "PLAYTIME")/20));
	}
	
	
	private static boolean isNewerVersion() {
		 try {
	            Class<?> class_Material = Material.class;
	            Method method = class_Material.getDeclaredMethod("matchMaterial", String.class, Boolean.TYPE);
	            return (method != null);
	        } catch(ReflectiveOperationException ex) {
	        	return false;	
	        }
	}
	
}
