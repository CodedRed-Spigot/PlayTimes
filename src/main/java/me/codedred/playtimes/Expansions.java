package me.codedred.playtimes;

import java.util.*;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.codedred.playtimes.data.DataManager;
import me.codedred.playtimes.data.database.manager.DatabaseManager;
import me.codedred.playtimes.models.Leaderboard;
import me.codedred.playtimes.statistics.StatManager;
import me.codedred.playtimes.statistics.StatisticType;
import me.codedred.playtimes.time.TimeManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

public class Expansions extends PlaceholderExpansion {

  @Override
  public boolean canRegister() {
    return true;
  }

  @Override
  public @NotNull String getAuthor() {
    return "CodedRed";
  }

  @Override
  public @NotNull String getIdentifier() {
    return "PlayTimes";
  }

  @Override
  public @NotNull String getVersion() {
    return "1.5.4";
  }

  @Override
  public boolean persist() {
    return true;
  }

  @Override
  public String onRequest(OfflinePlayer player, @NotNull String identifier) {
    identifier = identifier.toLowerCase();
    switch (identifier) {
      case "playtime":
        return getPlayerPlayTime(player);
      case "uptime":
        return StatManager.getInstance().getUptime();
      case "joindate":
        return StatManager.getInstance().getJoinDate(player.getUniqueId());
      case "timesjoined":
        return Long.toString(
          StatManager
            .getInstance()
            .getPlayerStat(player.getUniqueId(), StatisticType.TIMES_JOINED)
        );
      // New cases for %PlayTimes_db_serverId% and %PlayTimes_total%
      case "total":
        return getTotalPlaytime(player);
      default:
        if (identifier.startsWith("db_")) {
          return getServerSpecificPlaytime(player, identifier.substring(3));
        }
        return handleLeaderboardIdentifier(identifier);
    }
  }

  private String getTotalPlaytime(OfflinePlayer player) {
    if (
      DataManager
        .getInstance()
        .getDBConfig()
        .getBoolean("database-settings.enabled")
    ) {
      TimeManager timeManager = TimeManager.getInstance();
      Long totalPlaytime = DatabaseManager
        .getInstance()
        .getRawTotalPlaytime(player.getUniqueId());
      return timeManager.buildFormat(totalPlaytime != null ? totalPlaytime : 0);
    }
    return "N/A";
  }

  private String getServerSpecificPlaytime(
    OfflinePlayer player,
    String serverId
  ) {
    if (
      DataManager
        .getInstance()
        .getDBConfig()
        .getBoolean("database-settings.enabled")
    ) {
      TimeManager timeManager = TimeManager.getInstance();
      Long playtime = DatabaseManager
        .getInstance()
        .getTimeForServer(player.getUniqueId(), serverId)
        .get("afktime");
      return timeManager.buildFormat(playtime != null ? playtime : 0);
    }
    return "N/A";
  }

  private String getPlayerPlayTime(OfflinePlayer player) {
    StatManager stats = StatManager.getInstance();
    TimeManager timeManager = TimeManager.getInstance();
    return timeManager.buildFormat(
      stats.getPlayerStat(player.getUniqueId(), StatisticType.PLAYTIME) / 20
    );
  }

  private String handleLeaderboardIdentifier(String identifier) {
    if (identifier.contains("topname")) {
      return getLeaderboardPlayerName(identifier);
    } else if (identifier.contains("toptime")) {
      return getLeaderboardPlayTime(identifier);
    }
    return null;
  }

  private String getLeaderboardPlayerName(String identifier) {
    Leaderboard board = new Leaderboard();
    int val = parseLeaderboardPosition(identifier);
    if (val == -1) {
      return ChatColor.RED + "Invalid leaderboard position!";
    }

    List<UUID> players = getTopPlayersUUID(board);
    if (players.size() < val + 1) return "N/A";

    OfflinePlayer targetPlayer = Bukkit.getOfflinePlayer(players.get(val));
    return targetPlayer.getName();
  }

  private String getLeaderboardPlayTime(String identifier) {
    Leaderboard board = new Leaderboard();
    int val = parseLeaderboardPosition(identifier);
    if (val == -1) {
      return ChatColor.RED + "Invalid leaderboard position!";
    }

    List<Integer> players = getTopPlayersPlayTime(board);
    if (players.size() < val + 1) return "N/A";

    return TimeManager.getInstance().buildFormat(players.get(val) / 20);
  }

  private List<UUID> getTopPlayersUUID(Leaderboard board) {
    List<UUID> players = new ArrayList<>();
    for (Map.Entry<String, Integer> entry : board
      .getTopTen()
      .entrySet()) players.add(UUID.fromString(entry.getKey()));
    return players;
  }

  private List<Integer> getTopPlayersPlayTime(Leaderboard board) {
    List<Integer> players = new ArrayList<>();
    for (Map.Entry<String, Integer> entry : board
      .getTopTen()
      .entrySet()) players.add(entry.getValue());
    return players;
  }

  private int parseLeaderboardPosition(String identifier) {
    String num = identifier.substring(7);
    if (isInteger(num)) {
      return (Integer.parseInt(num) < 1) ? 0 : Integer.parseInt(num) - 1;
    }
    return -1;
  }

  private boolean isInteger(String str) {
    try {
      Integer.parseInt(str);
      return true;
    } catch (Exception e) {
      return false;
    }
  }
}
