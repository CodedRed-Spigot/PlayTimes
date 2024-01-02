package me.codedred.playtimes.afk;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import me.codedred.playtimes.PlayTimes;
import me.codedred.playtimes.data.DataManager;
import me.codedred.playtimes.data.database.manager.DatabaseManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class AFKManager {

  private static AFKManager instance = null;

  private final HashMap<UUID, Long> lastActive = new HashMap<>();
  private final HashMap<UUID, Long> afkTime = new HashMap<>();
  private final FileConfiguration config;

  private AFKManager() {
    config = DataManager.getInstance().getConfig();
  }

  public static AFKManager getInstance() {
    if (instance == null) {
      instance = new AFKManager();
    }
    return instance;
  }

  public void updateActivity(Player player) {
    boolean wasAFK = isAFK(player);
    lastActive.put(player.getUniqueId(), System.currentTimeMillis());

    if (
      wasAFK && config.getBoolean("afk-settings.notify-on-afk.onBackFromAFK")
    ) {
      player.sendMessage(
        ChatColor.translateAlternateColorCodes(
          '&',
          config.getString("afk-settings.back-from-afk-message")
        )
      );
    }
  }

  public boolean isAFK(Player player) {
    long afkThresholdMillis =
      config.getLong("afk-settings.threshold") * 60L * 1000L;
    return (
      lastActive.containsKey(player.getUniqueId()) &&
      System.currentTimeMillis() -
      lastActive.get(player.getUniqueId()) >
      afkThresholdMillis
    );
  }

  public void startAFKChecker() {
    new BukkitRunnable() {
      @Override
      public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
          if (isAFK(player)) {
            if (
              !afkTime.containsKey(player.getUniqueId()) &&
              config.getBoolean("afk-settings.notify-on-afk.onAFK")
            ) {
              player.sendMessage(
                ChatColor.translateAlternateColorCodes(
                  '&',
                  config.getString("afk-settings.afk-message")
                )
              );
            }
            afkTime.put(
              player.getUniqueId(),
              afkTime.getOrDefault(
                player.getUniqueId(),
                getDefaultAfkTime(player.getUniqueId())
              ) +
              1
            );
          }
        }
      }
    }
      .runTaskTimerAsynchronously(
        PlayTimes.getPlugin(PlayTimes.class),
        20L,
        20L
      );
  }

  private Long getDefaultAfkTime(UUID uuid) {
    DataManager dataManager = DataManager.getInstance();

    if (dataManager.hasDatabase()) {
      Map<String, Long> timeMap = DatabaseManager
        .getInstance()
        .getTimeForServer(uuid);
      return timeMap != null ? timeMap.getOrDefault("afktime", 0L) : 0L;
    } else {
      return dataManager.getData().getLong("afktime." + uuid, 0L);
    }
  }

  public void removePlayer(Player player) {
    lastActive.remove(player.getUniqueId());
    afkTime.remove(player.getUniqueId());
  }

  public long getAFKTime(Player player) {
    return afkTime.getOrDefault(
      player.getUniqueId(),
      getDefaultAfkTime(player.getUniqueId())
    );
  }

  public long getOfflineAFKTime(UUID uuid) {
    if (DataManager.getInstance().hasDatabase()) {
      return DatabaseManager
        .getInstance()
        .getTimeForServer(uuid)
        .get("afktime");
    }
    return DataManager.getInstance().getData().getLong("afktime." + uuid);
  }
}
