package me.codedred.playtimes.time.formats;

import me.codedred.playtimes.time.Timings;

public class NoDaysNoSeconds implements Timings {

    @Override
    public String buildFormat(long time) {
        if (time < 60)
            return time + ( time == 1 ? SECONDS : SECOND);

        int minutes = (int) (time / 60);

        // Time less than 1 hour
        if (minutes < 60)
            return String.valueOf(minutes + (minutes == 1 ? MINUTE : MINUTES));

        String format;

        int hours = minutes / 60;
        format = hours + (hours == 1 ? HOUR : HOURS);
        int inMins = 60 * hours;
        int left = minutes - inMins;

        if (left >= 1)
            format = format + " " + left + (left == 1 ? MINUTE : MINUTES);

        return format;

    }

}
