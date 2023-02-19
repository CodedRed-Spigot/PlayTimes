package me.codedred.playtimes.server;

import me.codedred.playtimes.data.DataManager;
import org.bukkit.Bukkit;
import java.io.IOException;
import java.util.UUID;

public class ServerManager {
    private static final ServerManager instance = new ServerManager();

    private final ServerStatus status;

    private ServerManager() {
        status = Bukkit.getOnlineMode() ? new ServerOnline() : new ServerOffline();
        cleanLeaderboard();
    }

    public static ServerManager getInstance() {
        return instance;
    }

    public UUID getUUID(String name) {
        return status.getUUID(name);
    }

    public String getName(UUID uuid) throws IOException {
        return status.getName(uuid);
    }

    public boolean isOnline() {
        return status.isOnline();
    }

    private void cleanLeaderboard() {
        DataManager data = DataManager.getInstance();
        if (!data.getData().contains("leaderboard")) {
            return;
        }
        for (String key : data.getData().getConfigurationSection("leaderboard").getKeys(false)) {
            if (Bukkit.getOfflinePlayer(UUID.fromString(key)).getName() == null) {
                data.getData().set("leaderboard." + key, null);
            }
        }
        data.saveData();
    }
}
