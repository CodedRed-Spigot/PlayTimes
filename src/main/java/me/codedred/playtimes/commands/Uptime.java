package me.codedred.playtimes.commands;

import me.clip.placeholderapi.PlaceholderAPI;
import me.codedred.playtimes.data.DataManager;
import me.codedred.playtimes.statistics.StatManager;
import me.codedred.playtimes.utils.ChatUtil;
import me.codedred.playtimes.utils.ServerUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class Uptime implements CommandExecutor {
	
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, String cmdL, String[] args) {
		if (cmdL.equalsIgnoreCase("uptime") || cmdL.equalsIgnoreCase("serveruptime") || cmdL.equalsIgnoreCase("serverupt")) {
			if (sender.hasPermission("pt.uptime")) {
				if (args.length >= 0) {
					 if (sender instanceof Player ) {
						 Player player = (Player) sender;
						 for (String msg :  DataManager.getInstance().getConfig().getStringList("uptime.message")) {
							 if (ServerUtils.hasPAPI())
									msg = PlaceholderAPI.setPlaceholders(player, msg);
								if (msg.contains("{\"text\":")) {
									Bukkit.dispatchCommand(Bukkit.getConsoleSender(), ("tellraw " + player + " " +  msg.replace("%serveruptime%",
                                            StatManager.getInstance().getUptime())));
								} else
									player.sendMessage(ChatUtil.format(msg.replace("%serveruptime%", StatManager.getInstance().getUptime())));
						 }
						 return true;
					 } else {
						 for (String msg :  DataManager.getInstance().getConfig().getStringList("uptime.message")) {
								sender.sendMessage(ChatUtil.format(msg.replace("%serveruptime%", StatManager.getInstance().getUptime())));
						 }
					 }
					
				 }
            } else
                ChatUtil.errno(sender, ChatUtil.ChatTypes.NO_PERMISSION);
            return true;
        }
		return false;
	}
}
