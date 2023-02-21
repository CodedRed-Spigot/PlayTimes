package me.codedred.playtimes;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.codedred.playtimes.models.Leaderboard;
import me.codedred.playtimes.statistics.StatManager;
import me.codedred.playtimes.statistics.StatisticType;
import me.codedred.playtimes.utils.TimeFormatterUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.util.*;

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
    public @NotNull String getAuthor(){
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
    public @NotNull String getIdentifier(){
        return "PlayTimes";
    }

    /**
     * This is the version of this expansion.
     * <br>You don't have to use numbers, since it is set as a String.
     *
     * @return The version as a String.
     */
    @Override
    public @NotNull String getVersion(){
        return "1.4.6";
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
     *         A {@link OfflinePlayer OfflinePlayer}.
     * @param  identifier
     *         A String containing the identifier/value.
     *
     * @return Possibly-null String of the requested identifier.
     */
    @Override
	public String onRequest(OfflinePlayer player, @NotNull String identifier) {
		identifier = identifier.toLowerCase();
		StatManager stats = StatManager.getInstance();
		switch (identifier) {
			case "playtime" -> {
				return TimeFormatterUtil.secondsToFormattedTime(stats.getPlayerStat(player.getUniqueId(), StatisticType.PLAYTIME) / 20);
			}
			case "uptime" -> {
				return stats.getUptime();
			}
			case "joindate" -> {
				return stats.getJoinDate(player.getUniqueId());
			}
		}

		// %PlayTimes_topname#%
		if (identifier.contains("topname")) {
			Leaderboard board = new Leaderboard();
			List<UUID> players = new ArrayList<>();
			for (Map.Entry<String, Integer> entry : board.getTopTen().entrySet())
				players.add(UUID.fromString(entry.getKey()));
			String num = identifier.substring(7);
			int val;

			if (isInteger(num))
				val = (Integer.parseInt(num) < 1) ? 0 : Integer.parseInt(num) - 1;
			else
				return ChatColor.RED + "Invalid leaderboard position!";

			if (players.size() < val + 1)
				return "N/A";

			OfflinePlayer targetPlayer = Bukkit.getOfflinePlayer(players.get(val));
			String name = targetPlayer.getName();


			return (players.isEmpty() ? "N/A" : name);
		}

		// %PlayTimes_toptime#%
		else if (identifier.contains("toptime")) {
			Leaderboard board = new Leaderboard();
			List<Integer> players = new ArrayList<>();
			for (Map.Entry<String, Integer> entry : board.getTopTen().entrySet())
				players.add(entry.getValue());

			String num = identifier.substring(7);
			int val;
			if (isInteger(num))
				val = (Integer.parseInt(num) < 1) ? 0 : Integer.parseInt(num) - 1;
			else
				return ChatColor.RED + "Invalid leaderboard position!";
			if (players.size() < val + 1)
				return "N/A";

			return (players.isEmpty() ? "N/A" : TimeFormatterUtil.secondsToFormattedTime(players.get(val)/20));
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




















