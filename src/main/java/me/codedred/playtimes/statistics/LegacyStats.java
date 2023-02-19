package me.codedred.playtimes.statistics;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import me.codedred.playtimes.data.DataManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import java.io.File;
import java.io.FileReader;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Map;
import java.util.UUID;

public class LegacyStats implements Stats {

    private static final Map<StatisticType, String> PROPERTY_MAP = Map.of(
            StatisticType.PLAYTIME, "stat.playOneMinute",
            StatisticType.LEAVE, "stat.leaveGame",
            StatisticType.REST, "stat.timeSinceDeath"
    );

    @Override
    public long getPlayerStatistic(UUID uuid, StatisticType type) {
        File playerStatistics = new File(worldFolder, uuid + ".json");

        if (playerStatistics.exists()) {
            try {
                JsonObject jsonObject = new Gson().fromJson(new FileReader(playerStatistics), JsonObject.class);

                String propertyName = PROPERTY_MAP.get(type);
                if (propertyName != null) {
                    return jsonObject.get(propertyName).getAsLong();
                }

            } catch (Exception e) {
                //e.printStackTrace();
            }
        }
        return 0;
    }

    @Override
    public long getOnlineStatistic(Player player, StatisticType type) {
        return getPlayerStatistic(player.getUniqueId(), type);
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
