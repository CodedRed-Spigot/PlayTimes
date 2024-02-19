package me.codedred.playtimes.data.database.manager;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import lombok.Getter;
import me.codedred.playtimes.PlayTimes;
import me.codedred.playtimes.data.DataManager;
import me.codedred.playtimes.data.database.datasource.DataSource;
import me.codedred.playtimes.data.database.datasource.impl.MySQL;
import me.codedred.playtimes.data.database.datasource.impl.SQLite;
import me.codedred.playtimes.data.database.table.UsersTable;
import org.bukkit.Bukkit;

@Getter
public class DatabaseManager {

  private static DatabaseManager instance;

  private DataSource dataSource;
  private UsersTable usersTable;
  private boolean connected = false;
  private final String serverId;

  private Map<UUID, Map<String, Map<String, Long>>> userPlaytimes = new HashMap<>();

  private DatabaseManager() {
    serverId =
      DataManager
        .getInstance()
        .getDBConfig()
        .getString("database-settings.serverId");
    setupDataSource();
  }

  public static DatabaseManager getInstance() {
    if (instance == null) {
      instance = new DatabaseManager();
    }
    return instance;
  }

  private void setupDataSource() {
    String type = DataManager
      .getInstance()
      .getDBConfig()
      .getString("database-settings.type");

    switch (type.toLowerCase()) {
      case "mysql":
        this.dataSource = new MySQL(PlayTimes.getPlugin(PlayTimes.class));
        break;
      case "sqlite":
        this.dataSource = new SQLite(PlayTimes.getPlugin(PlayTimes.class));
        break;
      default:
        throw new IllegalStateException(
          "Unexpected database type: " +
          type +
          ". Accepted Values: 'mysql', 'sqlite'"
        );
    }

    try {
      connected = dataSource != null && dataSource.getConnection() != null;
    } catch (SQLException e) {
      Bukkit
        .getServer()
        .getLogger()
        .warning("[PlayTimes] Error thrown from DatabaseManager!");
    }
  }

  public boolean isConnected() {
    return connected;
  }

  public void load() {
    if (isConnected()) {
      this.usersTable = new UsersTable(dataSource);
      this.usersTable.createTable();
    } else {
      Bukkit
        .getServer()
        .getLogger()
        .warning("[PlayTimes] Couldn't connect to Database!");
    }
  }

  private UsersTable getUsersTable() {
    if (this.usersTable == null) {
      this.usersTable = new UsersTable(dataSource);
    }
    return this.usersTable;
  }

  public void retrievePlaytime(UUID uuid) {
    Map<String, Map<String, Long>> timeMap = getUsersTable()
      .getPlaytimesByUuid(uuid);
    userPlaytimes.put(uuid, timeMap);
  }

  public void updatePlaytime(UUID uuid, Long playtime, Long afktime) {
    getUsersTable()
      .insertOrUpdate(uuid.toString(), serverId, playtime, afktime);

    userPlaytimes
      .computeIfAbsent(uuid, k -> new HashMap<>())
      .computeIfAbsent(serverId, k -> new HashMap<>())
      .put("playtime", playtime);
    userPlaytimes.get(uuid).get(serverId).put("afktime", afktime);
  }

  public boolean hasTimeForServer(UUID uuid, String server) {
    return (
      userPlaytimes.containsKey(uuid) &&
      userPlaytimes.get(uuid) != null &&
      userPlaytimes.get(uuid).containsKey(server)
    );
  }

  public boolean hasTimeForServer(UUID uuid) {
    return (hasTimeForServer(uuid, serverId));
  }

  public Map<String, Long> getTimeForServer(UUID uuid, String server) {
    return userPlaytimes.get(uuid).get(server);
  }

  public Map<String, Long> getTimeForServer(UUID uuid) {
    return getTimeForServer(uuid, serverId);
  }

  public Long getTotalRawtime(UUID uuid) {
    Long rawtime = 0L;

    Map<String, Map<String, Long>> userTimeData = userPlaytimes.get(uuid);

    if (userTimeData != null) {
      for (Map<String, Long> serverData : userTimeData.values()) {
        if (serverData != null) {
          Long individualRawtime = serverData.getOrDefault("playtime", 0L);
          rawtime += individualRawtime;
        }
      }
    }

    return rawtime;
  }

  public Long getTotalAfktime(UUID uuid) {
    Long afktime = 0L;

    Map<String, Map<String, Long>> userTimeData = userPlaytimes.get(uuid);

    if (userTimeData != null) {
      for (Map<String, Long> serverData : userTimeData.values()) {
        if (serverData != null) {
          Long individualAfktime = serverData.getOrDefault("afktime", 0L);
          afktime += individualAfktime;
        }
      }
    }

    return afktime;
  }

  // Calculate the total playtime after subtracting total afktime
  public Long getTotalPlaytime(UUID uuid) {
    Long totalPlaytime = 0L;
    Long totalAfktime = 0L;

    Map<String, Map<String, Long>> userTimeData = userPlaytimes.get(uuid);

    if (userTimeData != null) {
      for (Map<String, Long> serverData : userTimeData.values()) {
        totalPlaytime += serverData.getOrDefault("playtime", 0L);
        totalAfktime += serverData.getOrDefault("afktime", 0L);
      }
    }

    return totalPlaytime - totalAfktime;
  }

  public void purgeOldPlaytimeData() {
    Bukkit
      .getLogger()
      .info("[PlayTimes] Initiating purge of outdated playtime data...");

    int months = DataManager
      .getInstance()
      .getDBConfig()
      .getInt("purge-database.months", 12);

    Bukkit
      .getLogger()
      .info(
        String.format(
          "[PlayTimes] Purging playtime data for server ID '%s' that is older than %d months.",
          serverId,
          months
        )
      );
    this.usersTable.purgeOldPlaytimes(serverId, months);
  }
}
