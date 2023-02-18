package me.codedred.playtimes.utils;

import net.md_5.bungee.api.ChatColor;

public class HexUtil {

	private HexUtil() {
		throw new IllegalStateException("Utility Class");
	}

	public static String hex(String message) {
		StringBuilder builder = new StringBuilder();
		int index = 0;

		while (index < message.length()) {
			int colorCodeStart = message.indexOf("<#", index);
			if (colorCodeStart != -1) {
				int colorCodeEnd = message.indexOf(">", colorCodeStart + 2);
				if (colorCodeEnd != -1) {
					builder.append(message, index, colorCodeStart);
					String hexCode = message.substring(colorCodeStart + 2, colorCodeEnd);
					ChatColor hexColor = ChatColor.of('#' + hexCode);
					builder.append(hexColor);
					index = colorCodeEnd + 1;
					continue;
				}
			}
			builder.append(message.substring(index));
			break;
		}

		return ChatColor.translateAlternateColorCodes('&', builder.toString());
	}
}