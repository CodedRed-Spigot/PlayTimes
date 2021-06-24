package me.codedred.playtimes.data;

import me.codedred.playtimes.PlayTimes;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Debugger {
	
	public void execute() {
		DataManager data = DataManager.getInstance();
		//Data file update
		if (!data.getData().contains("leaderboard")) {
			//Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.DARK_PURPLE + "[PlayTime] " + ChatColor.WHITE + "Updating data file...");
			Map<String, Long> boardData = new HashMap<>();
			for (String uuid : data.getData().getKeys(false))
				boardData.put(uuid, data.getData().getLong(uuid));
			for (String uuid : data.getData().getKeys(false))
				data.getData().set(uuid, null);
			for (Map.Entry<String, Long> d : boardData.entrySet())
				data.getData().set("leaderboard." + d.getKey(), d.getValue());

		//	Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.DARK_PURPLE + "[PlayTime] " + ChatColor.WHITE + "Data file updated!");
			data.saveData();
		}

	}
}
