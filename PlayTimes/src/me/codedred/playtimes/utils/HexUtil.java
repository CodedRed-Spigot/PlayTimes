package me.codedred.playtimes.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.md_5.bungee.api.ChatColor;

public class HexUtil {
	
	private static Pattern pattern = Pattern.compile("#[a-fA-F0-9]{6}");

	public static String hex(String msg) {
		Matcher match = pattern.matcher(msg);
		while (match.find()) {
			String color = msg.substring(match.start(), match.end());
			msg = msg.replace(color, ChatColor.of(color) + "");
			match = pattern.matcher(msg);
		}
		return msg;
	}
}
