package me.codedred.playtimes.utils;

import me.codedred.playtimes.data.DataManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;

import java.lang.reflect.Method;

public class ServerUtils {

    private ServerUtils() {
        throw new IllegalStateException("Utility Class");
    }
    private static Boolean isNewerVersion = null;

    public static boolean isNewerVersion() {
        if (isNewerVersion == null) {
            try {
                Class<?> classMaterial = Material.class;
                Method method = classMaterial.getDeclaredMethod("matchMaterial", String.class, boolean.class);
                isNewerVersion = (method != null);
            } catch (ReflectiveOperationException ex) {
                isNewerVersion = false;
            }
        }
        return isNewerVersion;
    }

    /**
     * Checks if the server version is 1.17+
     * @return true if so
     */
    public static boolean isRisenVersion() {
        String version = Bukkit.getServer().getVersion();
        return version.startsWith("1.1") && (version.length() <= 5 || Character.isDigit(version.charAt(5)) && version.charAt(5) >= '7');
    }

    public static boolean hasPAPI() {
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null)
            return DataManager.getInstance().getConfig().getBoolean("use-papi-placeholders");
        return false;
    }
}
