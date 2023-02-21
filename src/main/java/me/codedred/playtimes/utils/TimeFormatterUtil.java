package me.codedred.playtimes.utils;

import me.codedred.playtimes.data.DataManager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class TimeFormatterUtil {

    private static final DataManager dataManager = DataManager.getInstance();
    private static final String FORMAT = dataManager.getConfig().getString("playtime.format");
    private static final Locale LOCALE = Locale.forLanguageTag(dataManager.getConfig().getString("playtime.locale"));
    private static final TimeZone TIMEZONE = TimeZone.getTimeZone(dataManager.getConfig().getString("playtime.timezone"));
    private static final long MILLISECONDS_PER_SECOND = 1000;

    private static final SimpleDateFormat SDF = new SimpleDateFormat(FORMAT, LOCALE);

    private TimeFormatterUtil() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Converts the given time in seconds to a formatted date string based on the provided date format, locale,
     * and timezone.
     *
     * @param timeInSeconds the time in seconds to be converted
     * @return the formatted date string
     */
    public static synchronized String secondsToFormattedTime(long timeInSeconds) {
        SDF.setTimeZone(TIMEZONE);
        Date result = new Date(timeInSeconds * MILLISECONDS_PER_SECOND);
        return SDF.format(result);
    }
}
