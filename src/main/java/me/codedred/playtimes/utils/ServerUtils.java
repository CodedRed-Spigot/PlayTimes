package me.codedred.playtimes.utils;

import java.lang.reflect.Method;
import me.codedred.playtimes.data.DataManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;

public class ServerUtils {

  private static Boolean isNewerVersion = null;

  private ServerUtils() {
    throw new IllegalStateException("Utility Class");
  }

  public static boolean isNewerVersion() {
    if (isNewerVersion == null) {
      try {
        Class<?> classMaterial = Material.class;
        Method method = classMaterial.getDeclaredMethod(
          "matchMaterial",
          String.class,
          boolean.class
        );
        isNewerVersion = (method != null);
      } catch (ReflectiveOperationException ex) {
        isNewerVersion = false;
      }
    }
    return isNewerVersion;
  }

  /**
   * Checks if the server version is 1.17+
   *
   * @return true if so
   */
  public static boolean isRisenVersion() {
    String version = Bukkit.getServer().getVersion();
    String mcVersion = null;

    if (version.contains("(MC:")) {
      int startIndex = version.indexOf("(MC:") + 5;
      int endIndex = version.indexOf(")", startIndex);
      mcVersion = version.substring(startIndex, endIndex).trim();
    }

    if (mcVersion == null) {
      Bukkit
        .getLogger()
        .warning("PlayTimes failed to extract Minecraft version: " + version);
      return false;
    }

    String[] versionParts = mcVersion.split("\\.");

    try {
      int minorVersion = versionParts.length >= 2
        ? Integer.parseInt(versionParts[1])
        : 0;

      return minorVersion >= 17;
    } catch (NumberFormatException e) {
      Bukkit
        .getLogger()
        .warning("PlayTimes failed to parse Minecraft version: " + mcVersion);
      return false;
    }
  }

  public static boolean hasPAPI() {
    if (
      Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null
    ) return DataManager
      .getInstance()
      .getConfig()
      .getBoolean("use-papi-placeholders");
    return false;
  }
}
