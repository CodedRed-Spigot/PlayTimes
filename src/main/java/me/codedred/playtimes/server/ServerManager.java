package me.codedred.playtimes.server;

import me.codedred.playtimes.data.DataManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.io.IOException;
import java.util.UUID;

public class ServerManager {
    private final static ServerManager instance = new ServerManager();

    public static ServerManager getInstance() {
        return instance;
    }

    private ServerStatus status;

    public void register() {
        if (Bukkit.getOnlineMode())
            status = new ServerOnline();
        else
            status = new ServerOffline();
        cleanLeaderboard();
    }

    public ServerStatus getStatus() {
        return status;
    }

    public UUID getUUID(String name) {
        return getStatus().getUUID(name);
    }

    public String getName(UUID uuid) throws IOException {
        return getStatus().getName(uuid);
    }

    public boolean isOnline() {
        return getStatus().isOnline();
    }

    private void cleanLeaderboard() {
        DataManager data = DataManager.getInstance();
        if (!data.getData().contains("leaderboard"))
            return;
        for (String key : data.getData().getConfigurationSection("leaderboard").getKeys(false)) {
            if (Bukkit.getServer().getOfflinePlayer(UUID.fromString(key)).getName() == null)
                data.getData().set("leaderboard." + key, null);
        }
        data.saveData();
    }
}
