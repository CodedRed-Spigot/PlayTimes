package me.codedred.playtimes.server;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import me.codedred.playtimes.server.ServerManager.LookUpTypes;
import me.codedred.playtimes.statistics.StatManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class ServerOffline implements ServerStatus {

  @Override
  public UUID getUUID(String name) {
    UUID offlineUUID = null;
    Player onlinePlayer = Bukkit.getServer().getPlayer(name);
    if (onlinePlayer != null) {
      offlineUUID = onlinePlayer.getUniqueId();
    }

    if (offlineUUID == null) {
      OfflinePlayer offlinePlayers[] = Bukkit.getServer().getOfflinePlayers();
      int playerIndex = 0;
      while ((playerIndex < offlinePlayers.length) && (offlineUUID == null)) {
        if (offlinePlayers[playerIndex].getName().equalsIgnoreCase(name)) {
          offlineUUID = offlinePlayers[playerIndex].getUniqueId();
        }
        playerIndex = playerIndex + 1;
      }
    }
    if (offlineUUID != null) {
      return offlineUUID;
    } else {
      try (
        InputStream is = new URL(
          "https://api.mojang.com/users/profiles/minecraft/" + name
        )
          .openStream()
      ) {
        BufferedReader rd = new BufferedReader(
          new InputStreamReader(is, StandardCharsets.UTF_8)
        );
        JsonObject rootobj = new Gson().fromJson(rd, JsonObject.class);
        UUID uuid;

        if (ServerManager.getInstance().lookupType() == LookUpTypes.OFFLINE) {
          String fixedName = rootobj.get("name").getAsString();
          uuid =
            UUID.nameUUIDFromBytes(
              ("OfflinePlayer:" + fixedName).getBytes(StandardCharsets.UTF_8)
            );
        } else {
          String id = rootobj.get("id").getAsString();
          String uuidWithDashes = id.replaceAll(
            "(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})",
            "$1-$2-$3-$4-$5"
          );

          uuid = UUID.fromString(uuidWithDashes);
        }

        if (!StatManager.getInstance().hasJoinedBefore(uuid)) {
          return null;
        }
        return uuid;
      } catch (IOException e) {
        return null;
      }
    }
  }

  @Override
  public boolean isOnline() {
    return false;
  }

  @Override
  public String getName(UUID uuid) throws IOException {
    try (
      InputStream is = new URL("https://mcapi.ca/player/profile/" + uuid)
        .openStream();
      BufferedReader rd = new BufferedReader(
        new InputStreamReader(is, StandardCharsets.UTF_8)
      )
    ) {
      JsonObject rootobj = new Gson().fromJson(rd, JsonObject.class);
      return rootobj.get("name").getAsString();
    } catch (IOException e) {
      e.printStackTrace();
      return "User Not Found";
    }
  }
}
