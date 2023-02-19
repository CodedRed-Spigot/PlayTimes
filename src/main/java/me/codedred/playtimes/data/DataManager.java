package me.codedred.playtimes.data;

import me.codedred.playtimes.PlayTimes;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class DataManager {

	private static final DataManager instance = new DataManager();
    private final CustomConfig data = new CustomConfig(JavaPlugin.getPlugin(PlayTimes.class), "data.yml");
    private final CustomConfig cfg = new CustomConfig(JavaPlugin.getPlugin(PlayTimes.class), "config.yml");

    public static DataManager getInstance() {
        return instance;
    }

    public FileConfiguration getConfig() {
        return cfg.getConfig();
    }

    public FileConfiguration getData() {
        return data.getConfig();
    }

    public void reloadAll() {
        data.reloadConfig();
        cfg.reloadConfig();
    }

    public void saveData() {
        data.saveConfig();
    }


}
