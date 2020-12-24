package me.codedred.playtimes.api;

import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;

import me.codedred.playtimes.PlayTimes;
import me.codedred.playtimes.models.Clock;
import me.codedred.playtimes.utils.Statistics;

public class TimelessServer {

	private Clock clock;
	
	public TimelessServer() {
		this.clock = new Clock();
	}
	
	public List<UUID> getTop10Players() {
		 List<UUID> players = new ArrayList<>();
		 Map<String, Integer> map = getTopTen();
		 Object[] array = map.keySet().toArray();
		 for (int i = 0; i < map.size(); i++)
			 players.add(UUID.fromString(array[i].toString()));
		 
		 return players;
	}
	
	public List<Integer> getTop10Times() {
		 List<Integer> players = new ArrayList<>();
		 Map<String, Integer> map = getTopTen();
		 Object[] array = map.values().toArray();
		 for (int i = 0; i < map.size(); i++)
			 players.add(Integer.parseInt(array[i].toString())/20);
		 return players;
	}
	
	public UUID getNumberOne() {
		return getTop10Players().get(0);
	}
	
	public UUID getNumberTwo() {
		return getTop10Players().get(1);
	}

	public UUID getNumberThree() {
		return getTop10Players().get(2);
	}
	
	public UUID getNumberFour() {
		return getTop10Players().get(3);
	}
	
	public UUID getNumberFive() {
		return getTop10Players().get(4);
	}
	
	public UUID getNumberSix() {
		return getTop10Players().get(5);
	}
	
	public UUID getNumberSeven() {
		return getTop10Players().get(6);
	}
	
	public UUID getNumberEight() {
		return getTop10Players().get(7);
	}
	
	public UUID getNumberNine() {
		return getTop10Players().get(8);
	}
	
	public UUID getNumberTen() {
		return getTop10Players().get(9);
	}
	
	public String getUptime() {
		return clock.getUptime();
	}
	
	public long getUptimeInSeconds() {
		return TimeUnit.MILLISECONDS.toSeconds(ManagementFactory.getRuntimeMXBean().getUptime());
	}
	

	private Map<String, Integer> getTopTen() {
        Map<String, Integer> topTen = new LinkedHashMap<>();
        for (String key : PlayTimes.getPlugin(PlayTimes.class).getData().getKeys(false)) {
        	if (Bukkit.getServer().getOfflinePlayer(UUID.fromString(key)).getName() != null)
        		topTen.put(key, Integer.valueOf(PlayTimes.getPlugin(PlayTimes.class).getData().getString(key)));
        	else {
        		PlayTimes.getPlugin(PlayTimes.class).getData().set(key, null);
        		PlayTimes.getPlugin(PlayTimes.class).data.data.saveConfig();
        	}
        		
        }

        List<Map.Entry<String, Integer>> list = new LinkedList<>(topTen.entrySet());
        list.sort((o1, o2) -> o2.getValue() - o1.getValue());

    	if (list.size() > 20) {
    		List<Map.Entry<String, Integer>> delList = new LinkedList<>(topTen.entrySet());
    		delList = list.subList(20, list.size());

    		for (Entry<String, Integer> key : delList) {
    			PlayTimes.getPlugin(PlayTimes.class).getData().set(key.getKey(), null);
    		}
    		PlayTimes.getPlugin(PlayTimes.class).data.data.saveConfig();
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
