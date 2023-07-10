package me.codedred.playtimes.commands;

import me.codedred.playtimes.PlayTimes;
import me.codedred.playtimes.data.DataManager;
import me.codedred.playtimes.player.OfflinePlayer;
import me.codedred.playtimes.player.OnlinePlayer;
import me.codedred.playtimes.server.ServerManager;
import me.codedred.playtimes.statistics.StatManager;
import me.codedred.playtimes.time.TimeConstants;
import me.codedred.playtimes.time.TimeManager;
import me.codedred.playtimes.utils.ChatUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Time implements CommandExecutor {

    private final DataManager data = DataManager.getInstance();

    public boolean onCommand(CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        if (!sender.hasPermission("pt.use")) {
            ChatUtil.errno(sender, ChatUtil.ChatTypes.NO_PERMISSION);
            return true;
        }

        switch (args.length > 0 ? args[0].toLowerCase() : "") {
            case "" -> handlePlayerCommand(sender);
            case "reload" -> handleReloadCommand(sender);
            case "top" -> handleTopCommand(sender);
            case "block" -> handleBlockCommand(sender, args, true);
            case "unblock" -> handleBlockCommand(sender, args, false);
            case "debug" -> handleDebugCommand(sender);
            default -> handleOtherPlayerCommand(sender, args);
        }
        return true;
    }

    private void handleDebugCommand(CommandSender sender) {
        if (!sender.hasPermission("pt.reload")) {
            ChatUtil.errno(sender, ChatUtil.ChatTypes.NO_PERMISSION);
            return;
        }
        // wip
        sender.sendMessage("Server version: " + Bukkit.getServer().getVersion() +
                "\nPlayTimes version: " + JavaPlugin.getPlugin(PlayTimes.class).getDescription().getVersion() +
                "\nStatManager: " + StatManager.getInstance().name +
                "\nServerStatus: " + ServerManager.getInstance().isOnline());
    }

    private void handlePlayerCommand(CommandSender sender) {
        if (!(sender instanceof Player)) {
            StatManager stats = StatManager.getInstance();
            sender.sendMessage(ChatUtil.format("&cThe console has been up for " + stats.getUptime()));
            return;
        }
        OnlinePlayer player = new OnlinePlayer((Player) sender);
        player.sendMessageToTarget();
    }

    private void handleReloadCommand(CommandSender sender) {
        if (!sender.hasPermission("pt.reload")) {
            ChatUtil.errno(sender, ChatUtil.ChatTypes.NO_PERMISSION);
            return;
        }
        data.reloadAll();
        TimeConstants.reload();
        TimeManager.getInstance().registerTimings();
        sender.sendMessage(ChatUtil.format("&aPlayTimes Configurations Reloaded!"));
    }

    private void handleTopCommand(CommandSender sender) {
        Bukkit.dispatchCommand(sender, "toppt");
    }

    private void handleBlockCommand(CommandSender sender, String[] args, boolean block) {
        String blockAction = block ? "block" : "unblock";
        if (!sender.hasPermission("pt." + blockAction)) {
            ChatUtil.errno(sender, ChatUtil.ChatTypes.NO_PERMISSION);
            return;
        }
        if (args.length < 2) {
            sender.sendMessage(ChatUtil.formatWithPrefix("&c&lIncorrect Usage&c, try &7/pt " + blockAction + " <player>"));
            return;
        }
        String playerName = args[1];
        String blockedKey = "blocked." + playerName.toLowerCase();
        if (block == data.getData().contains(blockedKey)) {
            sender.sendMessage(ChatUtil.formatWithPrefix("&cUser is already " + blockAction + "ed!"));
            sender.sendMessage(ChatUtil.formatWithPrefix("&c&oMaybe try, &7/pt " + (block ? "unblock" : "block") + " <player>"));
            return;
        }
        UUID uuid;
        if (Bukkit.getPlayer(playerName) == null) {
            try {
                uuid = ServerManager.getInstance().getUUID(playerName.toLowerCase());
            } catch (NullPointerException e) {
                ChatUtil.errno(sender, ChatUtil.ChatTypes.PLAYER_NOT_FOUND);
                return;
            }
        } else {
            uuid = Objects.requireNonNull(Bukkit.getPlayer(playerName)).getUniqueId();
        }
        if (block) {
            data.getData().set(blockedKey, uuid.toString());
            if (data.getData().contains("leaderboard." + uuid)) {
                data.getData().set("leaderboard." + uuid, null);
            }
            sender.sendMessage(ChatUtil.formatWithPrefix("&aUser &c&lBLOCKED &afrom leaderboards!"));
        } else {
            data.getData().set(blockedKey, null);
            sender.sendMessage(ChatUtil.formatWithPrefix("&aUser &b&lUNBLOCKED &afrom leaderboards!"));
        }
        data.saveData();
    }

    private void handleOtherPlayerCommand(CommandSender sender, String[] args) {
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
                } else {
                    target = Objects.requireNonNull(Bukkit.getPlayer(args[0])).getUniqueId();
                }

                org.bukkit.OfflinePlayer offlinePlayer = Bukkit.getServer().getOfflinePlayer(target);
                OfflinePlayer player = new OfflinePlayer(sender, target, offlinePlayer.getName());
                player.sendMessageToTarget();
            }
        }.runTaskAsynchronously(JavaPlugin.getPlugin(PlayTimes.class));
    }
}
