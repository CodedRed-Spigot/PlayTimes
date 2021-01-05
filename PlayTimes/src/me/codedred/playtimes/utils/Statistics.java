package me.codedred.playtimes.utils;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Statistics {
	
	public static long getPlayerStatistic(UUID player, String stat)  {
		
        File worldFolder = new File(Bukkit.getServer().getWorlds().get(0).getWorldFolder(), "stats");
        File playerStatistics = new File(worldFolder, player + ".json");
        
        if(playerStatistics.exists()) {
            try {
                JSONParser parser = new JSONParser();
                JSONObject jsonObject = (JSONObject) parser.parse(new FileReader(playerStatistics));
                
                JSONObject pilot = null; // changes depending on stat
                JSONObject passenger = null;
           
                
                if (stat.equalsIgnoreCase("PLAYTIME")) {
                	if (isNewerVersion()) {
                		pilot = (JSONObject) jsonObject.get("stats");
                		passenger = (JSONObject) pilot.get("minecraft:custom");
                		return (long) passenger.get("minecraft:play_one_minute");
                	}
                	return (long) jsonObject.get("stat.playOneMinute");
                }
                else if (stat.equalsIgnoreCase("LEAVE")) {
            		if (isNewerVersion()) {
            			pilot = (JSONObject) jsonObject.get("stats");
            			JSONObject second = (JSONObject) pilot.get("minecraft:custom");
            			return (long) second.get("minecraft:leave_game");
            		}
            		return (long) jsonObject.get("stat.leaveGame");
                }
                else if (stat.equalsIgnoreCase("REST")) {
            		if (isNewerVersion()) {
            			pilot = (JSONObject) jsonObject.get("stats");
            			JSONObject second = (JSONObject) pilot.get("minecraft:custom");
            			return (long) second.get("minecraft:time_since_rest");
            		}
            		return (long) jsonObject.get("stat.timeSinceDeath");
                }
			} catch (IOException | ParseException e) {
				//e.printStackTrace();
				// return 0;
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
