package me.codedred.playtimes.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.clip.placeholderapi.PlaceholderAPI;
import me.codedred.playtimes.PlayTimes;
import me.codedred.playtimes.models.Clock;

public class Uptime implements CommandExecutor {

	private PlayTimes plugin;
	public Uptime(PlayTimes plugin) {
		this.plugin = plugin;
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String cmdL, String[] args) {
		if (cmdL.equalsIgnoreCase("uptime") || cmdL.equalsIgnoreCase("serveruptime") || cmdL.equalsIgnoreCase("serverupt")) {
			if (sender.hasPermission("pt.uptime")) {
				if (args.length >= 0) {
					Clock clock = new Clock();
					 if (sender instanceof Player ) {
						 Player player = (Player) sender;
						 for (String msg :  plugin.getConfig().getStringList("uptime.message")) {
							 if (plugin.hasPAPI()) 	
									msg = PlaceholderAPI.setPlaceholders(player, msg);
								if (msg.indexOf("{\"text\":") != -1 ) {
									Bukkit.dispatchCommand(Bukkit.getConsoleSender(), ("tellraw " + player + " " +  msg.replace("%serveruptime%",
											clock.getUptime())));
								} else
									player.sendMessage(plugin.f(msg.replace("%serveruptime%", clock.getUptime())));
						 }
						 return true;
					 } else {
						 for (String msg :  plugin.getConfig().getStringList("uptime.message")) {
								sender.sendMessage(plugin.f(msg.replace("%serveruptime%", clock.getUptime())));
						 }
					 }
					
				 }
					return true;
			} else {
				sender.sendMessage(plugin.fp(plugin.getConfig().getString("messages.noPermission")));
				return true;
			}
		}
		return false;
	}
}
