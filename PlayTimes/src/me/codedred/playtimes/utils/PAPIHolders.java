package me.codedred.playtimes.utils;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import me.clip.placeholderapi.PlaceholderAPI;

public class PAPIHolders {

	/***
	 * Translates PAPI holders
	 * 
	 * @param player - receiver
	 * @param msg - message that is being checked/translated
	 * @return fixed message with updated PAPI holders
	 */
	public static String getHolders(Player player, String msg) {
		try {
			return PlaceholderAPI.setPlaceholders(player, msg);
		} catch (Exception e) {
			return msg;
		}
	}
	
	/***
	 * Translates PAPI holders for offline player
	 * 
	 * @param player - receiver
	 * @param msg - message that is being checked/translated
	 * @return fixed message with updated PAPI holders
	 */
	public static String getHolders(OfflinePlayer player, String msg) {
		try {
			return PlaceholderAPI.setPlaceholders(player, msg);
		} catch (Exception e) {
			return msg;
		}
	}
}
