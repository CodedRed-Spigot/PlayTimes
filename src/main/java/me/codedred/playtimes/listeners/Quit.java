package me.codedred.playtimes.listeners;

import java.util.UUID;
import me.codedred.playtimes.afk.AFKManager;
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
    if (data.hasDatabase()) {
      DatabaseManager dbManager = DatabaseManager.getInstance();
      dbManager.updatePlaytime(
        uuid,
        StatManager
          .getInstance()
          .getPlayerStat(
            event.getPlayer().getUniqueId(),
            StatisticType.PLAYTIME
          ) /
        20,
        AFKManager.getInstance().getAFKTime(event.getPlayer())
      );
    } else {
      // save afktime in data.yml
      data
        .getData()
        .set(
          "afktime." + uuid,
          AFKManager.getInstance().getAFKTime(event.getPlayer())
        );
      data.saveData();
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
