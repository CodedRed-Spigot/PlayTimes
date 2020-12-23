package me.codedred.playtimes.utils;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import me.codedred.playtimes.PlayTimes;

public class Statistics {
	
	public static long getPlayerStatistic(UUID player, String stat)  {
		
        File worldFolder = new File(Bukkit.getServer().getWorlds().get(0).getWorldFolder(), "stats");
        File playerStatistics = new File(worldFolder, player + ".json");
        
        
        if(playerStatistics.exists()) {
            JSONParser parser = new JSONParser();
            JSONObject jsonObject = null;
            try {
                try {
					jsonObject = (JSONObject) parser.parse(new FileReader(playerStatistics));
				} catch (ParseException e) {
					return 0;
				}
            } catch (IOException e) {
            	return 0;
            }
            JSONObject pilot = null;
            if (stat.equalsIgnoreCase("PLAYTIME")) {
            	try {
    				OfflinePlayer op = Bukkit.getOfflinePlayer(player);
    				if (PlayTimes.getPlugin(PlayTimes.class).isNewerVersion())
    					return op.getPlayer().getStatistic((Statistic.valueOf("PLAY_ONE_MINUTE")));
    				else
    					return op.getPlayer().getStatistic((Statistic.valueOf("PLAY_ONE_TICK")));
            		/*if (isNewerVersion()) {
            			pilot = (JSONObject) jsonObject.get("stats");
            			JSONObject second = (JSONObject) pilot.get("minecraft:custom");
            			return (long) second.get("minecraft:play_one_minute");
            		}
            		return (long) jsonObject.get("stat.playOneMinute");*/
            	} catch (Exception e) {
            		return 0;
            	}
            }
            else if (stat.equalsIgnoreCase("LEAVE")) {
            	try {
            		if (isNewerVersion()) {
            			pilot = (JSONObject) jsonObject.get("stats");
            			JSONObject second = (JSONObject) pilot.get("minecraft:custom");
            			return (long) second.get("minecraft:leave_game");
            		}
            			return (long) jsonObject.get("stat.leaveGame");
            	} catch (Exception e) {
            		return 0;
            	}
            }
            else if (stat.equalsIgnoreCase("REST")) {
            	try {
            		if (isNewerVersion()) {
            			pilot = (JSONObject) jsonObject.get("stats");
            			JSONObject second = (JSONObject) pilot.get("minecraft:custom");
            			return (long) second.get("minecraft:time_since_rest");
            		}
            			return (long) jsonObject.get("stat.timeSinceDeath");
            	} catch (Exception e) {
            		return 0;
            	}
            }
        }
        return 0;
    }
	
	public static boolean playerJoinedBefore(UUID player)  {
		
        File worldFolder = new File(Bukkit.getServer().getWorlds().get(0).getWorldFolder(), "stats");
        File playerStatistics = new File(worldFolder, player + ".json");
        if(playerStatistics.exists())
            return true;
        return false;
	}
	
	private static boolean isNewerVersion() {
		 try {
	            Class<?> class_Material = Material.class;
	            Method method = class_Material.getDeclaredMethod("matchMaterial", String.class, Boolean.TYPE);
	            return (method != null);
	        } catch(ReflectiveOperationException ex) {
	        	return false;	
	        }
	}


}
