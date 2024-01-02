package me.codedred.playtimes.afk;

import java.util.HashMap;
import java.util.UUID;
import me.codedred.playtimes.PlayTimes;
import me.codedred.playtimes.data.DataManager;
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
      config.getLong("afk-settings.afk-threshold") * 60L * 1000L;
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
              afkTime.getOrDefault(player.getUniqueId(), 0L) + 1
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

  public void removePlayer(Player player) {
    lastActive.remove(player.getUniqueId());
    afkTime.remove(player.getUniqueId());
  }

  public long getAFKTime(Player player) {
    return afkTime.getOrDefault(player.getUniqueId(), 0L);
  }
}
