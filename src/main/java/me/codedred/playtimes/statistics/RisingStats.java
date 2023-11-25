package me.codedred.playtimes.statistics;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.File;
import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.UUID;
import me.codedred.playtimes.data.DataManager;
import org.bukkit.Bukkit;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;

public class RisingStats implements Stats {

  @Override
  public long getPlayerStatistic(UUID uuid, StatisticType type) {
    if (Bukkit.getPlayer(uuid) != null) return getOnlineStatistic(
      Bukkit.getPlayer(uuid),
      type
    );
    File playerStatistics = new File(worldFolder, uuid + ".json");

    if (playerStatistics.exists()) {
      try {
        JsonObject jsonObject = new Gson()
          .fromJson(new FileReader(playerStatistics), JsonObject.class);

        JsonObject pilot = (JsonObject) jsonObject.get("stats");
        JsonObject passenger = (JsonObject) pilot.get("minecraft:custom");

        return switch (type) {
          case PLAYTIME -> passenger
            .get("minecraft:play_one_minute")
            .getAsLong();
          case TIMES_JOINED -> passenger
            .get("minecraft:leave_game")
            .getAsLong();
        };
      } catch (Exception e) {
        //e.printStackTrace();
      }
    }
    return 0;
  }

  @Override
  public long getOnlineStatistic(Player player, StatisticType type) {
    return switch (type) {
      case PLAYTIME -> player.getStatistic(Statistic.PLAY_ONE_MINUTE);
      case TIMES_JOINED -> player.getStatistic(Statistic.LEAVE_GAME) + 1;
    };
  }

  @Override
  public boolean hasJoinedBefore(UUID uuid) {
    File playerStatistics = new File(worldFolder, uuid + ".json");
    return playerStatistics.exists();
  }

  @Override
  public String getJoinDate(UUID uuid) {
    Player player = Bukkit.getPlayer(uuid);
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
      DataManager.getInstance().getConfig().getString("date-format")
    );
    Calendar calendar = Calendar.getInstance();
    if (player == null) {
      calendar.setTimeInMillis(Bukkit.getOfflinePlayer(uuid).getFirstPlayed());
      return simpleDateFormat.format(calendar.getTime());
    } else if (player.hasPlayedBefore()) {
      calendar.setTimeInMillis(player.getFirstPlayed());
      return simpleDateFormat.format(calendar.getTime());
    }
    return "Never Joined";
  }
}
