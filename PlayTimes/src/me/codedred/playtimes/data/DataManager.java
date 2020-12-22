package me.codedred.playtimes.data;

import me.codedred.playtimes.PlayTimes;
import me.codedred.playtimes.data.files.Config;
import me.codedred.playtimes.data.files.Data;

public class DataManager {
	
	private static DataManager instance = null;
	
	public static DataManager getInstance() {
		if (instance == null)
			instance = new DataManager();
		return instance;
	}
	
	public Data data = new Data(PlayTimes.getPlugin(PlayTimes.class));
	public Config cfg = new Config(PlayTimes.getPlugin(PlayTimes.class));;
	
}
