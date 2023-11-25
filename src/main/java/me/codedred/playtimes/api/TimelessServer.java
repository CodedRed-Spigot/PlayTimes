package me.codedred.playtimes.api;

import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import me.codedred.playtimes.models.Leaderboard;
import me.codedred.playtimes.statistics.StatManager;

public class TimelessServer {

  public List<UUID> getTop10Players() {
    final List<UUID> players = new ArrayList<>();
    final Leaderboard board = new Leaderboard();
    final Map<String, Integer> map = board.getTopTen();
    final Object[] array = map.keySet().toArray();
    for (int i = 0; i < map.size(); ++i) {
      players.add(UUID.fromString(array[i].toString()));
    }
    return players;
  }

  public List<Integer> getTop10Times() {
    final List<Integer> players = new ArrayList<>();
    final Leaderboard board = new Leaderboard();
    final Map<String, Integer> map = board.getTopTen();
    final Object[] array = map.values().toArray();
    for (int i = 0; i < map.size(); ++i) {
      players.add(Integer.parseInt(array[i].toString()) / 20);
    }
    return players;
  }

  public Map<String, Integer> getTopMap() {
    final Leaderboard board = new Leaderboard();
    return board.getTopTen();
  }

  public UUID getNumberOne() {
    return this.getTop10Players().get(0);
  }

  public UUID getNumberTwo() {
    return this.getTop10Players().get(1);
  }

  public UUID getNumberThree() {
    return this.getTop10Players().get(2);
  }

  public UUID getNumberFour() {
    return this.getTop10Players().get(3);
  }

  public UUID getNumberFive() {
    return this.getTop10Players().get(4);
  }

  public UUID getNumberSix() {
    return this.getTop10Players().get(5);
  }

  public UUID getNumberSeven() {
    return this.getTop10Players().get(6);
  }

  public UUID getNumberEight() {
    return this.getTop10Players().get(7);
  }

  public UUID getNumberNine() {
    return this.getTop10Players().get(8);
  }

  public UUID getNumberTen() {
    return this.getTop10Players().get(9);
  }

  public String getUptime() {
    return StatManager.getInstance().getUptime();
  }

  public long getUptimeInSeconds() {
    return TimeUnit.MILLISECONDS.toSeconds(
      ManagementFactory.getRuntimeMXBean().getUptime()
    );
  }
}
