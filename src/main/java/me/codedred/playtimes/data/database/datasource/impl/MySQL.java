package me.codedred.playtimes.data.database.datasource.impl;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import lombok.val;
import me.codedred.playtimes.PlayTimes;
import me.codedred.playtimes.data.DataManager;
import me.codedred.playtimes.data.database.datasource.DataSource;

public class MySQL implements DataSource {

  private final String connectionUrl;
  private final String user;
  private final String password;

  public MySQL(PlayTimes plugin) {
    val config = DataManager
      .getInstance()
      .getDBConfig()
      .getConfigurationSection("database-settings");
    val host = config.getString("host");
    val port = config.getString("port");
    this.user = config.getString("user");
    this.password = config.getString("password");
    val database = config.getString("database");
    val SSL = config.getBoolean("useSSL");

    this.connectionUrl =
      String.format(
        "jdbc:mysql://%s:%s/%s?autoReconnect=true&useSSL=" + SSL,
        host,
        port,
        database
      );

    try {
      Class.forName("com.mysql.cj.jdbc.Driver");
      plugin.getLogger().info("Successfully connected to database.");
    } catch (ClassNotFoundException e) {
      plugin
        .getLogger()
        .severe("MySQL JDBC Driver not found: " + e.getMessage());
    }
  }

  @Override
  public Connection getConnection() throws SQLException {
    return DriverManager.getConnection(connectionUrl, user, password);
  }

  @Override
  public void closeConnection() throws SQLException {
    // connection.close();
  }
}
