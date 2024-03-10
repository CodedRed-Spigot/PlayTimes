package me.codedred.playtimes.commands;

import java.util.Objects;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import me.codedred.playtimes.PlayTimes;
import me.codedred.playtimes.data.DataManager;
import me.codedred.playtimes.models.Message;
import me.codedred.playtimes.server.ServerManager;
import me.codedred.playtimes.statistics.StatManager;
import me.codedred.playtimes.time.TimeConstants;
import me.codedred.playtimes.time.TimeManager;
import me.codedred.playtimes.utils.ChatUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

public class Time implements CommandExecutor {

  private final DataManager data = DataManager.getInstance();

  public boolean onCommand(
    CommandSender sender,
    @NotNull Command cmd,
    @NotNull String label,
    String[] args
  ) {
    if (!sender.hasPermission("pt.use")) {
      ChatUtil.errno(sender, ChatUtil.ChatTypes.NO_PERMISSION);
      return true;
    }

    String arg = args.length > 0 ? args[0].toLowerCase() : "";

    switch (arg) {
      case "":
        handlePlayerCommand(sender);
        break;
      case "reload":
        handleReloadCommand(sender);
        break;
      case "reloaddatabase":
        handleReloadDatabaseCommand(sender);
        break;
      case "top":
        handleTopCommand(sender);
        break;
      case "block":
        handleBlockCommand(sender, args, true);
        break;
      case "unblock":
        handleBlockCommand(sender, args, false);
        break;
      case "version":
        handleVersionCommand(sender);
        break;
      case "help":
        handleHelpCommand(sender);
        break;
      default:
        handleOtherPlayerCommand(sender, args);
        break;
    }

    return true;
  }

  private void handleHelpCommand(CommandSender sender) {
    if (!sender.hasPermission("pt.reload")) {
      ChatUtil.errno(sender, ChatUtil.ChatTypes.NO_PERMISSION);
      return;
    }
    sender.sendMessage(ChatUtil.format("&6&l*** PlayTimes Help ***"));
    sender.sendMessage(ChatUtil.format("&e/pt: &7Displays your play time"));
    sender.sendMessage(
      ChatUtil.format("&e/pt <player>: &7Displays the play time of <player>")
    );
    sender.sendMessage(
      ChatUtil.format(
        "&e/pt reload: &7Reloads the PlayTimes plugin configurations"
      )
    );
    sender.sendMessage(
      ChatUtil.format("&e/pt top: &7Shows the top players by play time")
    );
    sender.sendMessage(
      ChatUtil.format(
        "&e/pt block <player>: &7Blocks a player from leaderboards"
      )
    );
    sender.sendMessage(
      ChatUtil.format(
        "&e/pt unblock <player>: &7Unblocks a player from leaderboards"
      )
    );
    sender.sendMessage(
      ChatUtil.format("&e/pt version: &7Displays the version info")
    );
  }

  private void handleVersionCommand(CommandSender sender) {
    if (!sender.hasPermission("pt.reload")) {
      ChatUtil.errno(sender, ChatUtil.ChatTypes.NO_PERMISSION);
      return;
    }
    boolean isOnline = ServerManager.getInstance().isOnline();
    String serverStatus = isOnline ? "Online" : "Offline";

    sender.sendMessage(ChatUtil.format("&6&l*** PlayTimes Version Info ***"));
    sender.sendMessage(
      ChatColor.GOLD +
      "Server version: " +
      ChatColor.WHITE +
      Bukkit.getServer().getVersion() +
      ChatColor.GOLD +
      "\nPlayTimes version: " +
      ChatColor.WHITE +
      JavaPlugin.getPlugin(PlayTimes.class).getDescription().getVersion() +
      ChatColor.GOLD +
      "\nStat Manager: " +
      ChatColor.WHITE +
      StatManager.getInstance().name +
      ChatColor.GOLD +
      "\nServer Status: " +
      ChatColor.WHITE +
      serverStatus +
      ChatColor.GOLD +
      "\nDatabase connected: " +
      ChatColor.WHITE +
      DataManager.getInstance().hasDatabase() +
      ChatColor.GOLD +
      "\nAFK Status: " +
      ChatColor.WHITE +
      DataManager.getInstance().hasAfkEnabled() +
      ChatColor.GOLD +
      "\nUUID Lookup Type: " +
      ChatColor.WHITE +
      ServerManager.getInstance().isOfflineLookup()
    );
  }

