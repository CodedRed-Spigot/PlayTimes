package me.codedred.playtimes.models;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import me.codedred.playtimes.PlayTimes;
import me.codedred.playtimes.utils.FirstJoinDate;
import me.codedred.playtimes.utils.PAPIHolders;
import me.codedred.playtimes.utils.Statistics;

public class NonOnlinePlayer {

	private final UUID player;
	private final OfflinePlayer offlinePlayer; 
	private final String name;
	
	private PlayTimes plugin;
	public NonOnlinePlayer(PlayTimes plugin, OfflinePlayer player, UUID uuid, String name) {
		this.plugin = plugin;
		this.player = uuid;
		this.offlinePlayer = player;
		this.name = name;
	}
	
	
	public void sendTimeTo(CommandSender target) {
		for (String msg : plugin.getConfig().getStringList("playtime.message")) {
			
			if (plugin.hasPAPI())
				msg = PAPIHolders.getHolders(offlinePlayer, msg);
			
			if (msg.indexOf("{\"text\":") != -1)
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "tellraw " + target + " " + translatedMsg(msg));
			else
				target.sendMessage(plugin.f(translatedMsg(msg)));
		}
	}
	
	
	public long getStat(String stat) {
		return Statistics.getPlayerStatistic(player, stat);
	}
	
	
	private String translatedMsg(String msg) {
		Clock clock = new Clock();
		return msg
				.replace("%time%", clock.getTime(getStat("PLAYTIME")/20))
				.replace("%player%", name)
				.replace("%timesjoined%", String.valueOf(getStat("LEAVE") + 1))
				.replace("%joindate%", FirstJoinDate.getOfflineJoinDate(player, plugin.getConfig().getString("date-format")))
		;
	}
	
}
