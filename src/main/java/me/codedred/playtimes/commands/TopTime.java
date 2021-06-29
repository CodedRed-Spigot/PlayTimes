package me.codedred.playtimes.commands;

import me.codedred.playtimes.data.DataManager;
import me.codedred.playtimes.statistics.StatManager;
import me.codedred.playtimes.time.TimeManager;
import me.codedred.playtimes.utils.ChatUtil;
import me.codedred.playtimes.utils.CoolDownUtil;
import me.codedred.playtimes.utils.ServerUtils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import me.codedred.playtimes.models.Leaderboard;
import me.codedred.playtimes.utils.PAPIHolders;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.UUID;

public class TopTime implements CommandExecutor {
	
	@Override
	public boolean onCommand(CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
			if (!sender.hasPermission("pt.top")) {
				ChatUtil.errno(sender, ChatUtil.ChatTypes.NO_PERMISSION);
				return true;
			}
			DataManager data = DataManager.getInstance();
			if (data.getConfig().getBoolean("top-playtime.enable-cooldown") && !sender.hasPermission("pt.block-cooldown")) {
				if (sender instanceof Player) {
					Player player = (Player) sender;
					if (CoolDownUtil.contains(player.getUniqueId())) {
						sender.sendMessage(ChatUtil.formatWithPrefix(data.getConfig().getString("messages.cooldown")
								.replace("%timeleft%", Integer.toString(CoolDownUtil.left(player.getUniqueId())))));
						return true;
					}
					CoolDownUtil.add(player.getUniqueId(), System.currentTimeMillis() +
							(data.getConfig().getInt("top-playtime.cooldown-seconds") * 1000L));
				}
			}
			Leaderboard board = new Leaderboard();
			 Map<String, Integer> map = board.getTopTen();
			 
			 if (map.isEmpty()) {
				 sender.sendMessage(ChatUtil.format("&cRejoin the server to fill the leaderboard!"));
				 return true;
			 }

			 String header = ChatUtil.format(data.getConfig().getString("top-playtime.header"));
			 String footer = ChatUtil.format(data.getConfig().getString("top-playtime.footer"));
			 String content = data.getConfig().getString("top-playtime.content");

			StatManager statManager = StatManager.getInstance();
			TimeManager timeManager = TimeManager.getInstance();

		if (ServerUtils.hasPAPI() && sender instanceof Player) {
			Player player = (Player) sender;
			header = PAPIHolders.getHolders(player, header);
			footer = PAPIHolders.getHolders(player, footer);
		}

			 String original = content;
			 sender.sendMessage(header);
             for (int i = 0; i < map.size(); i++) {
                 UUID uuid = UUID.fromString(map.keySet().toArray()[i].toString());

				 if (ServerUtils.hasPAPI()) {
					 org.bukkit.OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
					 if (player != null)
						 content = PAPIHolders.getHolders(player, content);
				 }

                 String[][] replacements = {{"%player%", Bukkit.getServer().getOfflinePlayer(uuid).getName()},
						 					{"%place%",String.valueOf(i + 1)},
						 					{"%time%", timeManager.buildFormat(Integer.parseInt(map.values().toArray()[i].toString())/20) }};
                 for (String[] r : replacements)
                 	content = StringUtils.replace(content, r[0], r[1]);
                 content = StringUtils.replace(content,"%joindate%", statManager.getJoinDate(uuid));
                 sender.sendMessage(ChatUtil.format(content));
				 content = original;
             }
			 sender.sendMessage(footer);

	        return true;
	}
}