  private void handlePlayerCommand(CommandSender sender) {
    if (!(sender instanceof Player)) {
      StatManager stats = StatManager.getInstance();
      sender.sendMessage(
        ChatUtil.format("&cThe console has been up for " + stats.getUptime())
      );
      return;
    }

    new BukkitRunnable() {
      @Override
      public void run() {
        Player player = (Player) sender;
        Message message = new Message(
          sender,
          player.getUniqueId(),
          player.getName()
        );
        message.sendMessageToTarget();
      }
    }
      .runTaskAsynchronously(JavaPlugin.getPlugin(PlayTimes.class));
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
    if (DataManager.getInstance().hasDatabase()) {
      sender.sendMessage(
        ChatUtil.format("&aTrying to reload the Database settings?")
      );
      sender.sendMessage(ChatUtil.format(" &e&oTry, /pt reloadDatabase"));
    }
  }

  private void handleReloadDatabaseCommand(CommandSender sender) {
    if (!sender.hasPermission("pt.reload")) {
      ChatUtil.errno(sender, ChatUtil.ChatTypes.NO_PERMISSION);
      return;
    }
    data.reloadDatabase();
    PlayTimes.getPlugin(PlayTimes.class).loadDatabase();
    sender.sendMessage(ChatUtil.format("&aDatabase Reloaded."));
    sender.sendMessage(
      ChatUtil.format(
        "&oIf you are still encountering issues try restarting your server."
      )
    );
  }

  private void handleTopCommand(CommandSender sender) {
    Bukkit.dispatchCommand(sender, "toppt");
  }

  private void handleBlockCommand(
    CommandSender sender,
    String[] args,
    boolean block
  ) {
    String blockAction = block ? "block" : "unblock";
    if (!sender.hasPermission("pt.block")) {
      ChatUtil.errno(sender, ChatUtil.ChatTypes.NO_PERMISSION);
      return;
    }
    if (args.length < 2) {
      sender.sendMessage(
        ChatUtil.formatWithPrefix(
          "&c&lIncorrect Usage&c, try &7/pt " + blockAction + " <player>"
        )
      );
      return;
    }
    String playerName = args[1];
    String blockedKey = "blocked." + playerName.toLowerCase();
    if (block == data.getData().contains(blockedKey)) {
      sender.sendMessage(
        ChatUtil.formatWithPrefix("&cUser is already " + blockAction + "ed!")
      );
      sender.sendMessage(
        ChatUtil.formatWithPrefix(
          "&c&oMaybe try, &7/pt " + (block ? "unblock" : "block") + " <player>"
        )
      );
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
    if (uuid == null) {
      sender.sendMessage(ChatUtil.formatWithPrefix("&c&lUser not found."));
      return;
    }
    if (block) {
      data.getData().set(blockedKey, uuid.toString());
      if (data.getData().contains("leaderboard." + uuid)) {
        data.getData().set("leaderboard." + uuid, null);
      }
      sender.sendMessage(
        ChatUtil.formatWithPrefix("&aUser &c&lBLOCKED &afrom leaderboards!")
      );
    } else {
      data.getData().set(blockedKey, null);
      sender.sendMessage(
        ChatUtil.formatWithPrefix("&aUser &b&lUNBLOCKED &afrom leaderboards!")
      );
    }
    data.saveData();
  }

  private void handleOtherPlayerCommand(CommandSender sender, String[] args) {
    if (!sender.hasPermission("pt.others")) {
      ChatUtil.errno(sender, ChatUtil.ChatTypes.NO_PERMISSION);
      return;
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
        } else {
          target =
            Objects.requireNonNull(Bukkit.getPlayer(args[0])).getUniqueId();
        }

        OfflinePlayer offlinePlayer = Bukkit
          .getServer()
          .getOfflinePlayer(target);
        Message message = new Message(sender, target, offlinePlayer.getName());
        message.sendMessageToTarget();
      }
    }
      .runTaskAsynchronously(JavaPlugin.getPlugin(PlayTimes.class));
  }
}
