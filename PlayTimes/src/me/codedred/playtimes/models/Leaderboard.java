package me.codedred.playtimes.models;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.Bukkit;

import me.codedred.playtimes.PlayTimes;
import me.codedred.playtimes.utils.Statistics;

public class Leaderboard {

	private PlayTimes plugin;
	public Leaderboard(PlayTimes plugin) {
		this.plugin = plugin;
	}
	
	public Map<String, Integer> getTopTen() {
        Map<String, Integer> topTen = new LinkedHashMap<>();

        for (String key : plugin.getData().getKeys(false)) {
        	if (Bukkit.getServer().getOfflinePlayer(UUID.fromString(key)).getName() != null)
        		topTen.put(key, Integer.valueOf(plugin.getData().getString(key)));
        	else {
        		plugin.getData().set(key, null);
        		plugin.data.data.saveConfig();
        	}
        		
        }

        List<Map.Entry<String, Integer>> list = new LinkedList<>(topTen.entrySet());
        list.sort((o1, o2) -> o2.getValue() - o1.getValue());

    	if (list.size() > 20) {
    		List<Map.Entry<String, Integer>> delList = new LinkedList<>(topTen.entrySet());
    		delList = list.subList(20, list.size());

    		for (Entry<String, Integer> key : delList) {
    			plugin.getData().set(key.getKey(), null);
    		}
    		plugin.data.data.saveConfig();
		}
        
        if (list.size() > 9) 
        	list = list.subList(0, 10);
        
        topTen.clear();

        for (Map.Entry<String, Integer> aList : list) {
            topTen.put(aList.getKey(), aList.getValue());
        }

        
        
        topTen = updateTimes(topTen);
        return topTen;
    }
	
	private Map<String, Integer> updateTimes(Map<String, Integer> topTen) {
		
		UUID player = null;
		long time = 0;
		for (String uuid : topTen.keySet()) {
			player = UUID.fromString(uuid);
			time = Statistics.getPlayerStatistic(player, "PLAYTIME");
			topTen.put(uuid, (int) time);
		}
		
		List<Map.Entry<String, Integer>> list = new LinkedList<>(topTen.entrySet());
        list.sort((o1, o2) -> o2.getValue() - o1.getValue());

        topTen.clear();

        for (Map.Entry<String, Integer> aList : list) {
            topTen.put(aList.getKey(), aList.getValue());
        }
		
        return topTen;
		
	}
	
	
	
}
