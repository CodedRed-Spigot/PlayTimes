package me.codedred.playtimes.statistics;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import me.codedred.playtimes.data.DataManager;
import org.bukkit.Bukkit;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;
import java.util.UUID;

public class RisingStats implements Stats {

    private static final Map<StatisticType, String> PROPERTY_MAP = Map.of(
            StatisticType.PLAYTIME, "minecraft:play_one_minute",
            StatisticType.LEAVE, "minecraft:leave_game",
            StatisticType.REST, "minecraft:time_since_rest"
    );

    @Override
    public long getPlayerStatistic(UUID uuid, StatisticType type) {
        if (Bukkit.getPlayer(uuid) != null) {
            return getOnlineStatistic(Bukkit.getPlayer(uuid), type);
        }
        File playerStatistics = new File(worldFolder, uuid + ".json");

        if (playerStatistics.exists()) {
            try {
                JsonObject jsonObject = new Gson().fromJson(new FileReader(playerStatistics), JsonObject.class);

                JsonObject stats = jsonObject.getAsJsonObject("stats").getAsJsonObject("minecraft:custom");

                String propertyName = PROPERTY_MAP.get(type);
                if (propertyName != null) {
                    return stats.get(propertyName).getAsLong();
                }

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
            case REST -> player.getStatistic(Statistic.TIME_SINCE_REST);
            case LEAVE -> player.getStatistic(Statistic.LEAVE_GAME) + 1;
        };
    }

    @Override
    public boolean hasJoinedBefore(UUID uuid) {
        File playerStatistics = new File(worldFolder, uuid + ".json");
        return playerStatistics.exists();
    }

    private static final SimpleDateFormat DATE_FORMATTER =
            new SimpleDateFormat(DataManager.getInstance().getConfig().getString("date-format"));

    @Override
    public String getJoinDate(UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);
        Calendar calendar = Calendar.getInstance();
        if (player == null) {
            calendar.setTimeInMillis(Bukkit.getOfflinePlayer(uuid).getFirstPlayed());
            return DATE_FORMATTER.format(calendar.getTime());
        } else if (player.hasPlayedBefore()) {
            calendar.setTimeInMillis(player.getFirstPlayed());
            return DATE_FORMATTER.format(calendar.getTime());
        }
        return "Never Joined";
    }

}
