package me.codedred.playtimes;

import me.codedred.playtimes.commands.Time;
import me.codedred.playtimes.commands.TopTime;
import me.codedred.playtimes.commands.Uptime;
import me.codedred.playtimes.listeners.Join;
import me.codedred.playtimes.listeners.Quit;
import me.codedred.playtimes.server.ServerManager;
import me.codedred.playtimes.statistics.StatManager;
import me.codedred.playtimes.utils.ChatUtil;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.Objects;

public class PlayTimes extends JavaPlugin {
	
	@Override
	public void onEnable() {
		checkForUpdate();


		ServerManager.getInstance().register();
		StatManager.getInstance().registerStatistics();
		
		if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
			Expansions exp = new Expansions();
			exp.register();
			getLogger().info(ChatColor.DARK_PURPLE + "[PlayTimes] " + ChatColor.WHITE + "PlaceholdersAPI Hooked!");
		}

		registerEvents();
		registerCommands();

		@SuppressWarnings("unused")
        Metrics metrics = new Metrics(this);
		getLogger().info(ChatColor.DARK_PURPLE + "[PlayTimes] " + ChatColor.WHITE + "Successfully loaded.");
	}

	@Override
	public void onDisable() {
		getLogger().info("PlayTimes shutting down");
	}

	private void registerEvents() {
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(new Join(), this);
		pm.registerEvents(new Quit(), this);
	}

	private void registerCommands() {
		Objects.requireNonNull(getCommand("playtime")).setExecutor(
				new Time());
		Objects.requireNonNull(getCommand("uptime")).setExecutor(
				new Uptime());
		Objects.requireNonNull(getCommand("topplaytime")).setExecutor(
				new TopTime());
	}

	private void checkForUpdate() {
		Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
			UpdateChecker updater = new UpdateChecker(this, 58858);
			try {
				if (updater.checkForUpdates()) {
					getLogger().warning(ChatUtil.format("&eYou are using an older version of PlayTimes!"));
					getLogger().info(ChatUtil.format("&eDownload the newest version here:"));
					getLogger().info(ChatUtil.format("&bhttps://www.spigotmc.org/resources/58858/"));
				} else {
					getLogger().info("[PlayTimes] Plugin is up to date! - " + getDescription().getVersion());
				}
			} catch (IOException e) {
				getLogger().warning("[PlayTimes] Could not check for updates!");
			}
		});
	}


}
