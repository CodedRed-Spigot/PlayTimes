package me.codedred.playtimes;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.bukkit.OfflinePlayer;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.codedred.playtimes.api.TimelessServer;
import me.codedred.playtimes.models.Clock;
import me.codedred.playtimes.utils.FirstJoinDate;
import me.codedred.playtimes.utils.NameFetcher;
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
    public String onRequest(OfflinePlayer player, String identifier){
    	Clock clock = new Clock();
        // %PlayTimes_playtime%
        if(identifier.equalsIgnoreCase("playtime")){
        	return clock.getTime((Statistics.getPlayerStatistic(player.getUniqueId(), "PLAYTIME")/20));
        }

        // %PlayTimes_uptime%
        else if(identifier.equalsIgnoreCase("uptime")){
        	return clock.getUptime();
        }
        
        // %PlayTimes_joindate%
        else if(identifier.equalsIgnoreCase("joindate")){
        	return FirstJoinDate.getOfflineJoinDate(player.getUniqueId(), plugin.getConfig().getString("date-format"));
        }
        
        // %PlayTimes_just--%
        else if (identifier.equalsIgnoreCase("justhours")) {
        	return clock.getHours((Statistics.getPlayerStatistic(player.getUniqueId(), "PLAYTIME")/20)) + "";
        }
        else if (identifier.equalsIgnoreCase("justdays")) {
        	return clock.getDays((Statistics.getPlayerStatistic(player.getUniqueId(), "PLAYTIME")/20)) + "";
        }
        else if (identifier.equalsIgnoreCase("justmins")) {
        	return clock.getMins((Statistics.getPlayerStatistic(player.getUniqueId(), "PLAYTIME")/20)) + "";
        }
        else if (identifier.equalsIgnoreCase("justsecs")) {
        	return clock.getSecs((Statistics.getPlayerStatistic(player.getUniqueId(), "PLAYTIME")/20)) + "";
        }
        
        // %PlayTimes_topname#%
        else if (identifier.contains("topname")) {
        	TimelessServer server = new TimelessServer();
        	List<UUID> players = server.getTop10Players();
   		 
			try {
				if (identifier.equalsIgnoreCase("topname1"))
					return (players.isEmpty() ? "N/A" : NameFetcher.getName(players.get(0)));
				else if (identifier.equalsIgnoreCase("topname2"))
					return (players.size() < 2 ? "N/A" : NameFetcher.getName(players.get(1)));
				else if (identifier.equalsIgnoreCase("topname3"))
					return (players.size() < 3 ? "N/A" : NameFetcher.getName(players.get(2)));
				else if (identifier.equalsIgnoreCase("topname4"))
					return (players.size() < 4 ? "N/A" : NameFetcher.getName(players.get(3)));
				else if (identifier.equalsIgnoreCase("topname5"))
					return (players.size() < 5 ? "N/A" : NameFetcher.getName(players.get(4)));
				else if (identifier.equalsIgnoreCase("topname6"))
					return (players.size() < 6 ? "N/A" : NameFetcher.getName(players.get(5)));
				else if (identifier.equalsIgnoreCase("topname7"))
					return (players.size() < 7 ? "N/A" : NameFetcher.getName(players.get(6)));
				else if (identifier.equalsIgnoreCase("topname8"))
					return (players.size() < 8 ? "N/A" : NameFetcher.getName(players.get(7)));
				else if (identifier.equalsIgnoreCase("topname9"))
					return (players.size() < 9 ? "N/A" : NameFetcher.getName(players.get(8)));
				else if (identifier.equalsIgnoreCase("topname10"))
					return (players.size() < 10 ? "N/A" : NameFetcher.getName(players.get(9)));
				
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
        
        // %PlayTimes_toptime#%
        else if (identifier.contains("toptime")) {
        	TimelessServer server = new TimelessServer();
        	List<Integer> players = server.getTop10Times();
   		 
			if (identifier.equalsIgnoreCase("toptime1"))
				return (players.isEmpty() ? "N/A" : clock.getTime(players.get(0)));
			else if (identifier.equalsIgnoreCase("toptime2"))
				return (players.size() < 2 ? "N/A" : clock.getTime(players.get(1)));
			else if (identifier.equalsIgnoreCase("toptime3"))
				return (players.size() < 3 ? "N/A" : clock.getTime(players.get(2)));
			else if (identifier.equalsIgnoreCase("toptime4"))
				return (players.size() < 4 ? "N/A" : clock.getTime(players.get(3)));
			else if (identifier.equalsIgnoreCase("toptime5"))
				return (players.size() < 5 ? "N/A" : clock.getTime(players.get(4)));
			else if (identifier.equalsIgnoreCase("toptime6"))
				return (players.size() < 6 ? "N/A" : clock.getTime(players.get(5)));
			else if (identifier.equalsIgnoreCase("toptime7"))
				return (players.size() < 7 ? "N/A" : clock.getTime(players.get(6)));
			else if (identifier.equalsIgnoreCase("toptime8"))
				return (players.size() < 8 ? "N/A" : clock.getTime(players.get(7)));
			else if (identifier.equalsIgnoreCase("toptime9"))
				return (players.size() < 9 ? "N/A" : clock.getTime(players.get(8)));
			else if (identifier.equalsIgnoreCase("toptime10"))
				return (players.size() < 10 ? "N/A" : clock.getTime(players.get(9)));
        }
        
        

        // We return null if an invalid placeholder (f.e. %example_placeholder3%) 
        // was provided
        return null;
    }
}





















