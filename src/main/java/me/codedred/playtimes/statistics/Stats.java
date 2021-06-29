package me.codedred.playtimes.statistics;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.UUID;

public interface Stats {

    long getPlayerStatistic(UUID uuid, StatisticType type);
    boolean hasJoinedBefore(UUID uuid);
    String getJoinDate(UUID uuid);
    long getOnlineStatistic(Player player, StatisticType type);

    File worldFolder = new File(Bukkit.getServer().getWorlds().get(0).getWorldFolder(), "stats");
}
