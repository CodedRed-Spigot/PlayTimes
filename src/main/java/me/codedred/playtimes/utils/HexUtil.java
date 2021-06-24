package me.codedred.playtimes.utils;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HexUtil {

	private static final Pattern hexPattern = Pattern.compile("<#([A-Fa-f0-9]){6}>");
	public static String hex(String message){
		Matcher matcher = hexPattern.matcher(message);
		while (matcher.find()) {
			ChatColor hexColor = ChatColor.of(matcher.group().substring(1, matcher.group().length() - 1));
			String before = message.substring(0, matcher.start());
			String after = message.substring(matcher.end());
			message = before + hexColor + after;
			matcher = hexPattern.matcher(message);
		}
		return ChatColor.translateAlternateColorCodes('&', message);
	}
}
