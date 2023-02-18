package me.codedred.playtimes.utils;

import me.codedred.playtimes.data.DataManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;

import java.lang.reflect.Method;

public class ServerUtils {

    public static boolean isNewerVersion() {
        try {
            Class<?> class_Material = Material.class;
            Method method = class_Material.getDeclaredMethod("matchMaterial", String.class, Boolean.TYPE);
            return (method != null);
        } catch(ReflectiveOperationException ex) {
            return false;
        }
    }

    public static boolean isRisenVersion() {
        return Bukkit.getServer().getVersion().contains("1.17") || Bukkit.getServer().getVersion().contains("1.18")
                || Bukkit.getServer().getVersion().contains("1.19");
    }

    public static boolean hasPAPI() {
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null)
            return DataManager.getInstance().getConfig().getBoolean("use-papi-placeholders");
        return false;
    }
}
