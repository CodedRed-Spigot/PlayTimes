package me.codedred.playtimes.models;

import java.util.*;
import me.codedred.playtimes.afk.AFKManager;
import me.codedred.playtimes.data.DataManager;
import me.codedred.playtimes.statistics.StatManager;
import me.codedred.playtimes.statistics.StatisticType;

public class Leaderboard {

  final String LEADERBOARD = "leaderboard";

  /**
   * Grabs the uuids from the data file, sorts them, then removes redundant uuids and updates the top 10
   *
   * @return listing of top 10 players
   */
  public Map<String, Integer> getTopTen() {
    Map<String, Integer> topTen = new LinkedHashMap<>();
    DataManager data = DataManager.getInstance();
    if (!data.getData().contains("leaderboard")) {
      return topTen;
    }
    for (String key : data
      .getData()
      .getConfigurationSection(LEADERBOARD)
      .getKeys(false)) {
      topTen.put(
        key,
        Integer.valueOf(data.getData().getString(LEADERBOARD + "." + key))
      );
    }

    List<Map.Entry<String, Integer>> list = new LinkedList<>(topTen.entrySet());
    list.sort((o1, o2) -> o2.getValue() - o1.getValue());

    if (list.size() > 20) {
      List<Map.Entry<String, Integer>> delList = list.subList(20, list.size());

      for (Map.Entry<String, Integer> key : delList) {
        data.getData().set(LEADERBOARD + "." + key.getKey(), null);
      }
      data.saveData();
    }

    if (list.size() > 9) {
      list = list.subList(0, 10);
    }

    topTen.clear();

    for (Map.Entry<String, Integer> entry : list) {
      topTen.put(entry.getKey(), entry.getValue());
    }

    updateTimes(topTen);
    return topTen;
  }

  /**
   * Updates the top ten times by querying the latest times from the StatManager and sorting the list.
   *
   * @param topTen the current leaderboard, with UUIDs and their corresponding time
   */
  private void updateTimes(Map<String, Integer> topTen) {
    StatManager statManager = StatManager.getInstance();
    DataManager dataManager = DataManager.getInstance();
    List<Map.Entry<String, Integer>> list = new ArrayList<>(topTen.entrySet());

    for (Map.Entry<String, Integer> entry : list) {
      String uuid = entry.getKey();
      int latestTime = (int) statManager.getPlayerStat(
        UUID.fromString(uuid),
        StatisticType.PLAYTIME
      );

      if (
        !dataManager
          .getConfig()
          .getBoolean("top-playtime.track-rawtime", false) &&
        dataManager.hasAfkEnabled()
      ) {
        latestTime -=
          AFKManager.getInstance().getAFKTime(UUID.fromString(uuid)) * 20;
      }

      topTen.put(uuid, latestTime);
    }

    list = new ArrayList<>(topTen.entrySet());
    list.sort(Map.Entry.<String, Integer>comparingByValue().reversed());

    topTen.clear();

    for (int i = 0; i < Math.min(list.size(), 10); i++) {
      Map.Entry<String, Integer> entry = list.get(i);
      topTen.put(entry.getKey(), entry.getValue());
    }
  }
}
