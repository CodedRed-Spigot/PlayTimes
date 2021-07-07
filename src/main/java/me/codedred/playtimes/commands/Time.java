package me.codedred.playtimes.commands;

import me.codedred.playtimes.PlayTimes;
import me.codedred.playtimes.data.DataManager;
import me.codedred.playtimes.data.Debugger;
import me.codedred.playtimes.server.ServerManager;
import me.codedred.playtimes.statistics.StatManager;
import me.codedred.playtimes.time.TimeManager;
import me.codedred.playtimes.utils.ChatUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import me.codedred.playtimes.player.OnlinePlayer;
import me.codedred.playtimes.player.OfflinePlayer;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Time implements CommandExecutor {
	
	public boolean onCommand(CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
		DataManager data = DataManager.getInstance();
		if (!sender.hasPermission("pt.use")) {
			ChatUtil.errno(sender, ChatUtil.ChatTypes.NO_PERMISSION);
			return true;
		}
		
		// command -> /playtimes [0 arguments]
		if (args.length == 0) {
			if (!(sender instanceof Player)) {
				StatManager stats = StatManager.getInstance();
				sender.sendMessage(ChatUtil.format("&cThe console has been up for " + stats.getUptime()));
				return true;
			}
			OnlinePlayer player = new OnlinePlayer((Player) sender);
			player.sendMessageToTarget();
			return true;
		}



		// command -> /playtimes reload
		if (args[0].equalsIgnoreCase("reload")) {
			if (!sender.hasPermission("pt.reload")) {
				ChatUtil.errno(sender, ChatUtil.ChatTypes.NO_PERMISSION);
				return true;
			}
			Debugger debug = new Debugger();
			debug.execute();
			data.reloadAll();
			TimeManager.getInstance().registerTimings();
			sender.sendMessage(ChatUtil.format("&9&lPlayTime Configurations Reloaded!"));
			return true;
		}




		// command -> /playtimes top
		else if (args[0].equalsIgnoreCase("top")) {
			Bukkit.dispatchCommand(sender, "toppt");
			return true;
		}



		// command -> /playtimes block <player>
		else if (args[0].equalsIgnoreCase("block")) {
			if (!sender.hasPermission("pt.block")) {
				ChatUtil.errno(sender, ChatUtil.ChatTypes.NO_PERMISSION);
				return true;
			}
			if (args.length < 2) {
				sender.sendMessage(ChatUtil.formatWithPrefix("&c&lIncorrect Usage&c, try &7/pt block <player>"));
				return true;
			}
			if (data.getData().contains("blocked." + args[1].toLowerCase())) {
				sender.sendMessage(ChatUtil.formatWithPrefix("&cUser is already blocked!"));
				sender.sendMessage(ChatUtil.formatWithPrefix("&c&oMaybe try, &7/pt unblock <player>"));
				return true;
			}
			UUID uuid;
			if (Bukkit.getPlayer(args[1]) == null) {
				try {
					uuid = ServerManager.getInstance().getUUID(args[1].toLowerCase());
				} catch (NullPointerException e) {
					ChatUtil.errno(sender, ChatUtil.ChatTypes.PLAYER_NOT_FOUND);
					return true;
				}
			}
			else
				uuid = Bukkit.getPlayer(args[1]).getUniqueId();

			
			data.getData().set("blocked." + args[1].toLowerCase(), uuid.toString());

			if (data.getData().contains("leaderboard." + uuid))
				data.getData().set("leaderboard." + uuid, null);

			data.saveData();
			sender.sendMessage(ChatUtil.formatWithPrefix("&aUser &c&lBLOCKED &afrom leaderboards!"));
			return true;
		}




		// command -> /playtimes block <player>
		else if (args[0].equalsIgnoreCase("unblock")) {
			if (!sender.hasPermission("pt.block")) {
				ChatUtil.errno(sender, ChatUtil.ChatTypes.NO_PERMISSION);
				return true;
			}
			if (args.length < 2) {
				sender.sendMessage(ChatUtil.formatWithPrefix("&c&lIncorrect Usage&c, try &7/pt unblock <player>"));
				return true;
			}
			if (!data.getData().contains("blocked." + args[1].toLowerCase())) {
				sender.sendMessage(ChatUtil.formatWithPrefix("&cUser is not blocked!"));
				sender.sendMessage(ChatUtil.formatWithPrefix("&c&oMaybe try, &7/pt block <player>"));
				return true;
			}
			data.getData().set("blocked." + args[1].toLowerCase(), null);
			data.saveData();

			sender.sendMessage(ChatUtil.formatWithPrefix("&aUser &b&lUNBLOCKED &afrom leaderboards!"));
			return true;



		}


		// command -/playtimes holo
		/*else if (args[0].equalsIgnoreCase("holo")) {
			if (args.length != 3) {
				switch(args[2].toUpperCase()) {
					case "CUSTOM":

						return true;
					case "TOP10":
						return true;
					case "TOP5":
						return true;
					case "TOP3":
						return true;
				}
			}

		}*/




			// command -> /playtimes <player>
		if (!sender.hasPermission("pt.others")) {
			ChatUtil.errno(sender, ChatUtil.ChatTypes.NO_PERMISSION);
			return true;
		}

		new BukkitRunnable() {
			@Override
			public void run() {
				Pattern p = Pattern.compile("[^a-z0-9_]", Pattern.CASE_INSENSITIVE);
				Matcher m = p.matcher(args[0]);
				boolean b = m.find();

				if (b) {
					ChatUtil.errno(sender, ChatUtil.ChatTypes.PLAYER_NOT_FOUND);
					return;
				}

				UUID target;
				if (Bukkit.getPlayer(args[0]) == null) {
					target = ServerManager.getInstance().getUUID(args[0]);
					if (target == null) {
						ChatUtil.errno(sender, ChatUtil.ChatTypes.PLAYER_NOT_FOUND);
						return;
					}
				}
				else
					target = Bukkit.getPlayer(args[0]).getUniqueId();

				org.bukkit.OfflinePlayer offlinePlayer = Bukkit.getServer().getOfflinePlayer(target);
				OfflinePlayer player = new OfflinePlayer(sender, target, offlinePlayer.getName());
				player.sendMessageToTarget();


			}
		}.runTaskAsynchronously(PlayTimes.getPlugin(PlayTimes.class));
		return true;
	}
}
