package com.c.framework.elasticsearch.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {

    /**
     * yyyy-MM-dd HH:mm:ss
     */
    public static final String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";

    /**
     * yyyy-MM-dd HH:mm
     */
    public static final String YYYY_MM_DD_HH_MM = "yyyy-MM-dd HH:mm";

    /**
     * yyyy-MM-dd
     */
    public static final String YYYY_MM_DD = "yyyy-MM-dd";

    /**
     * HH:mm
     */
    public static final String HH_MM = "HH:mm";

    /**
     * 一天最大毫秒数
     */
    public static final int MAX_DAY_TIME = 24 * 60 * 60 * 1000;
    
    /**
     * 根据相差月份获取指定月份的起始时间
     *
     * @param diffNum 1为上个月 2为上上个月
     * @return
     */
    public static Date getMonthStart(int diffNum) {
        Calendar calendar = getCurStartCalendar();
        setMonthStart(calendar, diffNum);
        return calendar.getTime();
    }

    /**
     * 根据相差月份获取指定月份的结束时间
     *
     * @param diffNum 1为上个月 2为上上个月
     * @return
     */
    public static Date getMonthEnd(int diffNum) {
        Calendar calendar = getCurStartCalendar();
        setMonthStart(calendar, diffNum - 1);
        calendar.add(Calendar.MILLISECOND, -1);
        return calendar.getTime();
    }

    /**
     * 根据日期获取指定月份的起始时间
     *
     * @param date
     * @return
     */
    public static Date getMonthStart(Date date) {
        Calendar calendar = getStartCalendar(date);
        setMonthStart(calendar, 0);
        return calendar.getTime();
    }

    /**
     * 根据日期获取指定月份的结束时间
     *
     * @param date
     * @return
     */
    public static Date getMonthEnd(Date date) {
        Calendar calendar = getStartCalendar(date);
        setMonthStart(calendar, -1);
        return calendar.getTime();
    }

    /**
     * 根据相差天数获取指定日期的起始时间
     *
     * @param diffNum 1为昨天 2为前天
     * @return
     */
    public static Date getDayStart(int diffNum) {
        Calendar calendar = getCurStartCalendar();
        calendar.add(Calendar.DAY_OF_MONTH, -diffNum);
        return calendar.getTime();
    }

    /**
     * 根据相差天数获取指定日期的结束时间
     *
     * @param diffNum 1为昨天 2为前天
     * @return
     */
    public static Date getDayEnd(int diffNum) {
        Calendar calendar = getCurStartCalendar();
        calendar.add(Calendar.DAY_OF_MONTH, -diffNum + 1);
        calendar.add(Calendar.MILLISECOND, -1);
        return calendar.getTime();
    }

    /**
     * 根据日期获取指定日期的起始时间
     *
     * @param date
     * @return
     */
    public static Date getDayStart(Date date) {
        Calendar calendar = getStartCalendar(date);
        return calendar.getTime();
    }

    /**
     * 根据日期获取指定日期的结束时间
     *
     * @param date
     * @return
     */
    public static Date getDayEnd(Date date) {
        Calendar calendar = getStartCalendar(date);
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        calendar.add(Calendar.MILLISECOND, -1);
        return calendar.getTime();
    }

    /**
     * 根据相差周数获取指定周的起始时间
     * 周为周一开始周末结束 java默认周天开始周六结束
     *
     * @param diffNum 1为上周 2为上上周
     * @return
     */
    public static Date getWeekStart(int diffNum) {
        Calendar calendar = getCurStartCalendar();
        calendar.set(Calendar.DAY_OF_WEEK, 2);
        calendar.add(Calendar.WEEK_OF_MONTH, -diffNum);
        return calendar.getTime();
    }

    /**
     * 根据相差周数获取指定周的结束时间
     * 周为周一开始周末结束 java默认周天开始周六结束
     *
     * @param diffNum 1为上周 2为上上周
     * @return
     */
    public static Date getWeekEnd(int diffNum) {
        Calendar calendar = getCurStartCalendar();
        calendar.set(Calendar.DAY_OF_WEEK, 2);
        calendar.add(Calendar.WEEK_OF_MONTH, -diffNum + 1);
        calendar.add(Calendar.MILLISECOND, -1);
        return calendar.getTime();
    }

    /**
     * 根据日期获取指定周的起始时间
     * 周为周一开始周末结束 java默认周天开始周六结束
     *
     * @param date
     * @return
     */
    public static Date getWeekStart(Date date) {
        Calendar calendar = getStartCalendar(date);
        calendar.set(Calendar.DAY_OF_WEEK, 2);
        return calendar.getTime();
    }

    /**
     * 根据日期获取指定周的结束时间
     * 周为周一开始周末结束 java默认周天开始周六结束
     *
     * @param date
     * @return
     */
    public static Date getWeekEnd(Date date) {
        Calendar calendar = getStartCalendar(date);
        calendar.set(Calendar.DAY_OF_WEEK, 2);
        calendar.add(Calendar.WEEK_OF_MONTH, 1);
        calendar.add(Calendar.MILLISECOND, -1);
        return calendar.getTime();
    }

    /**
     * 根据相差年份获取指定年份的起始时间
     *
     * @param diffNum 1为去年 2为前年
     * @return
     */
    public static Date getYearStart(int diffNum) {
        Calendar calendar = getCurStartCalendar();
        calendar.set(Calendar.DAY_OF_YEAR, 1);
        calendar.add(Calendar.YEAR, -diffNum);
        return calendar.getTime();
    }

    /**
     * 根据相差年份获取指定年份的结束时间
     *
     * @param diffNum 1为去年 2为前年
     * @return
     */
    public static Date getYearEnd(int diffNum) {
        Calendar calendar = getCurStartCalendar();
        calendar.set(Calendar.DAY_OF_YEAR, 1);
        calendar.add(Calendar.YEAR, -diffNum + 1);
        calendar.add(Calendar.MILLISECOND, -1);
        return calendar.getTime();
    }

    /**
     * 根据日期获取指定年份的起始时间
     *
     * @param date
     * @return
     */
    public static Date getYearStart(Date date) {
        Calendar calendar = getStartCalendar(date);
        calendar.set(Calendar.DAY_OF_YEAR, 1);
        return calendar.getTime();
    }

    /**
     * 根据日期获取指定年份的结束时间
     *
     * @param date
     * @return
     */
    public static Date getYearEnd(Date date) {
        Calendar calendar = getStartCalendar(date);
        calendar.set(Calendar.DAY_OF_YEAR, 1);
        calendar.add(Calendar.YEAR, 1);
        calendar.add(Calendar.MILLISECOND, -1);
        return calendar.getTime();
    }

    /**
     * 根据相差季节数获取指定季节的起始时间
     *
     * @param diffNum 1为上一季度 2为上上季度
     * @return
     */
    public static Date getQuarterStart(int diffNum) {
        diffNum = -diffNum;
        Calendar calendar = getCurStartCalendar();
        calendar.set(Calendar.MONTH, getStartQuarter(calendar.get(Calendar.MONTH)) + diffNum * 3);
        setMonthStart(calendar, 0);
        return calendar.getTime();
    }

    /**
     * 根据相差季节数获取指定季节的结束时间
     *
     * @param diffNum 1为上一季度 2为上上季度
     * @return
     */
    public static Date getQuarterEnd(int diffNum) {
        diffNum = -diffNum;
        Calendar calendar = getCurStartCalendar();
        calendar.set(Calendar.MONTH, getStartQuarter(calendar.get(Calendar.MONTH)) + (diffNum + 1) * 3);
        setMonthStart(calendar, 0);
        calendar.set(Calendar.MILLISECOND, -1);
        return calendar.getTime();
    }

    /**
     * 根据相日期获取指定季节的起始时间
     *
     * @param date
     * @return
     */
    public static Date getQuarterStart(Date date) {
        Calendar calendar = getStartCalendar(date);
        calendar.set(Calendar.MONTH, getStartQuarter(calendar.get(Calendar.MONTH)));
        setMonthStart(calendar, 0);
        return calendar.getTime();
    }

    public static int getStartQuarter(int month) {
        month = month + 1;
        int diff = month % 3;
        return month - (diff == 0 ? 3 : diff);
    }

    /**
     * 根据日期数获取指定季节的结束时间
     *
     * @param date
     * @return
     */
    public static Date getQuarterEnd(Date date) {
        Calendar calendar = getStartCalendar(date);
        int month = getStartQuarter(calendar.get(Calendar.MONTH)) + 3;
        calendar.set(Calendar.MONTH, month);
        setMonthStart(calendar, 0);
        calendar.set(Calendar.MILLISECOND, -1);
        return calendar.getTime();
    }

    /**
     * 获取天日期格式化 yyyy-MM-dd
     *
     * @param date
     * @return
     */
    public static String getDayString(Date date) {
        Instant instant = date.toInstant();
        ZoneId zoneId = ZoneId.systemDefault();
        LocalDate localDate = instant.atZone(zoneId).toLocalDate();
        return localDate.toString();
    }

    /**
     * 获取星期
     *
     * @param date
     * @return
     */
    public static String getDayOfWeek(Date date) {
        Instant instant = date.toInstant();
        ZoneId zoneId = ZoneId.systemDefault();
        LocalDate localDate = instant.atZone(zoneId).toLocalDate();
        return localDate.getDayOfWeek().name();
    }

    /**
     * 获取天日期格式化 yyyy-MM-dd HH:mm:ss
     *
     * @param date
     * @return
     */
    public static String getDayTimeString(Date date) {
        Instant instant = date.toInstant();
        ZoneId zoneId = ZoneId.systemDefault();
        LocalDateTime localDateTime = instant.atZone(zoneId).toLocalDateTime();
        return localDateTime.toString();
    }

    /**
     * 获取天日期格式化 yyyy-MM-dd HH:mm:ss
     *
     * @param date
     * @return
     */
    public static String getTimeString(Date date) {
        Instant instant = date.toInstant();
        ZoneId zoneId = ZoneId.systemDefault();
        LocalTime localTime = instant.atZone(zoneId).toLocalTime();
        return localTime.toString();
    }

    /**
     * 设置时间
     *
     * @param calendar
     * @param hour
     * @param minute
     * @param second
     */
    public static void setTime(Calendar calendar, int hour, int minute, int second) {
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, second);
        calendar.set(Calendar.MILLISECOND, 0);
    }

    /**
     * 设置相差月份起始时间
     *
     * @param calendar
     * @param diffNum
     */
    public static void setMonthStart(Calendar calendar, int diffNum) {
        calendar.add(Calendar.MONTH, -diffNum);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
    }

    /**
     * 获取当前时间日历
     *
     * @return
     */
    public static Calendar getCurStartCalendar() {
        return getStartCalendar(new Date());
    }

    /**
     * 获取当前时间日历
     *
     * @return
     */
    public static Calendar getStartCalendar(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        setTime(calendar, 0, 0, 0);
        return calendar;
    }

    /**
     * 字符串日期格式按照日期模式转换成日期
     *
     * @param sDate   -- 字符串的日期
     * @param pattern -- 日期格式 （比如：yyyy-MM-dd）
     * @return
     * @throws ParseException
     */
    public static Date parseToDate(String sDate, String pattern) {
        try {
            SimpleDateFormat sf = new SimpleDateFormat(pattern);
            return sf.parse(sDate);
        } catch (Exception ex) {
            throw new EsException("日期转换异常");
        }
    }

    /**
     * 同步年月日
     *
     * @param calendar 需要同步的日历
     * @param sync     目标日历
     * @return
     */
    public static Date syncYearMonthDay(Calendar calendar, Calendar sync) {
        calendar.set(Calendar.YEAR, sync.get(Calendar.YEAR));
        calendar.set(Calendar.MONTH, sync.get(Calendar.MONTH));
        calendar.set(Calendar.DAY_OF_MONTH, sync.get(Calendar.DAY_OF_MONTH));
        return calendar.getTime();
    }

    /**
     * 获取两个时间的天数差
     * @param startDate
     * @param endDate
     * @return
     */
    public static int getSubDay(Date startDate, Date endDate) {
        Date date = getDayStart(startDate);
        long start = date.getTime() / MAX_DAY_TIME;
        Date dayStart = getDayStart(endDate);
        long end = dayStart.getTime() / MAX_DAY_TIME;
        return (int) (end - start);
    }

    public static void main(String[] args) {
        System.out.println(getDayTimeString(new Date()));
    }
}
