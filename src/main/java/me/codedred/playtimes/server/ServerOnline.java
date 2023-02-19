package me.codedred.playtimes.server;

import com.google.gson.*;
import me.codedred.playtimes.statistics.StatManager;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class ServerOnline implements ServerStatus {

    @Override
    public UUID getUUID(@NotNull String name) {
        UUID uuid = Bukkit.getOfflinePlayer(name).getUniqueId();
        if (StatManager.getInstance().hasJoinedBefore(uuid)) {
            return null;
        }
        return uuid;
    }

    @Override
    public boolean isOnline() {
        return true;
    }

    @Override
    public String getName(UUID uuid) throws IOException {
        try (InputStream is = new URL("https://mcapi.ca/player/profile/" + uuid).openStream()) {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            JsonElement root = JsonParser.parseReader(rd);

            JsonObject rootObj = root.getAsJsonObject();

            return rootObj.get("name").getAsString();
        } catch (IOException e) {
            e.printStackTrace();
            return "User Not Found";
        }
    }
}