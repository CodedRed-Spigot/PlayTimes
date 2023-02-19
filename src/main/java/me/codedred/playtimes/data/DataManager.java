package me.codedred.playtimes.data;

import me.codedred.playtimes.PlayTimes;
import org.bukkit.configuration.file.FileConfiguration;

public class DataManager {

	private static final DataManager instance = new DataManager();
    private final CustomConfig data = new CustomConfig(PlayTimes.getPlugin(PlayTimes.class), "data.yml");
    private final CustomConfig cfg = new CustomConfig(PlayTimes.getPlugin(PlayTimes.class), "config.yml");

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
