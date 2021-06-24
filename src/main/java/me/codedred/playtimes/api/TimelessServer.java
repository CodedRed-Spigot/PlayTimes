package me.codedred.playtimes.api;

import me.codedred.playtimes.models.Leaderboard;
import me.codedred.playtimes.statistics.StatManager;
import org.bukkit.Bukkit;
import me.codedred.playtimes.PlayTimes;

import java.lang.management.ManagementFactory;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

public class TimelessServer {
	
	public List<UUID> getTop10Players() {
		 List<UUID> players = new ArrayList<>();
         Leaderboard board = new Leaderboard();
		 Map<String, Integer> map = board.getTopTen();
		 Object[] array = map.keySet().toArray();
		 for (int i = 0; i < map.size(); i++)
			 players.add(UUID.fromString(array[i].toString()));
		 
		 return players;
	}
	
	public List<Integer> getTop10Times() {
		 List<Integer> players = new ArrayList<>();
         Leaderboard board = new Leaderboard();
		 Map<String, Integer> map = board.getTopTen();
		 Object[] array = map.values().toArray();
		 for (int i = 0; i < map.size(); i++)
			 players.add(Integer.parseInt(array[i].toString())/20);
		 return players;
	}

	public Map<String, Integer> getTopMap() {
        Leaderboard board = new Leaderboard();
        return board.getTopTen();
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
		return StatManager.getInstance().getUptime();
	}
	
	public long getUptimeInSeconds() {
		return TimeUnit.MILLISECONDS.toSeconds(ManagementFactory.getRuntimeMXBean().getUptime());
	}


}
