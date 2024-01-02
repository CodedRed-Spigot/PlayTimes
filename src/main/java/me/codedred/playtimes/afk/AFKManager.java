package me.codedred.playtimes.afk;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import me.codedred.playtimes.PlayTimes;
import me.codedred.playtimes.data.DataManager;
import me.codedred.playtimes.data.database.manager.DatabaseManager;
import me.codedred.playtimes.utils.ChatUtil;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class AFKManager {

  private static AFKManager instance = null;

  private final HashMap<UUID, Long> lastActive = new HashMap<>();
  private final HashMap<UUID, Long> afkTime = new HashMap<>();
  private final FileConfiguration config;
  private long afkThresholdMillis;

  private boolean notifyOnAfk;
  private boolean notifyOnBack;

  private String onAfkMessage;
  private String onBackMessage;

  private AFKManager() {
    config = DataManager.getInstance().getConfig();
    afkThresholdMillis = config.getLong("afk-settings.threshold") * 60L * 1000L;
    notifyOnAfk = config.getBoolean("afk-settings.notify-on-afk.onAFK");
    notifyOnBack =
      config.getBoolean("afk-settings.notify-on-afk.onBackFromAFK");
    onAfkMessage = config.getString("afk-settings.afk-message");
    onBackMessage = config.getString("afk-settings.back-from-afk-message");
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

    if (wasAFK && notifyOnBack) {
      player.sendMessage(ChatUtil.format(onBackMessage));
    }
  }

  public boolean isAFK(Player player) {
    return (
      lastActive.containsKey(player.getUniqueId()) &&
      System.currentTimeMillis() -
      lastActive.get(player.getUniqueId()) >
      afkThresholdMillis
    );
  }

  public void startAFKChecker() {
    Bukkit
      .getScheduler()
      .runTaskTimerAsynchronously(
        PlayTimes.getPlugin(PlayTimes.class),
        () -> {
          for (Player player : Bukkit.getOnlinePlayers()) {
            if (isAFK(player)) {
              if (!afkTime.containsKey(player.getUniqueId()) && notifyOnAfk) {
                player.sendMessage(ChatUtil.format(onAfkMessage));
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
          // Remove players who are no longer online
          Set<UUID> onlinePlayers = Bukkit
            .getOnlinePlayers()
            .stream()
            .map(Player::getUniqueId)
            .collect(Collectors.toSet());

          lastActive.keySet().retainAll(onlinePlayers);
          afkTime.keySet().retainAll(onlinePlayers);
        },
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

  public void removePlayer(UUID uuid) {
    lastActive.remove(uuid);
    afkTime.remove(uuid);
  }

  public long getAFKTime(UUID uuid) {
    return afkTime.getOrDefault(uuid, getDefaultAfkTime(uuid));
  }

  public void reload() {
    FileConfiguration config = DataManager.getInstance().getConfig();
    afkThresholdMillis = config.getLong("afk-settings.threshold") * 60L * 1000L;
    notifyOnAfk = config.getBoolean("afk-settings.notify-on-afk.onAFK");
    notifyOnBack =
      config.getBoolean("afk-settings.notify-on-afk.onBackFromAFK");
    onAfkMessage = config.getString("afk-settings.afk-message");
    onBackMessage = config.getString("afk-settings.back-from-afk-message");
  }
}
