package me.codedred.playtimes.commands;

import me.codedred.playtimes.data.DataManager;
import me.codedred.playtimes.statistics.StatManager;
import me.codedred.playtimes.utils.*;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import me.codedred.playtimes.models.Leaderboard;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class TopTime implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
		if (!sender.hasPermission("pt.top")) {
			ChatUtil.errno(sender, ChatUtil.ChatTypes.NO_PERMISSION);
			return true;
		}

		if (!(sender instanceof Player player)) {
			sender.sendMessage("Must be a player to run this command.");
			return true;
		}

		DataManager data = DataManager.getInstance();

		if (data.getConfig().getBoolean("top-playtime.enable-cooldown") && !player.hasPermission("pt.block-cooldown")) {
			if (CoolDownUtil.contains(player.getUniqueId())) {
				String cooldownMessage = Objects.requireNonNull(data.getConfig().getString("messages.cooldown"))
						.replace("%timeleft%", Integer.toString(CoolDownUtil.left(player.getUniqueId())));
				sender.sendMessage(ChatUtil.formatWithPrefix(cooldownMessage));
				return true;
			}
			CoolDownUtil.add(player.getUniqueId(), System.currentTimeMillis() + (data.getConfig().getInt("top-playtime.cooldown-seconds") * 1000L));
		}

		Leaderboard board = new Leaderboard();
		Map<String, Integer> map = board.getTopTen();

		if (map.isEmpty()) {
			sender.sendMessage(ChatUtil.format("&cRejoin the server to fill the leaderboard!"));
			return true;
		}

		StatManager statManager = StatManager.getInstance();
		String header = ChatUtil.format(data.getConfig().getString("top-playtime.header"));
		String footer = ChatUtil.format(data.getConfig().getString("top-playtime.footer"));
		String content = data.getConfig().getString("top-playtime.content");

		if (ServerUtils.hasPAPI()) {
			header = PAPIHolders.getHolders(player, header);
			footer = PAPIHolders.getHolders(player, footer);
		}

		sender.sendMessage(header);

		for (int i = 0; i < map.size(); i++) {
			UUID uuid = UUID.fromString(map.keySet().toArray()[i].toString());
			org.bukkit.OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);

			if (ServerUtils.hasPAPI()) {
				content = PAPIHolders.getHolders(offlinePlayer, content);
			}

			String offlinePlayerName = offlinePlayer.getName();
			String place = String.valueOf(i + 1);
			String time = TimeFormatterUtil.secondsToFormattedTime(map.get(offlinePlayer.getUniqueId().toString()) / 20, "HH:mm:ss", Locale.US, "CDT");
			String joinDate = statManager.getJoinDate(uuid);

			if (time == null) {
				continue;
			}

			sender.sendMessage(ChatUtil.format(content
					.replace("%player%", offlinePlayerName)
					.replace("%place%", place)
					.replace("%time%", time)
					.replace("%joindate%", joinDate)));

			content = data.getConfig().getString("top-playtime.content");
		}

		sender.sendMessage(footer);
		return true;
	}



}
