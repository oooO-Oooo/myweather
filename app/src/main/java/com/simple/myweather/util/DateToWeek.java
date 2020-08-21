package com.simple.myweather.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DateToWeek {

    // 传入String类型日期，得到星期，日期格式：yy-MM-dd
    public static String dayForWeek(String date) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("yy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(format.parse(date));
        int dayForWeek;
        if (calendar.get(Calendar.DAY_OF_WEEK) == 1) {
            dayForWeek = 7;
        } else {
            dayForWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        }

        String[] weekDay = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
        if (dayForWeek == 7) {
            dayForWeek = 0;
        }
        return weekDay[dayForWeek];
    }
}
