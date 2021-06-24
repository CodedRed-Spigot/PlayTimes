package me.codedred.playtimes.server;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class ServerOnline implements ServerStatus {

    @Override
    public UUID getUUID(String name) {
        try {
            InputStream is = new URL("https://api.mojang.com/users/profiles/minecraft/" + name).openStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            JsonObject rootobj = new Gson().fromJson(rd, JsonObject.class);
            String u = rootobj.get("id").getAsString();
            StringBuilder uuid = new StringBuilder();
            for (int i = 0; i <= 31; i++) {
                uuid.append(u.charAt(i));
                if (i == 7 || i == 11 || i == 15 || i == 19) {
                    uuid.append("-");
                }
            }
            is.close();
            return UUID.fromString(uuid.toString());
        } catch (JsonSyntaxException | IOException e) {
            return null;
        }
    }

    @Override
    public boolean isOnline() {
        return true;
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
