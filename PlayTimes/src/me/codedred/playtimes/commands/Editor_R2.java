package me.codedred.playtimes.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import me.codedred.playtimes.PlayTimes;

public class Editor_R2 implements CommandExecutor {
	
	private PlayTimes plugin;
	public Editor_R2(PlayTimes plugin) {
		this.plugin = plugin;
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender.hasPermission("pt.admin")) {
			sender.sendMessage(ChatColor.RED + "This feature is NOT available in your version of Spigot.");
			sender.sendMessage(ChatColor.RED + "I wish there was a way but currently this feature is only available in 1.16+");
			sender.sendMessage(ChatColor.RED + "~ CodedRed");
			return true;
		}
		sender.sendMessage(plugin.fp(plugin.getConfig().getString("messages.noPermission")));
		return false;
	}

}
