package me.codedred.playtimes.data.database.datasource.impl;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import lombok.val;
import me.codedred.playtimes.PlayTimes;
import me.codedred.playtimes.data.DataManager;
import me.codedred.playtimes.data.database.datasource.DataSource;

public class MySQL implements DataSource {

  private Connection connection;

  public MySQL(PlayTimes plugin) {
    val config = DataManager
      .getInstance()
      .getConfig()
      .getConfigurationSection("database-settings");

    val host = config.getString("host");
    val port = config.getString("port");
    val user = config.getString("user");
    val password = config.getString("password");
    val database = config.getString("database");

    val connectionUrl = String.format(
      "jdbc:mysql://%s:%s/%s?autoReconnect=true",
      host,
      port,
      database
    );

    try {
      Class.forName("com.mysql.cj.jdbc.Driver");

      this.connection =
        DriverManager.getConnection(connectionUrl, user, password);
      plugin.getLogger().info("Successfully connected to database.");
    } catch (SQLException | ClassNotFoundException exception) {
      plugin
        .getLogger()
        .severe(
          "ERROR! Database failed to connect. Please check your config.yml and try again."
        );
    }
  }

  @Override
  public Connection getConnection() {
    return connection;
  }

  @Override
  public void closeConnection() throws SQLException {
    connection.close();
  }
}
