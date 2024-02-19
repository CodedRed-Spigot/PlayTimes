package me.codedred.playtimes.data;

import me.codedred.playtimes.PlayTimes;
import me.codedred.playtimes.afk.AFKManager;
import me.codedred.playtimes.data.database.manager.DatabaseManager;
import me.codedred.playtimes.server.ServerManager;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class DataManager {

  private static final DataManager instance = new DataManager();
  private final CustomConfig data = new CustomConfig(
    JavaPlugin.getPlugin(PlayTimes.class),
    "data.yml"
  );
  private final CustomConfig db = new CustomConfig(
    JavaPlugin.getPlugin(PlayTimes.class),
    "database.yml"
  );
  private final CustomConfig cfg = new CustomConfig(
    JavaPlugin.getPlugin(PlayTimes.class),
    "config.yml"
  );

  public static DataManager getInstance() {
    return instance;
  }

  public FileConfiguration getConfig() {
    return cfg.getConfig();
  }

  public FileConfiguration getData() {
    return data.getConfig();
  }

  public FileConfiguration getDBConfig() {
    return db.getConfig();
  }

  public void saveData() {
    data.saveConfig();
  }

  public void reloadAll() {
    data.reloadConfig();
    cfg.reloadConfig();
    db.reloadConfig();
    ServerManager.getInstance().updateLookupType();
    if (hasAfkEnabled()) AFKManager.getInstance().reload();
  }

  public void reloadDatabase() {
    db.reloadConfig();
  }

  public boolean hasDatabase() {
    return (
      getDBConfig().getBoolean("database-settings.enabled") &&
      DatabaseManager.getInstance().isConnected()
    );
  }

  public boolean hasAfkEnabled() {
    return getConfig().getBoolean("afk-settings.enabled", false);
  }
}
