package com.mmall.util;

import com.github.pagehelper.util.StringUtil;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Date;

/**
 * @author zhangruiyan
 */
public class DateTimeUtil {
    /**
     * joda->time
     * str->Date
     * Date->str
     */
    public static final String STANDARD_FORMAT="yyyy-MM-dd HH:mm:ss";

    /**
     * 字符串转时间
     * @param dateTimeStr
     * @param formatStr
     * @return
     */
    public static Date strToDate(String dateTimeStr,String formatStr){
        DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern(formatStr);
        DateTime dateTime = dateTimeFormatter.parseDateTime(dateTimeStr);
        return dateTime.toDate();
    }

    public static Date strToDate(String dateTimeStr){
        DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern(STANDARD_FORMAT);
        DateTime dateTime = dateTimeFormatter.parseDateTime(dateTimeStr);
        return dateTime.toDate();
    }

    /**
     * 时间转字符串
     * @param date
     * @param formatStr
     * @return
     */
    public static String dateToStr(Date date,String formatStr){
        if (date==null){
            return StringUtils.EMPTY;
        }
        DateTime dateTime = new DateTime(date);
        return dateTime.toString(formatStr);
    }


    public static String dateToStr(Date date){
        if (date==null){
            return StringUtils.EMPTY;
        }
        DateTime dateTime = new DateTime(date);
        return dateTime.toString(STANDARD_FORMAT);
    }

   /*测试，可正常运行
    public static void main(String[] args) {
        System.out.println(DateTimeUtil.dateToStr(new Date(),"yyyy-MM-dd"));
        System.out.println(DateTimeUtil.dateToStr(new Date()));
        System.out.println(DateTimeUtil.strToDate("2021-01-01","yyyy-MM-dd"));
        System.out.println(DateTimeUtil.strToDate("2021-01-01 11:11:11"));

    }*/




}
