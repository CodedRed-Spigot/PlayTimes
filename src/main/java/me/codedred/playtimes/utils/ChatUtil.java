package me.codedred.playtimes.utils;

import me.codedred.playtimes.data.DataManager;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class ChatUtil {

  private static final DataManager DATA_MANAGER = DataManager.getInstance();
  private static final boolean IS_NEWER_VERSION = ServerUtils.isNewerVersion();

  public enum ChatTypes {
    NO_PERMISSION,
    PLAYER_NOT_FOUND,
    PLAYER_NEVER_PLAYED,
  }

  public static void errno(CommandSender sender, ChatTypes type) {
    switch (type) {
      case NO_PERMISSION -> sender.sendMessage(
        format(DATA_MANAGER.getConfig().getString("messages.noPermission"))
      );
      case PLAYER_NOT_FOUND -> sender.sendMessage(
        formatWithPrefix(
          DATA_MANAGER.getConfig().getString("messages.player-not-found")
        )
      );
      case PLAYER_NEVER_PLAYED -> sender.sendMessage(
        formatWithPrefix(
          DATA_MANAGER.getConfig().getString("messages.player-never-joined")
        )
      );
    }
  }

  public static String format(String msg) {
    msg = ChatColor.translateAlternateColorCodes('&', msg);
    if (IS_NEWER_VERSION) msg = HexUtil.hex(msg);
    return msg;
  }

  public static String formatWithPrefix(String msg) {
    msg =
      ChatColor.translateAlternateColorCodes(
        '&',
        DATA_MANAGER.getConfig().getString("prefix") + " " + msg
      );
    if (IS_NEWER_VERSION) msg = HexUtil.hex(msg);
    return msg;
  }
}
