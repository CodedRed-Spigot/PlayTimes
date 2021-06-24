package me.codedred.playtimes;

import me.codedred.playtimes.commands.Time;
import me.codedred.playtimes.commands.TopTime;
import me.codedred.playtimes.commands.Uptime;
import me.codedred.playtimes.data.Debugger;
import me.codedred.playtimes.listeners.Join;
import me.codedred.playtimes.listeners.Quit;
import me.codedred.playtimes.server.ServerManager;
import me.codedred.playtimes.statistics.StatManager;
import me.codedred.playtimes.time.TimeManager;
import me.codedred.playtimes.utils.ChatUtil;
import me.codedred.playtimes.utils.ServerUtils;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Method;

public class PlayTimes extends JavaPlugin {
	
	@Override
	public void onEnable() {
		Debugger debug = new Debugger();
		debug.execute();
		
		checkForUpdate();


		ServerManager.getInstance().register();
		StatManager.getInstance().registerStatistics();
		TimeManager.getInstance().registerTimings();
		
		if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
			Expansions exp = new Expansions();
			exp.register();
			getServer().getConsoleSender().sendMessage(ChatColor.DARK_PURPLE + "[PlayTimes] " + ChatColor.WHITE + "PlaceholdersAPI Hooked!");
		}

		registerEvents();
		registerCommands();

		@SuppressWarnings("unused")
        Metrics metrics = new Metrics(this);
		getServer().getConsoleSender().sendMessage(ChatColor.DARK_PURPLE + "[PlayTimes] " + ChatColor.WHITE + "Successfully loaded.");
	}

	@Override
	public void onDisable() {

	}

	private void registerEvents() {
		PluginManager pm = this.getServer().getPluginManager();
		pm.registerEvents(new Join(), this);
		pm.registerEvents(new Quit(), this);
	}

	private void registerCommands() {
		getCommand("playtime").setExecutor(
				new Time());
		getCommand("uptime").setExecutor(
				new Uptime());
		getCommand("topplaytime").setExecutor(
				new TopTime());
	}

	private void checkForUpdate() {
		UpdateChecker updater = new UpdateChecker(this, 58858);
        try {
            if (updater.checkForUpdates()) {
				getServer().getConsoleSender().sendMessage(ChatUtil.format("&eYou are using an older version of PlayTimes!"));
				getServer().getConsoleSender().sendMessage(ChatUtil.format("&eDownload the newest version here:"));
				getServer().getConsoleSender().sendMessage(ChatUtil.format("&bhttps://www.spigotmc.org/resources/58858/"));
            } else {
                getServer().getConsoleSender().sendMessage("[PlayTimes] Plugin is up to date! - "
                				+ getDescription().getVersion());
            }
        } catch (Exception e) {
            getLogger().info("Could not check for updates! Stacktrace:");
            e.printStackTrace();
        }
	}


}
