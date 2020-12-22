package me.codedred.playtimes.models;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CoolDown {

	private Map<UUID, Long> cooldowns = new HashMap<UUID, Long>();
	
	
	public void remove(UUID uuid) {
		cooldowns.remove(uuid);
	}
	
	
	public void add(UUID uuid, long time) {
		cooldowns.put(uuid, time);
	}
	
	
	public int left(UUID uuid) {
		return (int) (cooldowns.get(uuid)/1000 - (System.currentTimeMillis()/1000));
	}
	
	
	public boolean contains(UUID uuid) {
		if (cooldowns.containsKey(uuid)) {
			if (cooldowns.get(uuid) > System.currentTimeMillis())
				return true;
			remove(uuid);
		}
		return false;
	}
}
