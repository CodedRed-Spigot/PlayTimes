package me.codedred.playtimes.api;

import me.codedred.playtimes.statistics.StatManager;
import me.codedred.playtimes.statistics.StatisticType;
import me.codedred.playtimes.time.TimeManager;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;

import java.lang.reflect.Method;
import java.util.UUID;

public class TimelessPlayer {
	
	private final Player player;
	private final UUID uuid;

	private final StatManager stats;
	private final TimeManager timings;

	public TimelessPlayer(Player player) {
		this.player = player;
		this.uuid = player.getUniqueId();

		stats = StatManager.getInstance();
		timings = TimeManager.getInstance();
	}
	
	public TimelessPlayer(UUID player) {
		this.uuid = player;
		this.player = null;

		stats = StatManager.getInstance();
		timings = TimeManager.getInstance();
	}
	
	public String getPlayTime() {
		return timings.buildFormat(stats.getPlayerStat(uuid, StatisticType.PLAYTIME)/20);
	}

	public long getRawPlayTimeInSeconds() {
		return stats.getPlayerStat(uuid, StatisticType.PLAYTIME)/20;
	}
	
}
