package me.codedred.playtimes.data.database.table;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import lombok.val;
import me.codedred.playtimes.data.database.datasource.DataSource;
import me.codedred.playtimes.utils.Async;

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
      "PRIMARY KEY (`uniqueId`, `serverId`))",
      TABLE_NAME
    );

    try (
      val preparedStatement = dataSource
        .getConnection()
        .prepareStatement(createTableSql)
    ) {
      preparedStatement.executeUpdate();
    } catch (SQLException exception) {
      exception.printStackTrace();
    }
  }

  public void insertOrUpdate(String uuid, String serverId, long playtime) {
    Async.run(() -> {
      String query = String.format(
        "INSERT INTO `%s` (`uniqueId`, `serverId`, `playtime`) VALUES (?, ?, ?) " +
        "ON DUPLICATE KEY UPDATE `playtime` = `playtime` + ?",
        TABLE_NAME
      );

      try (
        val preparedStatement = dataSource
          .getConnection()
          .prepareStatement(query)
      ) {
        preparedStatement.setString(1, uuid);
        preparedStatement.setString(2, serverId);
        preparedStatement.setLong(3, playtime);
        preparedStatement.setLong(4, playtime);

        preparedStatement.executeUpdate();
      } catch (SQLException exception) {
        exception.printStackTrace();
      }
    });
  }

  public Map<String, Long> getPlaytimesByUuid(UUID uuid) {
    Map<String, Long> playtimes = new HashMap<>();

    try (
      val preparedStatement = dataSource
        .getConnection()
        .prepareStatement(
          String.format("SELECT * FROM `%s` WHERE `uniqueId` = ?", TABLE_NAME)
        )
    ) {
      preparedStatement.setString(1, uuid.toString());

      try (val resultSet = preparedStatement.executeQuery()) {
        while (resultSet.next()) {
          String serverId = resultSet.getString("serverId");
          long playtime = resultSet.getLong("playtime");
          playtimes.put(serverId, playtime);
        }
      }
    } catch (SQLException exception) {
      exception.printStackTrace();
    }

    return playtimes;
  }
}
