package me.codedred.playtimes.statistics;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import me.codedred.playtimes.data.DataManager;
import org.bukkit.Bukkit;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.FileReader;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Map;
import java.util.UUID;

public class RisenStats implements Stats {

    private static final Map<StatisticType, String> PROPERTY_MAP = Map.of(
            StatisticType.PLAYTIME, "minecraft:custom.minecraft:play_time",
            StatisticType.LEAVE, "minecraft:custom.minecraft:leave_game",
            StatisticType.REST, "minecraft:custom.minecraft:time_since_rest"
    );

    @Override
    public long getPlayerStatistic(UUID uuid, StatisticType type) {
        if (Bukkit.getPlayer(uuid) != null) {
            return getOnlineStatistic(Bukkit.getPlayer(uuid), type);
        }

        File playerStatistics = new File(worldFolder, uuid + ".json");

        if (playerStatistics.exists()) {
            try (FileReader reader = new FileReader(playerStatistics)) {
                JsonObject jsonObject = new Gson().fromJson(reader, JsonObject.class);
                JsonObject stats = jsonObject.getAsJsonObject("stats");

                String propertyName = PROPERTY_MAP.get(type);
                if (propertyName != null) {
                    return stats.getAsJsonObject(propertyName).get("value").getAsLong();
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

    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern(DataManager.getInstance().getConfig().getString("date-format"));

    @Override
    public String getJoinDate(UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);
        Calendar calendar = Calendar.getInstance();
        if (player == null) {
            calendar.setTimeInMillis(Bukkit.getOfflinePlayer(uuid).getFirstPlayed());
            return DATE_FORMATTER.format(calendar.toInstant());
        } else if (player.hasPlayedBefore()) {
            calendar.setTimeInMillis(player.getFirstPlayed());
            return DATE_FORMATTER.format(calendar.toInstant());
        }
        return "Never Joined";
    }
}
