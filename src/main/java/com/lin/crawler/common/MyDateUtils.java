package com.lin.crawler.common;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

public class MyDateUtils {

    public static String getGMTDateStr() {
        Calendar cd = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("EEE d MMM yyyy HH:mm:ss 'GMT'", Locale.US);
        // 设置时区为GMT
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        String str = sdf.format(cd.getTime());
        str = str.replaceFirst(" ", ",");
        return str;
    }
}