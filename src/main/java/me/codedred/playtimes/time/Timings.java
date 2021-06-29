package me.codedred.playtimes.time;

public interface Timings extends TimeRegistry {

    String buildFormat(long time);
    /*public static int getDays(long l) {
        if (l < 60) {
            return 0;
        }
        int minutes = (int) (l / 60);
        if (minutes < 1440)
            return 0;
        return minutes / 1440;
    }

    public static int getHours(long l) {
        if (l < 60) {
            return 0;
        }
        int minutes = (int) (l / 60);
        if (minutes < 60)
            return 0;
        return minutes / 60;
    }

    public static int getMins(long l) {
        if (l < 60) {
            return 0;
        }
        return (int) (l / 60);
    }

    public static int getSecs(long l) {
        if (l <= 0) {
            return 0;
        }
        return (int) (l);
    }*/

}
