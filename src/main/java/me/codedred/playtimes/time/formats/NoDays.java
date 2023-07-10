package me.codedred.playtimes.time.formats;

import me.codedred.playtimes.time.TimeConstants;
import me.codedred.playtimes.time.Timings;

public class NoDays implements Timings {

    @Override
    public String buildFormat(long time) {
        if (time < 60)
            return formatSeconds(time);

        int minutes = (int) (time / 60);
        int secondsLeft = (int) (time % 60);

        // Time less than 1 hour
        if (minutes < 60)
            return formatMinutes(minutes, secondsLeft);

        // Greater than 1 hour, only in hours
        int hours = minutes / 60;
        int minutesLeft = minutes % 60;

        return formatHours(hours, minutesLeft, secondsLeft);
    }

    private String formatSeconds(long seconds) {
        return seconds + (seconds == 1 ? TimeConstants.getSeconds() : TimeConstants.getSecond());
    }

    private String formatMinutes(int minutes, int secondsLeft) {
        String format = minutes + (minutes == 1 ? TimeConstants.getMinute() : TimeConstants.getMinutes());
        if (secondsLeft > 0)
            format += formatSeconds(secondsLeft);
        return format;
    }

    private String formatHours(int hours, int minutesLeft, int secondsLeft) {
        String format = hours + (hours == 1 ? TimeConstants.getHour() : TimeConstants.getHours());
        if (minutesLeft >= 1)
            format += formatMinutes(minutesLeft, secondsLeft);
        else if (secondsLeft > 0)
            format += formatSeconds(secondsLeft);
        return format;
    }
}
