package space.mufeng.utils;

public class DateUtil {

    /**
     * getTodayStartTime
     */
    public static long getTodayStartTime() {
        return System.currentTimeMillis() / 1000 / 86400 * 86400;
    }

    public static long getTodayEndTime() {
        return getTodayStartTime() + 86400;
    }

    /**
     * yyyy-MM-dd HH:mm:ss
     */
    public static String format(long time) {
        return new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date(time * 1000));
    }

    public static String formatDate(long time) {
        return new java.text.SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date(time * 1000));
    }
}
