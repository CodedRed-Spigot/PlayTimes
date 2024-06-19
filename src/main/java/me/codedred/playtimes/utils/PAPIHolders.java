package me.codedred.playtimes.utils;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class PAPIHolders {

  private PAPIHolders() {
    throw new IllegalStateException("Utility class");
  }

  /**
   * Translates PAPI holders
   *
   * @param player the receiver of the message
   * @param msg    the message that is being checked/translated
   * @return the fixed message with updated PAPI holders
   */
  public static String getHolders(Player player, String msg) {
    return PlaceholderAPI.setPlaceholders(player, msg);
  }

  /**
   * Translates PAPI holders for offline players
   *
   * @param player the receiver of the message
   * @param msg    the message that is being checked/translated
   * @return the fixed message with updated PAPI holders
   */
  public static String getHolders(OfflinePlayer player, String msg) {
    return PlaceholderAPI.setPlaceholders(player, msg);
  }
}
