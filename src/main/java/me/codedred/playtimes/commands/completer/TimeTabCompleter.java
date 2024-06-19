package me.codedred.playtimes.commands.completer;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.metadata.MetadataValue;
import org.jetbrains.annotations.NotNull;

public class TimeTabCompleter implements TabCompleter {

  @Override
  public List<String> onTabComplete(
    @NotNull CommandSender sender,
    @NotNull Command command,
    @NotNull String alias,
    String[] args
  ) {
    List<String> completions = new ArrayList<>();

    if (args.length == 1) {
      if (sender.hasPermission("pt.use")) {
        completions.add("top");
        completions.addAll(getOnlinePlayerNames(sender));
      }
      if (sender.hasPermission("pt.block")) {
        completions.add("block");
        completions.add("unblock");
      }
      if (sender.hasPermission("pt.reload")) {
        completions.add("reload");
        completions.add("version");
        completions.add("help");
      }
    }

    return completions;
  }

  private List<String> getOnlinePlayerNames(CommandSender sender) {
    List<String> playerNames = new ArrayList<>();
    for (Player player : Bukkit.getOnlinePlayers()) {
      if (
        !isVanished(player) ||
        sender.equals(player) ||
        sender.hasPermission("pt.seevanished")
      ) {
        playerNames.add(player.getName());
      }
    }
    return playerNames;
  }

  private boolean isVanished(Player player) {
    for (MetadataValue meta : player.getMetadata("vanished")) {
      if (meta.asBoolean()) return true;
    }
    return false;
  }
}
