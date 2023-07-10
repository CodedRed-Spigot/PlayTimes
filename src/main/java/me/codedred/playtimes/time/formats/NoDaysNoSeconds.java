package me.codedred.playtimes.time.formats;

import me.codedred.playtimes.time.TimeConstants;
import me.codedred.playtimes.time.Timings;

public class NoDaysNoSeconds implements Timings {

    @Override
    public String buildFormat(long time) {
        if (time < 60)
            return time + ( time == 1 ? TimeConstants.getSeconds() : TimeConstants.getSecond());

        int minutes = (int) (time / 60);

        // Time less than 1 hour
        if (minutes < 60)
            return minutes + (minutes == 1 ? TimeConstants.getMinute() : TimeConstants.getMinutes());

        String format;

        int hours = minutes / 60;
        format = hours + (hours == 1 ? TimeConstants.getHour() : TimeConstants.getHours());
        int inMins = 60 * hours;
        int left = minutes - inMins;

        if (left >= 1)
            format = format + left + (left == 1 ? TimeConstants.getMinute() : TimeConstants.getMinutes());

        return format;

    }

}
