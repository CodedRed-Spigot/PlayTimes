package me.codedred.playtimes;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Objects;
import me.codedred.playtimes.afk.AFKManager;
import me.codedred.playtimes.afk.listeners.onChat;
import me.codedred.playtimes.afk.listeners.onInteract;
import me.codedred.playtimes.afk.listeners.onJoinQuit;
import me.codedred.playtimes.afk.listeners.onMove;
import me.codedred.playtimes.commands.Time;
import me.codedred.playtimes.commands.TopTime;
import me.codedred.playtimes.commands.Uptime;
import me.codedred.playtimes.commands.completer.TimeTabCompleter;
import me.codedred.playtimes.data.DataManager;
import me.codedred.playtimes.data.database.manager.DatabaseManager;
import me.codedred.playtimes.listeners.Join;
import me.codedred.playtimes.listeners.Quit;
import me.codedred.playtimes.server.ServerManager;
import me.codedred.playtimes.statistics.StatManager;
import me.codedred.playtimes.time.TimeManager;
import me.codedred.playtimes.utils.ChatUtil;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class PlayTimes extends JavaPlugin {

  @Override
  public void onEnable() {
    checkForUpdate();
    DataManager dataManager = DataManager.getInstance();
    dataManager.reloadAll();

    ServerManager.getInstance().register();
    StatManager.getInstance().registerStatistics();
    TimeManager.getInstance().registerTimings();

    loadDatabase();

    if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
      Expansions exp = new Expansions();
      exp.register();
      getLogger().info("PlaceholdersAPI Hooked!");
    }

    registerEvents();
    registerCommands();

    @SuppressWarnings("unused")
    Metrics metrics = new Metrics(this, 5289);

    AFKManager.getInstance().startAFKChecker();
    getLogger().info("Successfully loaded.");
  }

  @Override
  public void onDisable() {
    AFKManager.getInstance().endAFKChecker();
    getLogger().info("PlayTimes shutting down");
  }

  private void registerEvents() {
    PluginManager pm = getServer().getPluginManager();
    DataManager data = DataManager.getInstance();

    pm.registerEvents(new Join(), this);
    pm.registerEvents(new Quit(), this);

    if (data.hasAfkEnabled()) {
      pm.registerEvents(new onJoinQuit(), this);

      FileConfiguration config = data.getConfig();
      boolean cancelAfkOnMove = config.getBoolean(
        "afk-settings.cancel-afk.on-player-move"
      );
      boolean cancelAfkOnChat = config.getBoolean(
        "afk-settings.cancel-afk.on-player-chat"
      );
      boolean cancelAfkOnInteract = config.getBoolean(
        "afk-settings.cancel-afk.on-player-interact"
      );

      if (cancelAfkOnMove || cancelAfkOnChat || cancelAfkOnInteract) {
        if (cancelAfkOnMove) pm.registerEvents(new onMove(), this);
        if (cancelAfkOnChat) pm.registerEvents(new onChat(), this);
        if (cancelAfkOnInteract) pm.registerEvents(new onInteract(), this);
      }
    }
  }

  private void registerCommands() {
    Objects.requireNonNull(getCommand("playtime")).setExecutor(new Time());
    Objects.requireNonNull(getCommand("uptime")).setExecutor(new Uptime());
    Objects
      .requireNonNull(getCommand("topplaytime"))
      .setExecutor(new TopTime());
    Objects
      .requireNonNull(this.getCommand("pt"))
      .setTabCompleter(new TimeTabCompleter());
  }

  public void loadDatabase() {
    if (
      DataManager
        .getInstance()
        .getDBConfig()
        .getBoolean("database-settings.enabled")
    ) {
      getLogger().info("Connecting to Database...");
      DatabaseManager databaseManager = DatabaseManager.getInstance();

      try {
        if (databaseManager.getDataSource().getConnection() != null) {
          try {
            databaseManager.getDataSource().closeConnection();
          } catch (Exception e) {
            getLogger()
              .warning("Error while trying to close Database connection..");
          }
          databaseManager.load();

          if (
            DataManager
              .getInstance()
              .getDBConfig()
              .getBoolean("purge-database.enabled")
          ) databaseManager.purgeOldPlaytimeData();
        }
      } catch (SQLException e) {
        getLogger().severe("Error loading the database: " + e.getMessage());
        e.printStackTrace();
      }
    }
  }

  private void checkForUpdate() {
    Bukkit
      .getScheduler()
      .runTaskAsynchronously(
        this,
        () -> {
          UpdateChecker updater = new UpdateChecker(this, 58858);
          try {
            if (updater.hasUpdatesAvailable()) {
              getLogger()
                .warning(
                  ChatUtil.format(
                    "You are using an older version of PlayTimes!"
                  )
                );
              getLogger()
                .info(ChatUtil.format("Download the newest version here:"));
              getLogger()
                .info(
                  ChatUtil.format("https://www.spigotmc.org/resources/58858/")
                );
            } else {
              getLogger()
                .info(
                  "[PlayTimes] Plugin is up to date! - " +
                  getDescription().getVersion()
                );
            }
          } catch (IOException e) {
            getLogger().warning("[PlayTimes] Could not check for updates!");
          }
        }
      );
  }
}
