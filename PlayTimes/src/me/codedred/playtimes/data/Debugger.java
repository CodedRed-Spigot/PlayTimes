package me.codedred.playtimes.data;

import java.util.ArrayList;
import java.util.List;

import me.codedred.playtimes.PlayTimes;

public class Debugger {

	private PlayTimes plugin;
	public Debugger(PlayTimes plugin) {
		this.plugin = plugin;
	}
	
	public void settings() {
		if (!plugin.getConfig().contains("prefix")) {
			plugin.getConfig().set("prefix", "&7[&b&lPlayTime&7]");
		}
		if (!plugin.getConfig().contains("date-format")) {
			plugin.getConfig().set("date-format", "MM/dd/yyyy");
		}
		if (!plugin.getConfig().contains("playtime")) {
			plugin.getConfig().set("playtime.name.second", "s");
			plugin.getConfig().set("playtime.name.minute", "min");
			plugin.getConfig().set("playtime.name.hour", "hr");
			plugin.getConfig().set("playtime.name.day", "day");
			plugin.getConfig().set("playtime.name.seconds", "s");
			plugin.getConfig().set("playtime.name.minutes", "mins");
			plugin.getConfig().set("playtime.name.hours", "hrs");
			plugin.getConfig().set("playtime.name.days", "days");
		}
		if (!plugin.getConfig().contains("playtime.name.seconds")) {
			plugin.getConfig().set("playtime.name.seconds", "s");
			plugin.getConfig().set("playtime.name.minutes", "mins");
			plugin.getConfig().set("playtime.name.hours", "hrs");
			plugin.getConfig().set("playtime.name.days", "days");
		}
		if (!plugin.getConfig().contains("playtime.show-seconds")) {
			plugin.getConfig().set("playtime.show-seconds", true);
			plugin.getConfig().set("playtime.show-days", true);
		}
		if (!plugin.getConfig().contains("playtime.show-days")) {
			plugin.getConfig().set("playtime.show-days", true);
		}
		if (!plugin.getConfig().contains("playtime.message")) {
			List<String> p = new ArrayList<String>();
			p.add("&b&m=======&b&l[%player%]&b&m=======");
			p.add("&aPlaytime:&f %time%");
			p.add("&aTimes joined:&f %timesjoined%");
			p.add("&aJoined Date:&f %joindate%");
			p.add("&b&m============================");
			plugin.getConfig().set("playtime.message", p);
		}
		if (!plugin.getConfig().contains("uptime.message")) {
			List<String> p = new ArrayList<String>();
			p.add("&b&m============================");
			p.add("&aServer Uptime:&f %serveruptime%");
			p.add("&b&m============================");
			plugin.getConfig().set("uptime.message", p);
		}
		if (!plugin.getConfig().contains("top-playtime")) {
			plugin.getConfig().set("top-playtime.header", "&b******&9[&3&lPlayTime Leaderboards&9]&b*****");
			plugin.getConfig().set("top-playtime.content", "&5%place%) &9&l%player% &9&o- %time%");
			plugin.getConfig().set("top-playtime.footer", "&b****************************************");
			plugin.getConfig().set("top-playtime.enable-cooldown", true);
			plugin.getConfig().set("top-playtime.cooldown-seconds", 60);
		}
		if (!plugin.getConfig().contains("messages")) {
			plugin.getConfig().set("messages.noPermission", "&cYou cannot run this command.");
			plugin.getConfig().set("messages.player-not-found", "&cPlayer not found.");
			plugin.getConfig().set("messages.cooldown", "&cYou cannot use this command for %timeleft% seconds!");
		}
		if (!plugin.getConfig().contains("messages.player-not-found")) {
			plugin.getConfig().set("messages.player-not-found", "&cPlayer not found.");
		}
		if (!plugin.getConfig().contains("messages.cooldown")) {
			plugin.getConfig().set("messages.cooldown", "&cYou cannot use this command for %timeleft% seconds!");
		}
		if (!plugin.getConfig().contains("hide-players-from-leaderboard")) {
			plugin.getConfig().set("hide-players-from-leaderboard.enabled", true);
			List<String> list = new ArrayList<>(); list.add("Cmaaxx"); list.add("CodedRed");
			plugin.getConfig().set("hide-players-from-leaderboard.players", list);
		}

		plugin.saveConfig();
	}
}
