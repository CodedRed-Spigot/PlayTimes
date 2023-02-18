package me.codedred.playtimes.data;

import me.codedred.playtimes.PlayTimes;
import org.bukkit.configuration.file.FileConfiguration;

public class DataManager {
	
	private final static DataManager instance = new DataManager();
	
	public static DataManager getInstance() {
		return instance;
	}
	
	private final CustomConfig data = new CustomConfig(PlayTimes.getPlugin(PlayTimes.class), "data.yml");
	private final CustomConfig cfg = new CustomConfig(PlayTimes.getPlugin(PlayTimes.class), "config.yml");
	//private final CustomConfig holo = new CustomConfig(PlayTimes.getPlugin(PlayTimes.class), "hologram.yml");

	public FileConfiguration getConfig() {
		return cfg.getConfig();
	}

	public FileConfiguration getData() {
		return data.getConfig();
	}

	//public FileConfiguration getHolo() {
	//	return holo.getConfig();
	//}

	public void reloadAll() {
		data.reloadConfig();
		cfg.reloadConfig();
		//holo.reloadConfig();
	}

	public void saveData() {
		data.saveConfig();
	}

	//public void saveHolo() {
	//	holo.saveConfig();
	//}





}
