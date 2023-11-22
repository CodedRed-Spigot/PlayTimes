package me.codedred.playtimes.data.database.manager;

import me.codedred.playtimes.PlayTimes;
import me.codedred.playtimes.data.database.datasource.impl.SQLite;
import me.codedred.playtimes.data.database.table.UsersTable;
import me.codedred.playtimes.data.database.datasource.DataSource;
import me.codedred.playtimes.data.database.datasource.impl.MySQL;
import lombok.Getter;

@Getter
public class DatabaseManager {

    private final PlayTimes plugin;

    private DataSource dataSource;
    private UsersTable usersTable;

    public DatabaseManager(PlayTimes plugin) {
        this.plugin = plugin;
        setupDataSource();
    }

    private void setupDataSource() {
        String type = plugin.getConfig().getString("database-settings.type");

        switch (type.toLowerCase()) {
            case "mysql":
                this.dataSource = new MySQL(plugin);
                break;
            case "sqlite":
                this.dataSource = new SQLite(plugin);
                break;
            default:
                throw new IllegalStateException("Unexpected database type: " + type + ". Accepted Values: 'mysql', 'sqlite'");
        }
    }

    public void load() {
        if (dataSource.getConnection() != null) {
            this.usersTable = new UsersTable(dataSource);
            this.usersTable.createTable();
        }
    }
}

