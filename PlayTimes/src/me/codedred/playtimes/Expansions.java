package me.codedred.playtimes;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.codedred.playtimes.api.TimelessServer;
import me.codedred.playtimes.models.Clock;
import me.codedred.playtimes.utils.FirstJoinDate;
import me.codedred.playtimes.utils.StatFetcher;
import me.codedred.playtimes.utils.Statistics;

/**
 * This class will automatically register as a placeholder expansion 
 * when a jar including this class is added to the directory 
 * {@code /plugins/PlaceholderAPI/expansions} on your server.
 * <br>
 * <br>If you create such a class inside your own plugin, you have to
 * register it manually in your plugins {@code onEnable()} by using 
 * {@code new YourExpansionClass().register();}
 */
public class Expansions extends PlaceholderExpansion {

	private PlayTimes plugin;
	public Expansions(PlayTimes plugin) {
		this.plugin = plugin;
	}
    /**
     * This method should always return true unless we
     * have a dependency we need to make sure is on the server
     * for our placeholders to work!
     *
     * @return always true since we do not have any dependencies.
     */
    @Override
    public boolean canRegister(){
        return true;
    }

    /**
     * The name of the person who created this expansion should go here.
     * 
     * @return The name of the author as a String.
     */
    @Override
    public String getAuthor(){
        return "Cmaaxx"; // CodedRed
    }

    /**
     * The placeholder identifier should go here.
     * <br>This is what tells PlaceholderAPI to call our onRequest 
     * method to obtain a value if a placeholder starts with our 
     * identifier.
     * <br>This must be unique and can not contain % or _
     *
     * @return The identifier in {@code %<identifier>_<value>%} as String.
     */
    @Override
    public String getIdentifier(){
        return "PlayTimes";
    }

    /**
     * This is the version of this expansion.
     * <br>You don't have to use numbers, since it is set as a String.
     *
     * @return The version as a String.
     */
    @Override
    public String getVersion(){
        return "R5";
    }
  
    @Override
    public boolean persist(){
        return true;
    }
    /**
     * This is the method called when a placeholder with our identifier 
     * is found and needs a value.
     * <br>We specify the value identifier in this method.
     * <br>Since version 2.9.1 can you use OfflinePlayers in your requests.
     *
     * @param  player
     *         A {@link org.bukkit.OfflinePlayer OfflinePlayer}.
     * @param  identifier
     *         A String containing the identifier/value.
     *
     * @return Possibly-null String of the requested identifier.
     */
    @Override
	public String onRequest(OfflinePlayer player, String identifier) {
		identifier = identifier.toLowerCase();
		Clock clock = new Clock();
		switch (identifier) {
			case "playtime":
				return clock.getTime((Statistics.getPlayerStatistic(player.getUniqueId(), "PLAYTIME") / 20));
			case "uptime":
				return clock.getUptime();
			case "joindate":
				return FirstJoinDate.getOfflineJoinDate(player.getUniqueId(), plugin.getConfig().getString("date-format"));
			case "justhours":
				return clock.getHours((Statistics.getPlayerStatistic(player.getUniqueId(), "PLAYTIME") / 20)) + "";
			case "justdays":
				return clock.getDays((Statistics.getPlayerStatistic(player.getUniqueId(), "PLAYTIME") / 20)) + "";
			case "justmins":
				return clock.getMins((Statistics.getPlayerStatistic(player.getUniqueId(), "PLAYTIME") / 20)) + "";
			case "justsecs":
				return clock.getSecs((Statistics.getPlayerStatistic(player.getUniqueId(), "PLAYTIME") / 20)) + "";
		}

		// %PlayTimes_topname#%
		if (identifier.contains("topname")) {
			TimelessServer server = new TimelessServer();
			List<UUID> players = server.getTop10Players();
			String num = identifier.substring(7);
			int val;

			if (isInteger(num))
				val = (Integer.parseInt(num) < 1) ? 0 : Integer.parseInt(num) - 1;
			else
				return ChatColor.RED + "Invalid leaderboard position!";

			if (players.size() < val + 1)
				return "N/A";

			OfflinePlayer targetPlayer = Bukkit.getOfflinePlayer(players.get(val));
			String name = "";
			try {
				name = (targetPlayer.getName() == null) ? StatFetcher.getName(targetPlayer.getUniqueId()) : targetPlayer.getName();
			} catch (IOException e) {
				//e.printStackTrace();
			}

			return (players.isEmpty() ? "N/A" : name);
		}

		// %PlayTimes_toptime#%
		else if (identifier.contains("toptime")) {
			TimelessServer server = new TimelessServer();
			List<Integer> players = server.getTop10Times();

			String num = identifier.substring(7);
			int val;
			if (isInteger(num))
				val = (Integer.parseInt(num) < 1) ? 0 : Integer.parseInt(num) - 1;
			else
				return ChatColor.RED + "Invalid leaderboard position!";
			if (players.size() < val + 1)
				return "N/A";

			return (players.isEmpty() ? "N/A" : clock.getTime(players.get(val)));
		}


		// We return null if an invalid placeholder (f.e. %example_placeholder3%)
		// was provided
		return null;
	}

	boolean isInteger(String str) {
		try {
			Integer.parseInt(str);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

}




















