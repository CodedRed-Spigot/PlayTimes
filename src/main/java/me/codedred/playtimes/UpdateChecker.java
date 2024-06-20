package me.codedred.playtimes;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Objects;
import javax.net.ssl.HttpsURLConnection;
import org.bukkit.plugin.java.JavaPlugin;

public class UpdateChecker {

  private static final String UPDATE_URL =
    "https://api.spigotmc.org/legacy/update.php?resource=%d";

  private final JavaPlugin plugin;
  private final int projectId;

  public UpdateChecker(JavaPlugin plugin, int projectId) {
    this.plugin = Objects.requireNonNull(plugin, "Plugin cannot be null.");
    this.projectId = projectId;
  }

  public boolean hasUpdatesAvailable() throws IOException {
    String latestVersion;

    URL url = new URL(String.format(UPDATE_URL, projectId));
    HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
    connection.setRequestMethod("GET");
    connection.setConnectTimeout(5000);
    connection.setReadTimeout(5000);
    connection.setUseCaches(true);

    try (
      BufferedReader reader = new BufferedReader(
        new InputStreamReader(connection.getInputStream())
      )
    ) {
      latestVersion = reader.readLine();
    }

    if (latestVersion == null || latestVersion.isEmpty()) {
      throw new IOException("Failed to retrieve latest version.");
    }

    return !plugin.getDescription().getVersion().equals(latestVersion);
  }
}
