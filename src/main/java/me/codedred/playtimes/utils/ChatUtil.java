package me.codedred.playtimes.utils;

import me.codedred.playtimes.data.DataManager;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class ChatUtil {

    public enum ChatTypes {

        NO_PERMISSION,
        PLAYER_NOT_FOUND,
        PLAYER_NEVER_PLAYED

    }

    public static void errno(CommandSender sender, ChatTypes type) {
        DataManager data = DataManager.getInstance();
        switch (type) {
            case NO_PERMISSION:
                sender.sendMessage(format(data.getConfig().getString("messages.noPermission")));
                break;
            case PLAYER_NOT_FOUND:
                sender.sendMessage(formatWithPrefix(data.getConfig().getString("messages.player-not-found")));
                break;
            case PLAYER_NEVER_PLAYED:
                sender.sendMessage(formatWithPrefix(data.getConfig().getString("messages.player-never-joined")));
                break;
        }
    }

    public static String format(String msg) {
        msg = ChatColor.translateAlternateColorCodes('&', msg);
        if (ServerUtils.isNewerVersion())
            msg = HexUtil.hex(msg);
        return msg;
    }

    public static String formatWithPrefix(String msg) {
        msg = ChatColor.translateAlternateColorCodes('&', DataManager.getInstance().getConfig().getString("prefix") + " " + msg);
        if (ServerUtils.isNewerVersion())
            msg = HexUtil.hex(msg);
        return msg;
    }
}
