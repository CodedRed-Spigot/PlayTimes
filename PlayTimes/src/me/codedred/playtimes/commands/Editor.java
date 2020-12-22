package me.codedred.playtimes.commands;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import me.codedred.playtimes.PlayTimes;
import me.codedred.playtimes.utils.UUIDFetcher;

public class Editor implements CommandExecutor {
	
	private PlayTimes plugin;
	public Editor(PlayTimes plugin) {
		this.plugin = plugin;
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender.hasPermission("pt.admin")) {
			switch(args.length) {
			case 2:
				if (args[0].equalsIgnoreCase("reset")) {
					if (args[1].equalsIgnoreCase("all")) {
						for (OfflinePlayer p : Bukkit.getOfflinePlayers()) {
							if (p.getStatistic(Statistic.PLAY_ONE_MINUTE) > 0)
								p.decrementStatistic(Statistic.PLAY_ONE_MINUTE, p.getStatistic(Statistic.PLAY_ONE_MINUTE));
						}
						
						for (String key : plugin.getData().getKeys(false))
							plugin.getData().set(key, null);
						plugin.data.data.saveConfig();
						
						sender.sendMessage(plugin.fp("&aEvery players playtime has been reset!"));
						sender.sendMessage(plugin.fp("&a&oI hope you knew what you were doing :^)"));
						return true;
					}
					UUID target = null;
					try {
						target = UUIDFetcher.getUUID(args[1]);
					} catch(Exception e) {
						sender.sendMessage(plugin.fp("&cPlayer not found/never joined server before!"));
						return true;
					}
					if (target == null) {
						sender.sendMessage(plugin.fp("&cPlayer not found/never joined server before!"));
						return true;
					}
					if (!plugin.getServer().getOfflinePlayer(target).hasPlayedBefore()) {
						sender.sendMessage(plugin.fp("&cPlayer not found/never joined server before!"));
						return true;
					}
					OfflinePlayer player = plugin.getServer().getOfflinePlayer(target);
					if (player.getStatistic(Statistic.PLAY_ONE_MINUTE) > 0)
						player.decrementStatistic(Statistic.PLAY_ONE_MINUTE, player.getStatistic(Statistic.PLAY_ONE_MINUTE));
					
					if (plugin.getData().contains(target.toString())) {
						plugin.getData().set(target.toString(), null);
						plugin.data.data.saveConfig();
					}
					
					sender.sendMessage(plugin.fp("&a" + player.getName() + "'s playtime has been reset!"));
					return true;
				}
			case 3:
				if (args[0].equalsIgnoreCase("set")) {
					UUID target = null;
					try {
						target = UUIDFetcher.getUUID(args[1]);
					} catch(Exception e) {
						sender.sendMessage(plugin.fp("&cPlayer not found/never joined server before!"));
						return true;
					}
					if (target == null) {
						sender.sendMessage(plugin.fp("&cPlayer not found/never joined server before!"));
						return true;
					}
					if (!plugin.getServer().getOfflinePlayer(target).hasPlayedBefore()) {
						sender.sendMessage(plugin.fp("&cPlayer not found/never joined server before!"));
						return true;
					}
					if (!isNum(args[2])) {
						sender.sendMessage(plugin.fp("&cIncorrect Usage!"));
						sender.sendMessage(plugin.f("&c/&7pte set <player> <amount-in-seconds> &f- sets players playtime"));
						return true;
					}
					if (Integer.parseInt(args[2]) < 0) {
						sender.sendMessage(plugin.fp("&cCannot set less than 0 time!"));
						sender.sendMessage(plugin.f("&c/&7pte set <player> <amount-in-seconds> &f- sets players playtime"));
						return true;
					}
					OfflinePlayer player = plugin.getServer().getOfflinePlayer(target);
					if (player.getStatistic(Statistic.PLAY_ONE_MINUTE) > 0)
						player.decrementStatistic(Statistic.PLAY_ONE_MINUTE, player.getStatistic(Statistic.PLAY_ONE_MINUTE));
					
					player.incrementStatistic(Statistic.PLAY_ONE_MINUTE, Integer.parseInt(args[2]));
					sender.sendMessage(plugin.fp("&a" + player.getName() + "'s playtime has been updated!"));
					return true;
				}
			default:
				sender.sendMessage(plugin.f("&4&lWARNING! There is no going back."));
				sender.sendMessage(plugin.f("&4&lUse commands carefully!"));
				sender.sendMessage(plugin.f("&c&lPlayTimes Editor Commands:"));
				sender.sendMessage(plugin.f("&c&l/&7pte reset <player> &f- resets players playtime"));
				sender.sendMessage(plugin.f("&c&l/&7pte reset all &f- resets every players playtime on the server"));
				sender.sendMessage(plugin.f("&c&l/&7pte set <player> <amount-in-seconds> &f- sets players playtime"));
				return true;
			}
		}
		sender.sendMessage(plugin.fp(plugin.getConfig().getString("messages.noPermission")));
		return false;
	}
	
	private boolean isNum(String num) {
		try {
			Integer.parseInt(num);
		} catch (Exception e) {
			return false;
		}
		return true;
	}

}
