package me.codedred.playtimes.time.formats;

import me.codedred.playtimes.data.DataManager;
import me.codedred.playtimes.time.Timings;

public class NativeFormat implements Timings {

    @Override
    public String buildFormat(long time) {
        if (time < 60) 
            return time + ( time == 1 ? SECONDS : SECOND);

        int minutes = (int) (time / 60);
        int seconds = 60 * minutes;
        int secondsLeft = (int) (time - seconds);

        // Time less than 1 hour
        if (minutes < 60) {
            if (secondsLeft > 0)
                return String.valueOf(minutes + (minutes == 1 ? MINUTE : MINUTES) + " " + secondsLeft + (secondsLeft == 1 ? SECOND : SECONDS));
            return String.valueOf(minutes + (minutes == 1 ? MINUTE : MINUTES));
        }

        String format;

        // Time less than 1 Day
        if (minutes < 1440) {
            int hours = minutes / 60;
            format = hours + (hours == 1 ? HOUR : HOURS);
            int inMins = 60 * hours;
            int left = minutes - inMins;

            if (left >= 1)
                format = format + " " + left + (left == 1 ? MINUTE : MINUTES);

            if (secondsLeft > 0)
                format = format + " " + secondsLeft + (secondsLeft == 1 ? SECOND : SECONDS);

            return format;
        }

        // Time greater than 1 day
        int days = minutes / 1440;
        format = days + (days == 1 ? DAY : DAYS);
        int inMins = 1440 * days;
        int leftOver = minutes - inMins;
        if (leftOver >= 1) {
            if (leftOver < 60) {
                format = format + " " + leftOver + (leftOver == 1 ? MINUTE : MINUTES);
            }
            else {
                int hours = leftOver / 60;
                format = format + " " + hours + (hours == 1 ? HOUR : HOURS);
                int hoursInMins = 60 * hours;
                int minsLeft = leftOver - hoursInMins;
                if (leftOver >= 1) {
                    format = format + " " + minsLeft + (minsLeft == 1 ? MINUTE : MINUTES);
                }
            }
        }
        if (secondsLeft > 0)
            format = format + " " + secondsLeft + (secondsLeft == 1 ? SECOND : SECONDS);
        return format;
    }

}
