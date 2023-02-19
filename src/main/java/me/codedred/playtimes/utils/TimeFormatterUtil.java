package me.codedred.playtimes.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class TimeFormatterUtil {


    private TimeFormatterUtil() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Converts the given time in seconds to a formatted date string based on the provided date format, locale,
     * and timezone.
     *
     * @param timeInSeconds the time in seconds to be converted
     * @param format        the date format to use for the output
     * @param locale        the locale to use for formatting the date
     * @param timezone      the timezone to use for formatting the date
     * @return the formatted date string
     */
    public static String secondsToFormattedTime(long timeInSeconds, String format, Locale locale, String timezone) {
        SimpleDateFormat sdf = new SimpleDateFormat(format, locale);
        sdf.setTimeZone(TimeZone.getTimeZone(timezone));
        Date result = new Date(timeInSeconds * 1000);
        return sdf.format(result);
    }
}
