package me.codedred.playtimes.utils;

import me.codedred.playtimes.data.DataManager;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

// wip
public class LastPlayedUtil {
    public static long getOfflineTime(UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);
        if (player != null && player.isOnline()) {
            // Player is currently online
            return -1;
        }

        DataManager data = DataManager.getInstance();
        ConfigurationSection lastPlayedSection = data.getData().getConfigurationSection("last_played");

        if (lastPlayedSection != null && lastPlayedSection.contains(uuid.toString())) {
            String lastPlayedString = lastPlayedSection.getString(uuid.toString());
            LocalDateTime lastPlayed = LocalDateTime.parse(lastPlayedString, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            LocalDateTime now = LocalDateTime.now();
            Duration duration = Duration.between(lastPlayed, now);
            return duration.toMillis();
        }

        // Return 0 if no "last_played" data is available
        return 0;
    }
}
