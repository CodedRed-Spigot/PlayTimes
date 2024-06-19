package me.codedred.playtimes.utils;

import net.md_5.bungee.api.ChatColor;

public class HexUtil {

  private HexUtil() {
    throw new IllegalStateException("Utility Class");
  }

  public static String hex(String message) {
    StringBuilder builder = new StringBuilder();
    int length = message.length();

    for (int i = 0; i < length; i++) {
      if (
        message.charAt(i) == '<' &&
        i + 8 < length &&
        message.charAt(i + 1) == '#'
      ) {
        String hexCode = message.substring(i + 2, i + 8);
        if (message.charAt(i + 8) == '>' && isValidHexCode(hexCode)) {
          builder.append(ChatColor.of('#' + hexCode));
          i += 8; // Skip the processed hex code and the '>'
          continue;
        }
      }
      builder.append(message.charAt(i));
    }

    return ChatColor.translateAlternateColorCodes('&', builder.toString());
  }

  private static boolean isValidHexCode(String hexCode) {
    return hexCode.matches("^[a-fA-F0-9]{6}$");
  }
}
