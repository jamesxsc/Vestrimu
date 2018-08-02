package com.georlegacy.general.vestrimu.util.time;

public class TimeFormatter {

    public static String millisToTime(long millis) {
        long days = millis / 1000 / 60 / 60 / 24;
        long hours = (millis / 1000 / 60 / 60) % 24;
        long minutes = (millis / 1000 / 60) % 60;
        long seconds = (millis / 1000) % 60;

        StringBuilder builder = new StringBuilder();
        if (days > 0)
            builder.append(days).append(" day(s)");
        if (hours > 0)
            builder.append(", ").append(hours).append(" hour(s)");
        if (minutes > 0)
            builder.append(", ").append(minutes).append(" minute(s)");
        if (seconds > 0)
            builder.append(", ").append(seconds).append(" second(s)");

        return builder.toString();
    }

}
