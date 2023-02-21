package me.codedred.playtimes.utils;

import me.codedred.playtimes.data.DataManager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TimeFormatterUtil {


    private TimeFormatterUtil() {
        throw new IllegalStateException("Utility class");
    }

    private static final String FORMAT = DataManager.getInstance().getConfig().getString("playtime.format");
    private static final Locale LOCALE = Locale.forLanguageTag(DataManager.getInstance().getConfig().getString("playtime.locale"));


    /**
     * Converts the given time in seconds to a formatted date string based on the provided date format, locale,
     * and timezone.
     *
     * @param timeInSeconds the time in seconds to be converted
     * @return the formatted date string
     */
    public static String secondsToFormattedTime(long timeInSeconds) {
        SimpleDateFormat sdf = new SimpleDateFormat(FORMAT, LOCALE);
        Date result = new Date(timeInSeconds * 1000);
        return sdf.format(result);
    }
}
