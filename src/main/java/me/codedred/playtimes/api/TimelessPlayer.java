package me.codedred.playtimes.api;

import java.util.UUID;
import me.codedred.playtimes.statistics.StatManager;
import me.codedred.playtimes.statistics.StatisticType;
import me.codedred.playtimes.time.TimeManager;
import org.bukkit.entity.Player;

public class TimelessPlayer {

  private final UUID uuid;
  private final StatManager stats;
  private final TimeManager timings;

  public TimelessPlayer(final Player player) {
    this.uuid = player.getUniqueId();
    this.stats = StatManager.getInstance();
    this.timings = TimeManager.getInstance();
  }

  public TimelessPlayer(final UUID player) {
    this.uuid = player;
    this.stats = StatManager.getInstance();
    this.timings = TimeManager.getInstance();
  }

  public String getPlayTime() {
    return this.timings.buildFormat(
        this.stats.getPlayerStat(this.uuid, StatisticType.PLAYTIME) / 20L
      );
  }

  public long getRawPlayTimeInSeconds() {
    return this.stats.getPlayerStat(this.uuid, StatisticType.PLAYTIME) / 20L;
  }
}
