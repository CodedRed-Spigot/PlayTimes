package me.codedred.playtimes.statistics;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.UUID;

public interface Stats {

    public long getPlayerStatistic(UUID uuid, StatisticType type);
    public boolean hasJoinedBefore(UUID uuid);
    public String getJoinDate(UUID uuid);
    public long getOnlineStatistic(Player player, StatisticType type);

    static File worldFolder = new File(Bukkit.getServer().getWorlds().get(0).getWorldFolder(), "stats");
}
