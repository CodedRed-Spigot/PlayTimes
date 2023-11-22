package me.codedred.playtimes.data.database.datasource.impl;

import me.codedred.playtimes.PlayTimes;
import me.codedred.playtimes.data.DataManager;
import me.codedred.playtimes.data.database.datasource.DataSource;
import lombok.val;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLite implements DataSource {

    private Connection connection;

    public SQLite(PlayTimes plugin) {
        val config = DataManager.getInstance().getConfig();
        val database = config.getString("database");

        try {
            Class.forName("org.sqlite.JDBC");
            String url = "jdbc:sqlite:" + plugin.getDataFolder() + "/" + database + ".db";
            this.connection = DriverManager.getConnection(url);
            plugin.getLogger().info("Successfully connected to database.");
        } catch (SQLException | ClassNotFoundException exception) {
            plugin.getLogger().severe("ERROR! Database failed to connect. Please check your config.yml and try again.");
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
