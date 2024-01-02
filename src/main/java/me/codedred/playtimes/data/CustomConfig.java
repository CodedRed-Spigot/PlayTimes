package me.codedred.playtimes.data;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import me.codedred.playtimes.PlayTimes;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class CustomConfig {

  private final String name;
  private final PlayTimes plugin;
  private FileConfiguration dataConfig = null;
  private File dataConfigFile = null;

  public CustomConfig(PlayTimes plugin, String fileName) {
    this.plugin = plugin;
    this.name = fileName;
    saveDefaultConfig();
  }

  public void reloadConfig() {
    if (this.dataConfigFile == null) {
      this.dataConfigFile = new File(this.plugin.getDataFolder(), name);
    }

    this.dataConfig = YamlConfiguration.loadConfiguration(this.dataConfigFile);

    if (name == "config.yml") checkAndAddDefaults();

    InputStream defConfigStream = this.plugin.getResource(name);
    if (defConfigStream != null) {
      YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(
        new InputStreamReader(defConfigStream)
      );
      this.dataConfig.setDefaults(defConfig);
    }
  }

  private void checkAndAddDefaults() {
    Map<String, Object> requiredKeys = new HashMap<>();
    requiredKeys.put("prefix", "&7[&b&lPlayTimes&7]");
    requiredKeys.put("use-papi-placeholders", false);

    requiredKeys.put(
      "playtime.message",
      new String[] {
        "&b&m=======&b&l[%player%]&b&m=======",
        "&aPlaytime:&f %playtime%",
        "&aTimes Joined:&f %timesjoined%",
        "&aJoin Date:&f %joindate%",
        "&b&m============================",
      }
    );

    requiredKeys.put("playtime.name.second", "s");
    requiredKeys.put("playtime.name.minute", "min ");
    requiredKeys.put("playtime.name.hour", "hr ");
    requiredKeys.put("playtime.name.day", "day ");
    requiredKeys.put("playtime.name.seconds", "s");
    requiredKeys.put("playtime.name.minutes", "mins ");
    requiredKeys.put("playtime.name.hours", "hrs ");
    requiredKeys.put("playtime.name.days", "days ");
    requiredKeys.put("playtime.only-hours", false);
    requiredKeys.put("playtime.show-seconds", true);
    requiredKeys.put("playtime.show-days", true);
    requiredKeys.put("playtime.round-numbers", true);

    requiredKeys.put("afk-settings.enabled", false);
    requiredKeys.put("afk-settings.threshold", 5);
    requiredKeys.put("afk-settings.notify-on-afk.onAFK", true);
    requiredKeys.put("afk-settings.notify-on-afk.onBackFromAFK", true);
    requiredKeys.put(
      "afk-settings.afk-message",
      "&eYou are now marked as AFK."
    );
    requiredKeys.put(
      "afk-settings.back-from-afk-message",
      "&eYou are no longer AFK."
    );

    requiredKeys.put(
      "uptime.message",
      new String[] {
        "&b&m============================",
        "&aServer Uptime:&f %serveruptime%",
        "&b&m============================",
      }
    );

    requiredKeys.put(
      "top-playtime.header",
      "&b******&9[&3&lPlayTime Leaderboards&9]&b*****"
    );
    requiredKeys.put(
      "top-playtime.content",
      "&5%place%) &9&l%player% &9&o- %time%"
    );
    requiredKeys.put(
      "top-playtime.footer",
      "&b****************************************"
    );
    requiredKeys.put("top-playtime.enable-cooldown", true);
    requiredKeys.put("top-playtime.cooldown-seconds", 60);

    requiredKeys.put("date-format", "MM/dd/yyyy");

    requiredKeys.put("messages.noPermission", "&cYou cannot run this command.");
    requiredKeys.put("messages.player-not-found", "&cUnable to locate player.");
    requiredKeys.put(
      "messages.player-never-joined",
      "&cThis player has not joined the server."
    );
    requiredKeys.put(
      "messages.cooldown",
      "&cCommand unavailable for %timeleft% seconds!"
    );

    boolean changesMade = false;
    for (Map.Entry<String, Object> entry : requiredKeys.entrySet()) {
      if (!this.dataConfig.contains(entry.getKey())) {
        this.dataConfig.set(entry.getKey(), entry.getValue());
        changesMade = true;
      }
    }

    if (changesMade) {
      plugin
        .getLogger()
        .info(
          "Missing configuration keys detected in 'Config.yml'. Adding them now."
        );
      saveConfig();
    }
  }

  public FileConfiguration getConfig() {
    if (this.dataConfig == null) reloadConfig();
    return this.dataConfig;
  }

  public void saveConfig() {
    if ((this.dataConfig == null) || (this.dataConfigFile == null)) return;
    try {
      getConfig().save(this.dataConfigFile);
    } catch (IOException ex) {
      this.plugin.getLogger()
        .log(
          Level.SEVERE,
          ex,
          () -> "Could not save config to " + this.dataConfigFile
        );
    }
  }

  public void saveDefaultConfig() {
    if (this.dataConfigFile == null) {
      this.dataConfigFile = new File(this.plugin.getDataFolder(), name);
    }
    if (!this.dataConfigFile.exists()) {
      this.plugin.saveResource(name, false);
    }
  }
}
