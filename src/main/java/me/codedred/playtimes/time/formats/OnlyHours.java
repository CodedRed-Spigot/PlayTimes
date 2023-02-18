package me.codedred.playtimes.time.formats;

import me.codedred.playtimes.time.Timings;

public class OnlyHours implements Timings {

    @Override
    public String buildFormat(long time) {
        return ((double)Math.round((time/3600.0)*100d))/100d + HOURS;
    }

}
