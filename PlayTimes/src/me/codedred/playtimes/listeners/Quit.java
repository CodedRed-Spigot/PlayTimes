package me.codedred.playtimes.listeners;

import java.util.UUID;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import me.codedred.playtimes.PlayTimes;
import me.codedred.playtimes.utils.Statistics;

public class Quit implements Listener {
	
	private PlayTimes plugin;
	public Quit(PlayTimes plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		UUID player = event.getPlayer().getUniqueId();
		
		long time = Statistics.getPlayerStatistic(player, "PLAYTIME");
		
		plugin.getData().set(player + "", time);
		plugin.data.data.saveConfig();
	}

}
