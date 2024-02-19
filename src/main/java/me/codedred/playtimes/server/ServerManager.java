package me.codedred.playtimes.server;

import java.io.IOException;
import java.util.UUID;
import me.codedred.playtimes.data.DataManager;
import org.bukkit.Bukkit;

public class ServerManager {

  private static final ServerManager instance = new ServerManager();

  public static ServerManager getInstance() {
    return instance;
  }

  private ServerStatus status;
  private boolean offlineLookup = false;

  public void register() {
    if (Bukkit.getOnlineMode()) {
      status = new ServerOnline();
    } else {
      status = new ServerOffline();
    }
    cleanLeaderboard();
    updateLookupType();
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

  public boolean isOfflineLookup() {
    return offlineLookup;
  }

  public void updateLookupType() {
    DataManager dataManager = DataManager.getInstance();
    boolean databaseEnabled = dataManager
      .getDBConfig()
      .getBoolean("database-settings.enabled");
    String lookupType = dataManager
      .getConfig()
      .getString("uuid-lookups.type", "")
      .toLowerCase();

    boolean serverOnline = isOnline();

    if ("online".equals(lookupType)) {
      offlineLookup = false;
    } else {
      offlineLookup =
        (!serverOnline && !databaseEnabled) || "offline".equals(lookupType);
    }
  }

  private void cleanLeaderboard() {
    DataManager data = DataManager.getInstance();
    if (!data.getData().contains("leaderboard")) return;
    for (String key : data
      .getData()
      .getConfigurationSection("leaderboard")
      .getKeys(false)) {
      if (
        Bukkit.getServer().getOfflinePlayer(UUID.fromString(key)).getName() ==
        null
      ) data.getData().set("leaderboard." + key, null);
    }
    data.saveData();
  }
}
