package me.codedred.playtimes.listeners;

import me.codedred.playtimes.data.DataManager;
import me.codedred.playtimes.statistics.StatManager;
import me.codedred.playtimes.statistics.StatisticType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.Objects;
import java.util.UUID;

public class Join implements Listener {

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		DataManager data = DataManager.getInstance();
		if (data.getData().contains("blocked." + event.getPlayer().getName().toLowerCase()))
			return;

		UUID uuid = event.getPlayer().getUniqueId();
		if (data.getData().getConfigurationSection("leaderboard").contains(uuid.toString())) {
			return;
		}
		long time = StatManager.getInstance().getPlayerStat(uuid, StatisticType.PLAYTIME);
		data.getData().set("leaderboard." + uuid, time);
		data.saveData();

	}

}
