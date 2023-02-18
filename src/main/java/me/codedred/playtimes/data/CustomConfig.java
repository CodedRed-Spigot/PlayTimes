package me.codedred.playtimes.data;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import me.codedred.playtimes.PlayTimes;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;

public class CustomConfig {
	private final PlayTimes plugin;
	private FileConfiguration dataConfig = null;
	private File dataConfigFile = null;
	private final String name;

	public CustomConfig(PlayTimes plugin, String name) {
		this.plugin = plugin;
		this.name = name;
		saveDefaultConfig();
	}

	public void reloadConfig() {
		if (dataConfigFile == null) {
			dataConfigFile = new File(plugin.getDataFolder(),
					name);
		}
		
		dataConfig = YamlConfiguration
				.loadConfiguration(dataConfigFile);

		InputStream defConfigStream = plugin.getResource(name);
		if (defConfigStream != null) {
			YamlConfiguration defConfig = YamlConfiguration
					.loadConfiguration(new InputStreamReader(defConfigStream));
			dataConfig.setDefaults(defConfig);
		}
	}

	public FileConfiguration getConfig() {
		if (dataConfig == null) {
			reloadConfig();
		}
		return dataConfig;
	}

	public void saveConfig() {
		if ((dataConfig == null) || (dataConfigFile == null)) {
			return;
		}
		try {
			getConfig().save(dataConfigFile);
		} catch (IOException ex) {
			plugin.getLogger().log(Level.SEVERE, "Could not save config to "
					+ dataConfigFile, ex);
		}
	} 

	public void saveDefaultConfig() {
		if (dataConfigFile == null) {
			dataConfigFile = new File(plugin.getDataFolder(),
					name);
		}
		if (!dataConfigFile.exists()) {
			plugin.saveResource(name, false);
		}
	}

}