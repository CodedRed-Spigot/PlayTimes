package me.codedred.playtimes.listeners;

import java.util.UUID;
import me.codedred.playtimes.PlayTimes;
import me.codedred.playtimes.data.DataManager;
import me.codedred.playtimes.data.database.manager.DatabaseManager;
import me.codedred.playtimes.statistics.StatManager;
import me.codedred.playtimes.statistics.StatisticType;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class Join implements Listener {

  @EventHandler
  public void onJoin(PlayerJoinEvent event) {
    UUID uuid = event.getPlayer().getUniqueId();
    DataManager data = DataManager.getInstance();

    // Database
    if (data.hasDatabase()) {
      DatabaseManager dbManager = DatabaseManager.getInstance();
      dbManager.retrievePlaytime(uuid);

      // hacky bug fix that will update data again if database query was too slow.
      // this bug only arises when switching servers super fast!
      Bukkit
        .getScheduler()
        .runTaskLaterAsynchronously(
          PlayTimes.getPlugin(PlayTimes.class),
          () -> {
            dbManager.retrievePlaytime(uuid);
          },
          20L * 30
        );
    }

    // Leaderboard
    if (
      data
        .getData()
        .contains("blocked." + event.getPlayer().getName().toLowerCase())
    ) {
      return;
    }

    ConfigurationSection leaderboardSection = data
      .getData()
      .getConfigurationSection("leaderboard");

    if (leaderboardSection == null) {
      leaderboardSection = data.getData().createSection("leaderboard");
    }

    if (leaderboardSection.contains(uuid.toString())) {
      return;
    }

    long time = StatManager
      .getInstance()
      .getPlayerStat(uuid, StatisticType.PLAYTIME);
    leaderboardSection.set(uuid.toString(), time);

    data.saveData();
  }
}
