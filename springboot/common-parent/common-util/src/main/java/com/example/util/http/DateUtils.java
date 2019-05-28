package com.example.util.http;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {


    @AllArgsConstructor
    public enum FormatType{
        SIMPLE("yyyy-MM-dd"),
        COMMON("yyyy-MM-dd HH:mm:ss");
        @Setter@Getter
        private String type;
    }

    /**
     * str转为date
     * @param dateStr
     * @param formatType
     * @return
     */
    public static Date parse(String dateStr, FormatType formatType) {
        if (formatType == null) {
            formatType = FormatType.SIMPLE;
        }
        try {
            return new SimpleDateFormat(formatType.getType()).parse(dateStr);
        } catch (ParseException e) {
            return null;
        }
    }
    /**
     * 日期转为str
     * @param date
     * @param formatType
     * @return
     */
    public static String format(Date date, FormatType formatType) {
        if (formatType == null) {
            formatType = FormatType.SIMPLE;
        }
        return new SimpleDateFormat(formatType.getType()).format(date);
    }
    /**
     * 两个时间对减 date1-date2
     * @param date1
     * @param date2
     * @return
     */
    public static long compare(Date date1, Date date2) {
        return date1.getTime() - date2.getTime();
    }
    /**
     * 往后推移几天
     * @param date
     * @param days  负数向前移动 正数向后移动
     * @return
     */
    public static Date addDays(Date date, int days) {
        if (date == null) {
            date = new Date();
        }
        return new Date(date.getTime() + days * 24 * 60 * 60 * 1000);
    }
}
