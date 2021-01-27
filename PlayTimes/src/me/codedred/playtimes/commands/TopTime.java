package me.codedred.playtimes.commands;

import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.codedred.playtimes.PlayTimes;
import me.codedred.playtimes.models.Clock;
import me.codedred.playtimes.models.Leaderboard;
import me.codedred.playtimes.utils.FirstJoinDate;
import me.codedred.playtimes.utils.PAPIHolders;

public class TopTime implements CommandExecutor {

	private PlayTimes plugin;
	public TopTime(PlayTimes plugin) {
		this.plugin = plugin;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
			if (!sender.hasPermission("pt.top")) {
				sender.sendMessage(plugin.fp(plugin.getConfig().getString("messages.noPermission")));
				return true;
			}
			if (plugin.getConfig().getBoolean("top-playtime.enable-cooldown") && !sender.hasPermission("pt.block-cooldown")) {
				if (sender instanceof Player) {
					Player player = (Player) sender;
					if (plugin.cooldown.contains(player.getUniqueId())) {
						sender.sendMessage(plugin.fp(plugin.getConfig().getString("messages.cooldown")
								.replace("%timeleft%", Integer.toString(plugin.cooldown.left(player.getUniqueId())))));
						return true;
					}
					plugin.cooldown.add(player.getUniqueId(), System.currentTimeMillis() + (plugin.getConfig().getInt("top-playtime.cooldown-seconds") * 1000));
				}
			}
			Leaderboard board = new Leaderboard(plugin);
			 Map<String, Integer> map = board.getTopTen();
			 
			 if (map.size() == 0) {
				 sender.sendMessage(plugin.f("&cRejoin the server to fill the leaderboard!"));
				 return true;
			 }
			 String header = plugin.f(plugin.getConfig().getString("top-playtime.header"));
			 String footer = plugin.f(plugin.getConfig().getString("top-playtime.footer"));
			 String content = plugin.getConfig().getString("top-playtime.content");
			 if (plugin.hasPAPI() && sender instanceof Player) {
				 Player player = (Player) sender;
				 header = PAPIHolders.getHolders(player, header);
				 footer = PAPIHolders.getHolders(player, footer);
				 content = PAPIHolders.getHolders(player, content);
			 }
			 sender.sendMessage(header);
             for (int i = 0; i < map.size(); i++) {
                 UUID uuid = UUID.fromString(map.keySet().toArray()[i].toString());
                 String name = Bukkit.getServer().getOfflinePlayer(uuid).getName();
                 String time = map.values().toArray()[i].toString();
                 Clock clock = new Clock();
                 sender.sendMessage(plugin.f(content
                		 .replace("%place%", String.valueOf(i + 1))
                		 .replace("%player%", name)
                		 .replace("%time%", clock.getTime(Integer.valueOf(time)/20))
    					 .replace("%joindate%", FirstJoinDate.getOfflineJoinDate(uuid, plugin.getConfig().getString("date-format")))
                		 ));
             }
			 sender.sendMessage(footer);
			
	           
	        
	        return true;
	}
}
