package me.codedred.playtimes.commands;

import java.util.List;
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

  @Override
  public boolean onCommand(
    @NotNull CommandSender sender,
    @NotNull Command cmd,
    String cmdL,
    String[] args
  ) {
    if (
      !cmdL.equalsIgnoreCase("uptime") &&
      !cmdL.equalsIgnoreCase("serveruptime") &&
      !cmdL.equalsIgnoreCase("serverupt")
    ) {
      return false;
    }

    if (!sender.hasPermission("pt.uptime")) {
      ChatUtil.errno(sender, ChatUtil.ChatTypes.NO_PERMISSION);
      return true;
    }

    String uptime = StatManager.getInstance().getUptime();
    List<String> messages = DataManager
      .getInstance()
      .getConfig()
      .getStringList("uptime.message");

    for (String message : messages) {
      if (ServerUtils.hasPAPI() && sender instanceof Player player) {
        message = PlaceholderAPI.setPlaceholders(player, message);
      }

      if (message.contains("{\"text\":")) {
        String consoleCommand =
          "tellraw " +
          sender.getName() +
          " " +
          message.replace("%serveruptime%", uptime);
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), consoleCommand);
      } else {
        sender.sendMessage(
          ChatUtil.format(message.replace("%serveruptime%", uptime))
        );
      }
    }

    return true;
  }
}
