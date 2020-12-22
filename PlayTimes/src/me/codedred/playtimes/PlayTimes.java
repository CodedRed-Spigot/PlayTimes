package me.codedred.playtimes;

import java.lang.reflect.Method;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import me.codedred.playtimes.commands.Time;
import me.codedred.playtimes.commands.TopTime;
import me.codedred.playtimes.commands.Uptime;
import me.codedred.playtimes.data.DataManager;
import me.codedred.playtimes.data.Debugger;
import me.codedred.playtimes.listeners.Join;
import me.codedred.playtimes.listeners.Quit;
import me.codedred.playtimes.models.CoolDown;
import me.codedred.playtimes.utils.HexUtil;
import net.md_5.bungee.api.ChatColor;

public class PlayTimes extends JavaPlugin {
	
	public DataManager data;
	public CoolDown cooldown;
	
	@Override
	public void onEnable() {
		this.cooldown = new CoolDown();
		data = DataManager.getInstance();
		Debugger debug = new Debugger(this);
		debug.settings();
		
		checkForUpdate();
		
		if (this.hasPAPI()) {
			Expansions exp = new Expansions(this);
			exp.register();
			getServer().getConsoleSender().sendMessage(ChatColor.DARK_PURPLE + "[PlayTime] " + ChatColor.WHITE + "PlaceholdersAPI Hooked!");
		}
		
		registerEvents();
		registerCommands();
		
		@SuppressWarnings("unused")
		Metrics metrics = new Metrics(this);
		getServer().getConsoleSender().sendMessage(ChatColor.DARK_PURPLE + "[PlayTime] " + ChatColor.WHITE + "Successfully loaded.");
	}
	
	@Override
	public void onDisable() {
		
	}
	
	private void registerEvents() {
		PluginManager pm = this.getServer().getPluginManager();
		pm.registerEvents(new Join(this), this);
		pm.registerEvents(new Quit(this), this);
	}
	
	private void registerCommands() {
		getCommand("playtime").setExecutor(
				new Time(this));
		getCommand("uptime").setExecutor(
				new Uptime(this));
		getCommand("topplaytime").setExecutor(
				new TopTime(this));
		//if (Bukkit.getVersion().contains("1.16"))
		//	getCommand("pte").setExecutor(new Editor(this));
		//else
		//	getCommand("pte").setExecutor(new Editor_R2(this));
	}
	
	public FileConfiguration getConfig() {
		return data.cfg.getConfig();
	}
	
	public FileConfiguration getData() {
		return data.data.getConfig();
	}
	
	public String f(String msg) {
		if (Bukkit.getVersion().contains("1.16"))
			msg = HexUtil.hex(msg);
		return ChatColor.translateAlternateColorCodes('&', msg);
	}
	
	public String fp(String msg) {
		if (Bukkit.getVersion().contains("1.16"))
			msg = HexUtil.hex(msg);
		return ChatColor.translateAlternateColorCodes('&', getConfig().getString("prefix") + " " + msg);
	}
	
	public boolean hasPAPI() {
		if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
			return true;
		}
		return false;
	}
	
	private void checkForUpdate() {
		UpdateChecker updater = new UpdateChecker(this, 58858);
        try {
            if (updater.checkForUpdates()) {
                getServer().getConsoleSender().sendMessage(ChatColor.YELLOW + "=-=-=-=-=-=-=-=-=--=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=");
                getServer().getConsoleSender().sendMessage(ChatColor.YELLOW + "You are using an older version of PlayTime!");
                getServer().getConsoleSender().sendMessage(ChatColor.YELLOW + "Download the newest version here:");
                getServer().getConsoleSender().sendMessage(ChatColor.AQUA + "https://tinyurl.com/y7ltcg8m");
                getServer().getConsoleSender().sendMessage(ChatColor.YELLOW + "=-=-=-=-=-=-=-=-=--=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=");
            } else {
                getServer().getConsoleSender().sendMessage("[PlayTime] Plugin is up to date! - "
                				+ getDescription().getVersion());
            }
        } catch (Exception e) {
            getLogger().info("Could not check for updates! Stacktrace:");
            e.printStackTrace();
        }
	}
	
	public boolean isNewerVersion() {
		 try {
	            Class<?> class_Material = Material.class;
	            Method method = class_Material.getDeclaredMethod("matchMaterial", String.class, Boolean.TYPE);
	            return (method != null);
	        } catch(ReflectiveOperationException ex) {
	        	return false;	
	        }
	}

}
