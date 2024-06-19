package me.codedred.playtimes.data.database.table;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import me.codedred.playtimes.data.database.datasource.DataSource;
import me.codedred.playtimes.utils.Async;
import org.bukkit.Bukkit;

public class UsersTable {

  private static final String TABLE_NAME = "playtimes";
  private final DataSource dataSource;

  public UsersTable(DataSource dataSource) {
    this.dataSource = dataSource;
  }

  public void createTable() {
    String createTableSql = String.format(
      "CREATE TABLE IF NOT EXISTS `%s` (" +
      "`uniqueId` VARCHAR(36) NOT NULL," +
      "`serverId` VARCHAR(255) NOT NULL," +
      "`playtime` BIGINT NOT NULL DEFAULT 0," +
      "`afktime` BIGINT NOT NULL DEFAULT 0," +
      "`lastUpdated` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP," +
      "PRIMARY KEY (`uniqueId`, `serverId`))",
      TABLE_NAME
    );

    try (
      Connection conn = dataSource.getConnection();
      PreparedStatement preparedStatement = conn.prepareStatement(
        createTableSql
      )
    ) {
      preparedStatement.executeUpdate();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public void insertOrUpdate(
    String uuid,
    String serverId,
    long playtime,
    long afktime
  ) {
    Async.run(() -> {
      String query = String.format(
        "INSERT INTO `%s` (`uniqueId`, `serverId`, `playtime`, `afktime`) VALUES (?, ?, ?, ?) " +
        "ON DUPLICATE KEY UPDATE `playtime` = ?, `afktime` = ?",
        TABLE_NAME
      );

      try (
        Connection conn = dataSource.getConnection();
        PreparedStatement preparedStatement = conn.prepareStatement(query)
      ) {
        preparedStatement.setString(1, uuid);
        preparedStatement.setString(2, serverId);
        preparedStatement.setLong(3, playtime);
        preparedStatement.setLong(4, afktime);
        preparedStatement.setLong(5, playtime);
        preparedStatement.setLong(6, afktime);

        preparedStatement.executeUpdate();
      } catch (SQLException e) {
        e.printStackTrace();
      }
    });
  }

  public Map<String, Map<String, Long>> getPlaytimesByUuid(UUID uuid) {
    Map<String, Map<String, Long>> timesMap = new HashMap<>();

    try (
      Connection conn = dataSource.getConnection();
      PreparedStatement preparedStatement = conn.prepareStatement(
        String.format("SELECT * FROM `%s` WHERE `uniqueId` = ?", TABLE_NAME)
      )
    ) {
      preparedStatement.setString(1, uuid.toString());

      try (ResultSet resultSet = preparedStatement.executeQuery()) {
        while (resultSet.next()) {
          String serverId = resultSet.getString("serverId");
          long playtime = resultSet.getLong("playtime");
          long afktime = resultSet.getLong("afktime");

          Map<String, Long> timeData = new HashMap<>();
          timeData.put("playtime", playtime);
          timeData.put("afktime", afktime);

          timesMap.put(serverId, timeData);
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }

    return timesMap;
  }

  public Map<String, Timestamp> getLastUpdatedTimesByUuid(UUID uuid) {
    Map<String, Timestamp> lastUpdatedTimes = new HashMap<>();

    String query = String.format(
      "SELECT `serverId`, `lastUpdated` FROM `%s` WHERE `uniqueId` = ?",
      TABLE_NAME
    );
    try (
      Connection conn = dataSource.getConnection();
      PreparedStatement preparedStatement = conn.prepareStatement(query)
    ) {
      preparedStatement.setString(1, uuid.toString());

      try (ResultSet resultSet = preparedStatement.executeQuery()) {
        while (resultSet.next()) {
          String serverId = resultSet.getString("serverId");
          Timestamp lastUpdated = resultSet.getTimestamp("lastUpdated");
          lastUpdatedTimes.put(serverId, lastUpdated);
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }

    return lastUpdatedTimes;
  }

  /**
   * Deletes rows from the database where the last update was more than the specified number of months ago.
   *
   * @param serverId The server ID to match rows.
   * @param months   The number of months to check for old data.
   */
  public void purgeOldPlaytimes(String serverId, int months) {
    Async.run(() -> {
      String query = String.format(
        "DELETE FROM `%s` WHERE `serverId` = ? AND `lastUpdated` < (NOW() - INTERVAL ? MONTH)",
        TABLE_NAME
      );

      try (
        Connection conn = dataSource.getConnection();
        PreparedStatement preparedStatement = conn.prepareStatement(query)
      ) {
        preparedStatement.setString(1, serverId);
        preparedStatement.setInt(2, months);

        int deletedRows = preparedStatement.executeUpdate();
        Bukkit
          .getLogger()
          .info(
            "[PlayTimes] Deleted " +
            deletedRows +
            " rows for serverId " +
            serverId +
            " older than " +
            months +
            " months."
          );
      } catch (SQLException e) {
        e.printStackTrace();
      }
    });
  }
}
