package me.codedred.playtimes.listeners;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
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
      CompletableFuture.runAsync(() -> {
        DatabaseManager dbManager = DatabaseManager.getInstance();
        dbManager.updatePlaytime(
          uuid,
          StatManager
            .getInstance()
            .getPlayerStat(uuid, StatisticType.PLAYTIME) /
          20,
          AFKManager.getInstance().getAFKTime(uuid)
        );
      });
    } else {
      // Save AFK time in data.yml
      CompletableFuture.runAsync(() -> {
        data
          .getData()
          .set("afktime." + uuid, AFKManager.getInstance().getAFKTime(uuid));
        data.saveData();
      });
    }

    // Leaderboard
    String playerName = event.getPlayer().getName().toLowerCase();
    if (data.getData().contains("blocked." + playerName)) {
      return;
    }

    // Run leaderboard update
    CompletableFuture.runAsync(() -> {
      StatManager statManager = StatManager.getInstance();
      long playtime = statManager.getPlayerStat(uuid, StatisticType.PLAYTIME);
      if (
        !data.getConfig().getBoolean("top-playtime.track-rawtime", false) &&
        data.hasAfkEnabled()
      ) {
        playtime -= AFKManager.getInstance().getAFKTime(uuid) * 20;
      }

      data.getData().set("leaderboard." + uuid, playtime);
      data.saveData();
    });
  }
}
