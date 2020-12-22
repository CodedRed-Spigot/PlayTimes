package me.codedred.playtimes.listeners;

import java.util.List;
import java.util.UUID;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import me.codedred.playtimes.PlayTimes;
import me.codedred.playtimes.utils.Statistics;

public class Join implements Listener {
	
	private PlayTimes plugin;
	public Join(PlayTimes plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		UUID player = event.getPlayer().getUniqueId();
		if (plugin.getConfig().getBoolean("hide-players-from-leaderboard.enabled")) {
			List<String> list = plugin.getConfig().getStringList("hide-players-from-leaderboard.players");
			if (!list.isEmpty())
				if (list.contains(event.getPlayer().getName())) {
					if (plugin.getData().contains(player.toString())) {
						plugin.getData().set(player.toString(), null);
						plugin.data.data.saveConfig();
					}
					return;
				}
		}
		if (!plugin.getData().contains(player.toString())) {
			long time = Statistics.getPlayerStatistic(player, "PLAYTIME");
			
			plugin.getData().set(player.toString(), time);
			plugin.data.data.saveConfig();
			
		}

	}
	
	

}
