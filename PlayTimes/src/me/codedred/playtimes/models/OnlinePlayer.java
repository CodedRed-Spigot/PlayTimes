package me.codedred.playtimes.models;

import org.bukkit.Bukkit;
import org.bukkit.Statistic;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.codedred.playtimes.PlayTimes;
import me.codedred.playtimes.utils.FirstJoinDate;
import me.codedred.playtimes.utils.PAPIHolders;

public class OnlinePlayer {

	private final Player player;
	
	private PlayTimes plugin;
	public OnlinePlayer(PlayTimes plugin, Player player) {
		this.plugin = plugin;
		this.player = player;
	}
	
	
	public void sendTimeTo(CommandSender target) {
		for (String msg : plugin.getConfig().getStringList("playtime.message")) {
			
			if (plugin.hasPAPI())
				msg = PAPIHolders.getHolders(player, msg);
			
			if (msg.indexOf("{\"text\":") != -1)
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "tellraw " + target + " " + translatedMsg(msg));
			else
				target.sendMessage(plugin.f(translatedMsg(msg)));
		}
	}
	
	
	public long getStat(Statistic stat) {
		return player.getStatistic(stat);
	}
	
	
	private String translatedMsg(String msg) {
		Clock clock = new Clock();
		if (plugin.isNewerVersion()) {
			return msg
					.replace("%time%", clock.getTime(getStat(Statistic.valueOf("PLAY_ONE_MINUTE"))/20))
					.replace("%player%", player.getName())
					.replace("%timesjoined%", String.valueOf(getStat(Statistic.LEAVE_GAME) + 1))
					.replace("%joindate%", FirstJoinDate.getJoinDate(player, plugin.getConfig().getString("date-format")))
					;
		}
		else {
			return msg
					.replace("%time%", clock.getTime(getStat(Statistic.valueOf("PLAY_ONE_TICK"))/20))
					.replace("%player%", player.getName())
					.replace("%timesjoined%", String.valueOf(getStat(Statistic.LEAVE_GAME) + 1))
					.replace("%joindate%", FirstJoinDate.getJoinDate(player, plugin.getConfig().getString("date-format")))
					;
		}
	}
	
}
