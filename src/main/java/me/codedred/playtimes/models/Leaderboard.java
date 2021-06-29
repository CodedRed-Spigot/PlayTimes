package me.codedred.playtimes.models;

import me.codedred.playtimes.data.DataManager;
import me.codedred.playtimes.statistics.StatManager;
import me.codedred.playtimes.statistics.StatisticType;

import java.util.*;

public class Leaderboard {

	/***
	 * Grabs the uuids from the data file, sorts them, then removes redundant uuids and updates the top 10
	 * @return listing of top 10 players
	 */
	public Map<String, Integer> getTopTen() {
        Map<String, Integer> topTen = new LinkedHashMap<>();
		DataManager data = DataManager.getInstance();
		if (!data.getData().contains("leaderboard"))
			return topTen;
        for (String key : data.getData().getConfigurationSection("leaderboard").getKeys(false))
			topTen.put(key, Integer.valueOf(data.getData().getString("leaderboard." + key)));

        List<Map.Entry<String, Integer>> list = new LinkedList<>(topTen.entrySet());
        list.sort((o1, o2) -> o2.getValue() - o1.getValue());

    	if (list.size() > 20) {
    		List<Map.Entry<String, Integer>> delList = list.subList(20, list.size());

    		for (Map.Entry<String, Integer> key : delList)
    			data.getData().set("leaderboard." + key.getKey(), null);
    		data.saveData();
		}

        if (list.size() > 9)
        	list = list.subList(0, 10);

        topTen.clear();

        for (Map.Entry<String, Integer> aList : list)
            topTen.put(aList.getKey(), aList.getValue());

        topTen = updateTimes(topTen);
        return topTen;
    }

	/***
	 * Updates the top ten times.
	 * Need to find better solution eventually.
	 * @param topTen - Map of top ten uuids and times
	 * @return updated map
	 */
	private Map<String, Integer> updateTimes(Map<String, Integer> topTen) {

		StatManager stats = StatManager.getInstance();
		topTen.replaceAll((u, v) -> (int) stats.getPlayerStat(UUID.fromString(u), StatisticType.PLAYTIME));

		List<Map.Entry<String, Integer>> list = new LinkedList<>(topTen.entrySet());
        list.sort((o1, o2) -> o2.getValue() - o1.getValue());

        topTen.clear();

        for (Map.Entry<String, Integer> aList : list)
            topTen.put(aList.getKey(), aList.getValue());
		
        return topTen;
	}
	
	
	
}
