package me.codedred.playtimes.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CoolDownUtil {

	private static Map<UUID, Long> cooldowns = new HashMap<>();
	
	public static void remove(UUID uuid) {
		cooldowns.remove(uuid);
	}
	
	
	public static void add(UUID uuid, long time) {
		cooldowns.put(uuid, time);
	}
	
	
	public static int left(UUID uuid) {
		return (int) (cooldowns.get(uuid)/1000 - (System.currentTimeMillis()/1000));
	}
	
	
	public static boolean contains(UUID uuid) {
		if (cooldowns.containsKey(uuid)) {
			if (cooldowns.get(uuid) > System.currentTimeMillis())
				return true;
			remove(uuid);
		}
		return false;
	}
}
