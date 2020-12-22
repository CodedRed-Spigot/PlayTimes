package me.codedred.playtimes.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.UUID;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
public class NameFetcher  {

	// https://mcapi.ca/player/profile/443b14fc-b3bc-4e80-8c76-e465cc8cdb1c  <-- returns CodedRed
	//https://mcapi.ca/
	
	public static String getName(UUID uuid) throws MalformedURLException, IOException {
		InputStream is = new URL("https://mcapi.ca/player/profile/" + uuid).openStream();
		try {
			BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
			JsonParser jp = new JsonParser();
			JsonElement root = jp.parse(rd);
			JsonObject rootobj = root.getAsJsonObject();
			String name = rootobj.get("name").getAsString();

			return name;
		} finally {
			is.close();
		}
	}
}