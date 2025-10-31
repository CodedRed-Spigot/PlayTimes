package me.codedred.playtimes.models;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import me.codedred.playtimes.afk.AFKManager;
import me.codedred.playtimes.data.DataManager;
import me.codedred.playtimes.statistics.StatManager;
import me.codedred.playtimes.statistics.StatisticType;

public class Leaderboard {

  final String LEADERBOARD = "leaderboard";
  
  private static final Map<String, Integer> cachedLeaderboard = java.util.Collections.synchronizedMap(new LinkedHashMap<>());
  private static long lastCacheUpdate = 0;
  private static final long CACHE_DURATION = 30000; // 30 seconds

  /**
   * Grabs the uuids from the data file, sorts them, then removes redundant uuids and updates the top 10
   *
   * @return listing of top 10 players
   */
  public Map<String, Integer> getTopTen() {
    long currentTime = System.currentTimeMillis();
    
    if (currentTime - lastCacheUpdate < CACHE_DURATION && !cachedLeaderboard.isEmpty()) {
      return new LinkedHashMap<>(cachedLeaderboard);
    }
    
    Map<String, Integer> topTen = generateTopTen();
    
    cachedLeaderboard.clear();
    cachedLeaderboard.putAll(topTen);
    lastCacheUpdate = currentTime;
    
    return topTen;
  }
  
  /**
   * Forces cache invalidation and regenerates the leaderboard
   */
  public static void invalidateCache() {
    lastCacheUpdate = 0;
    cachedLeaderboard.clear();
  }
  
  private Map<String, Integer> generateTopTen() {
    Map<String, Integer> allUsers = new LinkedHashMap<>();
    DataManager data = DataManager.getInstance();
    if (!data.getData().contains("leaderboard")) {
      return allUsers;
    }
    
    for (String key : data
      .getData()
      .getConfigurationSection(LEADERBOARD)
      .getKeys(false)) {
      allUsers.put(
        key,
        Integer.valueOf(data.getData().getString(LEADERBOARD + "." + key))
      );
    }

    Map<String, Integer> updatedUsers = updateAllUserTimes(allUsers);
    
    List<Map.Entry<String, Integer>> list = new ArrayList<>(updatedUsers.entrySet());
    list.sort(Map.Entry.<String, Integer>comparingByValue().reversed());

    if (list.size() > 20) {
      List<Map.Entry<String, Integer>> delList = list.subList(20, list.size());

      for (Map.Entry<String, Integer> key : delList) {
        data.getData().set(LEADERBOARD + "." + key.getKey(), null);
      }
      // this is fine; happens inside the async /toppt functionality i think - DerexXD
      data.saveData();
      
      list = list.subList(0, 20);
    }

    Map<String, Integer> topTen = new LinkedHashMap<>();
    int count = Math.min(list.size(), 10);
    for (int i = 0; i < count; i++) {
      Map.Entry<String, Integer> entry = list.get(i);
      topTen.put(entry.getKey(), entry.getValue());
    }

    return topTen;
  }

  /**
   * Updates all users' times by querying the latest times from the StatManager.
   *
   * @param allUsers all users with their cached playtime data
   * @return updated map with current playtime data
   */
  private Map<String, Integer> updateAllUserTimes(Map<String, Integer> allUsers) {
    StatManager statManager = StatManager.getInstance();
    DataManager dataManager = DataManager.getInstance();
    Map<String, Integer> updatedUsers = new LinkedHashMap<>();

    for (Map.Entry<String, Integer> entry : allUsers.entrySet()) {
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

      updatedUsers.put(uuid, latestTime);
    }

    return updatedUsers;
  }
}
