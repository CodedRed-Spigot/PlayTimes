package me.codedred.playtimes.statistics;

import me.codedred.playtimes.utils.ServerUtils;
import me.codedred.playtimes.utils.TimeFormatterUtil;

import java.lang.management.ManagementFactory;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class StatManager {

    private static final StatManager instance = new StatManager();

    public static StatManager getInstance() {
        return instance;
    }

    private Stats stats;
    private boolean legacy = false;

    public void registerStatistics() {
        if (ServerUtils.isRisenVersion())
            stats = new RisenStats();
        else if (ServerUtils.isNewerVersion())
            stats = new RisingStats();
        else {
            stats = new LegacyStats();
            setLegacy();
        }
    }

    public Stats getStats() {
        return stats;
    }

    public long getPlayerStat(UUID uuid, StatisticType type) {
        return getStats().getPlayerStatistic(uuid, type);
    }

    public boolean hasJoinedBefore(UUID uuid) {
        return !getStats().hasJoinedBefore(uuid);
    }

    public String getJoinDate(UUID uuid) {
        return getStats().getJoinDate(uuid);
    }

    private void setLegacy() {
        legacy = true;
    }

    public boolean isLegacy() {
        return legacy;
    }

    public String getUptime() {
        return TimeFormatterUtil.secondsToFormattedTime((int) TimeUnit.MILLISECONDS.toSeconds(ManagementFactory.getRuntimeMXBean().getUptime()), "HH:mm:ss", Locale.US, "CDT");
    }

}
