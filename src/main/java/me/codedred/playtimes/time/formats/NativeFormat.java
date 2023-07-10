package me.codedred.playtimes.time.formats;

import me.codedred.playtimes.time.TimeConstants;
import me.codedred.playtimes.time.Timings;

public class NativeFormat implements Timings {

    @Override
    public String buildFormat(long time) {
        if (time < 60)
            return formatSeconds(time);

        int minutes = (int) (time / 60);
        int secondsLeft = (int) (time % 60);

        // Time less than 1 hour
        if (minutes < 60)
            return formatMinutes(minutes, secondsLeft);

        // Time less than 1 Day
        if (minutes < 1440) {
            int hours = minutes / 60;
            int minutesLeft = minutes % 60;
            return formatHours(hours, minutesLeft, secondsLeft);
        }

        // Time greater than 1 day
        int days = minutes / 1440;
        int minutesLeft = minutes % 1440;
        return formatDays(days, minutesLeft, secondsLeft);
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

    private String formatDays(int days, int minutesLeft, int secondsLeft) {
        String format = days + (days == 1 ? TimeConstants.getDay() : TimeConstants.getDays());
        if (minutesLeft >= 60) {
            int hours = minutesLeft / 60;
            int minutesAfterHours = minutesLeft % 60;
            format += formatHours(hours, minutesAfterHours, secondsLeft);
        } else if (minutesLeft >= 1) {
            format += formatMinutes(minutesLeft, secondsLeft);
        } else if (secondsLeft > 0) {
            format += formatSeconds(secondsLeft);
        }
        return format;
    }
}
