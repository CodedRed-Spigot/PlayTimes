package me.codedred.playtimes.listeners;

import java.util.UUID;
import me.codedred.playtimes.data.DataManager;
import me.codedred.playtimes.data.database.manager.DatabaseManager;
import me.codedred.playtimes.statistics.StatManager;
import me.codedred.playtimes.statistics.StatisticType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class Quit implements Listener {

  @EventHandler
  public void onQuit(PlayerQuitEvent event) {
    UUID uuid = event.getPlayer().getUniqueId();
    DataManager data = DataManager.getInstance();

    // Database
    if (data.getDBConfig().getBoolean("database-settings.enabled")) {
      DatabaseManager dbManager = DatabaseManager.getInstance();
      dbManager.updatePlaytime(
        uuid,
        StatManager
          .getInstance()
          .getStats()
          .getOnlineStatistic(event.getPlayer(), StatisticType.PLAYTIME) /
        20
      );
    }

    // Leaderboard
    String playerName = event.getPlayer().getName().toLowerCase();
    if (data.getData().contains("blocked." + playerName)) {
      return;
    }

    StatManager statManager = StatManager.getInstance();
    long playtime = statManager.getPlayerStat(uuid, StatisticType.PLAYTIME);
    data.getData().set("leaderboard." + uuid, playtime);

    data.saveData();
  }
}
