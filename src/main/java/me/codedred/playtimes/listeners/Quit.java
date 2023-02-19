package me.codedred.playtimes.listeners;

import me.codedred.playtimes.data.DataManager;
import me.codedred.playtimes.statistics.StatManager;
import me.codedred.playtimes.statistics.StatisticType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

public class Quit implements Listener {

	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		DataManager data = DataManager.getInstance();
		String playerName = event.getPlayer().getName().toLowerCase();
		if (data.getData().contains("blocked." + playerName)) {
			return;
		}

		StatManager statManager = StatManager.getInstance();
		UUID uuid = event.getPlayer().getUniqueId();
		long playtime = statManager.getPlayerStat(uuid, StatisticType.PLAYTIME);
		data.getData().set("leaderboard." + uuid, playtime);
		data.saveData();
	}
}
