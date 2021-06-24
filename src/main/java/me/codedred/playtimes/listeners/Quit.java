package me.codedred.playtimes.listeners;

import me.codedred.playtimes.data.DataManager;
import me.codedred.playtimes.statistics.StatManager;
import me.codedred.playtimes.statistics.StatisticType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import me.codedred.playtimes.PlayTimes;

import java.util.UUID;

public class Quit implements Listener {

	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		DataManager data = DataManager.getInstance();
		if (data.getData().contains("blocked." + event.getPlayer().getName().toLowerCase()))
			return;
		UUID uuid = event.getPlayer().getUniqueId();
		long time = StatManager.getInstance().getPlayerStat(uuid, StatisticType.PLAYTIME);
		data.getData().set("leaderboard." + uuid, time);
		data.saveData();
	}

}
