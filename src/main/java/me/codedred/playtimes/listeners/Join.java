package me.codedred.playtimes.listeners;

import java.util.UUID;
import me.codedred.playtimes.data.DataManager;
import me.codedred.playtimes.statistics.StatManager;
import me.codedred.playtimes.statistics.StatisticType;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class Join implements Listener {

  @EventHandler
  public void onJoin(PlayerJoinEvent event) {
    DataManager data = DataManager.getInstance();
    if (
      data
        .getData()
        .contains("blocked." + event.getPlayer().getName().toLowerCase())
    ) {
      return;
    }

    UUID uuid = event.getPlayer().getUniqueId();
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
