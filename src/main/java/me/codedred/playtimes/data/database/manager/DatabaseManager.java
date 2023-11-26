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

@Getter
public class DatabaseManager {

  private static DatabaseManager instance;

  private DataSource dataSource;
  private UsersTable usersTable;

  private Map<UUID, Map<String, Long>> userPlaytimes = new HashMap<>();

  private DatabaseManager() {
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
  }

  public void load() {
    try {
      if (dataSource.getConnection() != null) {
        this.usersTable = new UsersTable(dataSource);
        this.usersTable.createTable();
      }
    } catch (SQLException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  private UsersTable getUsersTable() {
    if (this.usersTable == null) {
      this.usersTable = new UsersTable(dataSource);
    }
    return this.usersTable;
  }

  // Called onPlayerJoin
  public void retrievePlayTime(UUID uuid) {
    Map<String, Long> timeMap = getUsersTable().getPlaytimesByUuid(uuid);
    userPlaytimes.put(uuid, timeMap);
  }

  // Called onPlayerLeave
  public void updatePlaytime(UUID uuid, Long playtime) {
    getUsersTable()
      .insertOrUpdate(
        uuid.toString(),
        DataManager
          .getInstance()
          .getDBConfig()
          .getString("database-settings.serverId"),
        playtime
      );
    userPlaytimes.remove(uuid);
  }

  // Given the serverId returns the playtime from that server
  public Long getPlayTimeForServer(UUID uuid, String server) {
    if (userPlaytimes.get(uuid).containsKey(server)) {
      return userPlaytimes.get(uuid).get(server);
    }
    return null;
  }

  // %PlayTimes_total%
  public Long getTotalPlayTime(UUID uuid) {
    Long playtime = 0L;
    Map<String, Long> userPlaytimeMap = userPlaytimes.get(uuid);

    if (userPlaytimeMap != null) {
      for (Long individualPlaytime : userPlaytimeMap.values()) {
        playtime += individualPlaytime;
      }
    }

    return playtime;
  }
}
