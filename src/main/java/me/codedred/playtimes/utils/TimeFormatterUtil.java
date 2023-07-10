package me.codedred.playtimes.utils;
import me.codedred.playtimes.data.DataManager;
import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class TimeFormatterUtil {

    private static final DataManager dataManager = DataManager.getInstance();

    private TimeFormatterUtil() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Converts the given time in seconds to a formatted date string
     *
     * @param timeInSeconds the time in seconds to be converted
     * @return the formatted date string
     */
    public static String secondsToFormattedTime(long timeInSeconds) {
        Duration duration = Duration.ofSeconds(timeInSeconds);
        long hours = duration.toHoursPart();
        long minutes = duration.toMinutesPart();
        long seconds = duration.toSecondsPart();

        LocalTime time = LocalTime.of((int) hours, (int) minutes, (int) seconds);

        return time.format(DateTimeFormatter.ofPattern(dataManager.getConfig().getString("playtime.format")));
    }
}
