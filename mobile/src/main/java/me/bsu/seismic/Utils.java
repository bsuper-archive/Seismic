package me.bsu.seismic;

import org.joda.time.Duration;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.SimpleTimeZone;
import java.util.TimeZone;


public class Utils {

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    public static final String TWITTER_KEY = "83YeNmqDghxUVFSYZjVCXCAbV";
    public static final String TWITTER_SECRET = "73mzbYXZ1BhjmcpGuUhsxSFu0WYzYSt429HWDHhLkebWqj3WbF";

    public static String convertUnixTimestampToLocalTimestampString(long unixTS) {
        Date date = new Date(unixTS);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
        sdf.setTimeZone(getUserTimezone());
        return sdf.format(date);
    }

    public static String getTimeDifferenceFromCurrentTime(long unixTS) {
        Duration duration = new Duration(System.currentTimeMillis() - unixTS);
        PeriodFormatter formatter = new PeriodFormatterBuilder()
                .appendDays()
                .appendSuffix(" days ")
                .appendHours()
                .appendSuffix(" hours ")
                .appendMinutes()
                .appendSuffix(" min ago")
                .toFormatter();
        return formatter.print(duration.toPeriod());
    }

    public static TimeZone getUserTimezone() {
        return SimpleTimeZone.getDefault();
    }

}
