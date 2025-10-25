package com.credit_suisse.app.util;

public class TimeFormatter {
    
    public static String formatTime(long milliseconds) {
        if (milliseconds < 1000) {
            return milliseconds + " ms";
        } else if (milliseconds < 60000) {
            double seconds = milliseconds / 1000.0;
            return String.format("%.2f seconds", seconds);
        } else {
            long minutes = milliseconds / 60000;
            long remainingMs = milliseconds % 60000;
            double seconds = remainingMs / 1000.0;
            return String.format("%d min %.2f sec", minutes, seconds);
        }
    }
    
    public static String formatComparison(long time1, long time2, String name1, String name2) {
        if (time1 == time2) {
            return "Both strategies performed equally";
        }
        
        long diff = Math.abs(time1 - time2);
        String faster = time1 < time2 ? name1 : name2;
        double percentage = ((double) diff / Math.min(time1, time2)) * 100;
        
        return String.format("%s is %s faster (%.1f%% improvement)", 
                faster, formatTime(diff), percentage);
    }
}