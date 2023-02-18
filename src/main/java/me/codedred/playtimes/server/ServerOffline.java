package me.codedred.playtimes.server;

import com.google.gson.*;
import me.codedred.playtimes.statistics.StatManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class ServerOffline implements ServerStatus {

    @Override
    public UUID getUUID(String name) {
        try {
            InputStream is = new URL("https://api.mojang.com/users/profiles/minecraft/" + name).openStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            JsonObject rootobj = new Gson().fromJson(rd, JsonObject.class);
            String fixedName = rootobj.get("name").getAsString();
            is.close();
            UUID uuid = UUID.nameUUIDFromBytes(("OfflinePlayer:" + fixedName).getBytes(StandardCharsets.UTF_8));
            if (StatManager.getInstance().hasJoinedBefore(uuid))
                return null;
            return uuid;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public boolean isOnline() {
        return false;
    }

    @Override
    public String getName(UUID uuid) throws JsonSyntaxException, IOException {
        try (InputStream is = new URL("https://mcapi.ca/player/profile/" + uuid).openStream()) {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            JsonObject rootobj = new Gson().fromJson(rd, JsonObject.class);

            return rootobj.get("name").getAsString();
        } catch (IOException e) {
            e.printStackTrace();
            return "User Not Found";
        }
    }
}
