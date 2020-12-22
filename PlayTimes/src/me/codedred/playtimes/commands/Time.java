package me.codedred.playtimes.commands;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.codedred.playtimes.PlayTimes;
import me.codedred.playtimes.models.Clock;
import me.codedred.playtimes.models.NonOnlinePlayer;
import me.codedred.playtimes.models.OnlinePlayer;
import me.codedred.playtimes.utils.UUIDFetcher;

public class Time implements CommandExecutor {

	private PlayTimes plugin;
	public Time(PlayTimes plugin) {
		this.plugin = plugin;
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!sender.hasPermission("pt.use")) {
			sender.sendMessage(plugin.fp(plugin.getConfig().getString("messages.noPermission")));
			return true;
		}
		
		// command -> /playtimes [0 arguments]
		if (args.length == 0) {
			if (!(sender instanceof Player)) {
				Clock clock = new Clock();
				sender.sendMessage(plugin.f("&cThe console has been up for " + clock.getUptime()));
				return true;
			}
			OnlinePlayer player = new OnlinePlayer(plugin, (Player) sender);
			player.sendTimeTo(sender); 
			return true;
		}
		
		// command -> /playtimes <reload>
		if (args[0].equalsIgnoreCase("reload")) {
			if (!sender.hasPermission("pt.reload")) {
				sender.sendMessage(plugin.fp(plugin.getConfig().getString("messages.noPermission")));
				return true;
			}
			plugin.data.cfg.reloadConfig();
			sender.sendMessage(plugin.f("&9&lPlayTime Configuration Reloaded!"));
			return true;
		}
		// command -> /playtimes <player>
		else {
			if (!sender.hasPermission("pt.others")) {
				sender.sendMessage(plugin.fp(plugin.getConfig().getString("messages.noPermission")));
				return true;
			}
			UUID target = null;
			try {
				target = UUIDFetcher.getUUID(args[0]);
			} catch (Exception e) {
				sender.sendMessage(plugin.fp(plugin.getConfig().getString("messages.player-not-found")));
				return true;
			}
			if (target == null) {
				sender.sendMessage(plugin.fp(plugin.getConfig().getString("messages.player-not-found")));
				return true;
			}
			OfflinePlayer offlinePlayer = Bukkit.getPlayer(args[0]);
			NonOnlinePlayer player = new NonOnlinePlayer(plugin, offlinePlayer, target, args[0]);
			player.sendTimeTo(sender);
			
			return true;
		}
	}
}



















