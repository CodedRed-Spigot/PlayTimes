package me.codedred.playtimes.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

import org.bukkit.entity.Player;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class StatFetcher {

	public static String getName(UUID uuid) throws MalformedURLException, IOException {
		try (InputStream is = new URL("https://mcapi.ca/player/profile/" + uuid).openStream()) {
			BufferedReader rd = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
			JsonParser jp = new JsonParser();
			JsonElement root = jp.parse(rd);
			JsonObject rootobj = root.getAsJsonObject();
			String name = rootobj.get("name").getAsString();

			return name;
		} catch (IOException e) {
			e.printStackTrace();
			return "error";
		}
	}
	
	public static UUID getBase(Player player) {
		return player.getUniqueId();
	}
	
	public static UUID getUUID(String name) throws MalformedURLException, IOException {
		InputStream is = new URL("https://api.mojang.com/users/profiles/minecraft/" + name).openStream();
		try {
			BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
			JsonParser jp = new JsonParser();
			JsonElement root = jp.parse(rd);
			JsonObject rootobj = root.getAsJsonObject();
			String u = rootobj.get("id").getAsString();
			String uuid = "";
			for(int i = 0; i <= 31; i++) {
				uuid = uuid + u.charAt(i);
				if(i == 7 || i == 11 || i == 15 || i == 19) {
					uuid = uuid + "-";
				}
			}
			return UUID.fromString(uuid);
		} finally {
			is.close();
		}
	}
}
