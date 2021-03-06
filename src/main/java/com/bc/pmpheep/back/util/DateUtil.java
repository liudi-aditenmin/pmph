package com.bc.pmpheep.back.util;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.poi.ss.usermodel.Cell;

public final class DateUtil {
    private final static SimpleDateFormat sdfYear = new SimpleDateFormat("yyyy");

    private final static SimpleDateFormat sdfDay  = new SimpleDateFormat("yyyy-MM-dd");

    private final static SimpleDateFormat sdfDays = new SimpleDateFormat("yyyyMMdd");

    private final static SimpleDateFormat sdfTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * 按照yyyy-MM-dd的格式，字符串转日期
     * 
     * @param date
     * @return
     */
    public static Date str3Date(String date) {
        if (StringUtil.notEmpty(date)) {
            try {
                return sdfDay.parse(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return new Date();
        } else {
            return null;
        }
    }

    /**
     * 按照yyyy-MM-dd HH:mm:ss的格式，字符串转日期
     *
     * @param date
     * @return
     */
    public static Date str4Date(String date) {
        if (StringUtil.notEmpty(date)) {
            try {
                return sdfTime.parse(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return new Date();
        } else {
            return null;
        }
    }

    /**
     * 获取YYYY格式
     * 
     * @return
     */
    public static String getYear() {
        return sdfYear.format(new Date());
    }

    /**
     * 获取YYYY-MM-DD格式
     * 
     * @return
     */
    public static String getDay() {
        return sdfDay.format(new Date());
    }

    /**
     * 获取YYYYMMDD格式
     * 
     * @return
     */
    public static String getDays() {
        return sdfDays.format(new Date());
    }

    /**
     * 获取YYYY-MM-DD HH:mm:ss格式
     * 
     * @return
     */
    public static String getTime() {
        return sdfTime.format(new Date());
    }

    /**
     * @Title: compareDate
     * @Description: TODO(日期比较，如果s>=e 返回true 否则返回false)
     * @param s
     * @param e
     * @return boolean
     * @throws
     * @author luguosui
     */
    public static boolean compareDate(String s, String e) {
        if (fomatDate(s) == null || fomatDate(e) == null) {
            return false;
        }
        return fomatDate(s).getTime() >= fomatDate(e).getTime();
    }

    /**
     * 格式化日期
     * 
     * @return
     */
    public static Date fomatDate(String date) {
        try {
            return sdfDay.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 校验日期是否合法
     * 
     * @return
     */
    public static boolean isValidDate(String s) {
        try {
            sdfDay.parse(s);
            return true;
        } catch (Exception e) {
            // 如果throw java.text.ParseException或者NullPointerException，就说明格式不对
            return false;
        }
    }

    public static int getDiffYear(String startTime, String endTime) {
        try {
            int years =
            (int) (((sdfDay.parse(endTime).getTime() - sdfDay.parse(startTime).getTime()) / (1000 * 60 * 60 * 24)) / 365);
            return years;
        } catch (Exception e) {
            // 如果throw java.text.ParseException或者NullPointerException，就说明格式不对
            return 0;
        }
    }

    /**
     * <li>功能描述：时间相减得到天数
     * 
     * @param beginDateStr
     * @param endDateStr
     * @return long
     * @author Administrator
     */
    public static long getDaySub(String beginDateStr, String endDateStr) {
        long day = 0;
        java.util.Date beginDate = null;
        java.util.Date endDate = null;
        try {
            beginDate = sdfDay.parse(beginDateStr);
            endDate = sdfDay.parse(endDateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        day = (endDate.getTime() - beginDate.getTime()) / (24 * 60 * 60 * 1000);
        // System.out.println("相隔的天数="+day);

        return day;
    }

    /**
     * 得到n天之后的日期
     * 
     * @param days
     * @return
     */
    public static String getAfterDayDate(String days) {
        int daysInt = Integer.parseInt(days);

        Calendar canlendar = Calendar.getInstance(); // java.util包
        canlendar.add(Calendar.DATE, daysInt); // 日期减 如果不够减会将月变动
        Date date = canlendar.getTime();

        String dateStr = sdfTime.format(date);

        return dateStr;
    }

    /**
     * 
     * <pre>
     * 功能描述：获取几天前日期
     * 使用示范：
     *
     * @param d 基准时间
     * @param day 几天前
     * @return 几天前日期
     * </pre>
     */
    public static java.util.Date getDateBefore(Date d, int day) {
        Calendar now = Calendar.getInstance();
        now.setTime(d);
        now.set(Calendar.DATE, now.get(Calendar.DATE) - day);
        return now.getTime();
    }

    /**
     * 得到n天之后是周几
     * 
     * @param days
     * @return
     */
    public static String getAfterDayWeek(String days) {
        int daysInt = Integer.parseInt(days);

        Calendar canlendar = Calendar.getInstance(); // java.util包
        canlendar.add(Calendar.DATE, daysInt); // 日期减 如果不够减会将月变动
        Date date = canlendar.getTime();

        SimpleDateFormat sdf = new SimpleDateFormat("E");
        String dateStr = sdf.format(date);

        return dateStr;
    }

    /**
     * 按照yyyy-MM-dd HH:mm:ss的格式，日期转字符串
     * 
     * @param date
     * @return yyyy-MM-dd HH:mm:ss
     */
    public static String date2Str(Date date) {
        return date2Str(date, "yyyy-MM-dd HH:mm:ss");
    }

    /**
     * 按照yyyy-MM-dd HH:mm:ss的格式，字符串转日期
     * 
     * @param date
     * @return
     */
    public static Date str2Date(String date) {
        if (StringUtil.notEmpty(date)) {
            try {
                return sdfTime.parse(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return new Date();
        } else {
            return null;
        }
    }

    /**
     * 按照参数format的格式，日期转字符串
     * 
     * @param date
     * @param format
     * @return
     */
    public static String date2Str(Date date, String format) {
        if (ObjectUtil.notNull(date)) {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            return sdf.format(date);
        } else {
            return "";
        }
    }

    /**
     * 把时间根据时、分、秒转换为时间段
     * 
     * @param StrDate
     */
    public static String getTimes(String StrDate) {
        String resultTimes = "";
        java.util.Date now;
        try {
            now = new Date();
            java.util.Date date = sdfTime.parse(StrDate);
            long times = now.getTime() - date.getTime();
            long day = times / (24 * 60 * 60 * 1000);
            long hour = (times / (60 * 60 * 1000) - day * 24);
            long min = ((times / (60 * 1000)) - day * 24 * 60 - hour * 60);
            long sec = (times / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);

            StringBuffer sb = new StringBuffer();
            // sb.append("发表于：");
            if (hour > 0) {
                sb.append(hour + "小时前");
            } else if (min > 0) {
                sb.append(min + "分钟前");
            } else {
                sb.append(sec + "秒前");
            }

            resultTimes = sb.toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return resultTimes;
    }

    /**
     * 得到当前时间
     * 
     * @return
     */
    public static Timestamp getCurrentTime() {
        String ret;
        ret = sdfTime.format(new java.util.Date());
        Timestamp currentTime = Timestamp.valueOf(ret);
        return currentTime;
    }

    /**
     * 得到当前时间(yyyy-MM-dd)
     * 
     * @return
     */
    public static Timestamp getCurrentTimeByYMD() {
        String ret;
        ret = sdfDay.format(new java.util.Date()) + " 00:00:00.0";
        Timestamp currentTime = Timestamp.valueOf(ret);
        return currentTime;
    }

    /**
     * 字符串转时间戳
     */
    public static Timestamp str2Timestam(String date) {
        String ret;
        ret = sdfTime.format(fomatDate(date));
        Timestamp currentTime = Timestamp.valueOf(ret);
        return currentTime;
    }

    /**
     * 
     * <pre>
     * 功能描述：格式化时间戳(java.sql.Timestamp)
     * 使用示范：
     *
     * @param format
     * @param tm
     * @return String
     * </pre>
     */
    public static String formatTimeStamp(String format, Timestamp tm) {
        if (ObjectUtil.notNull(tm)) {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            return sdf.format(new Date(tm.getTime()));
        } else {
            return "";
        }
    }

    /**
     * 指定时间转换成今天昨天前天
     * 
     * @author Mryang
     * @createDate 2017年11月28日 上午11:42:25
     * @param timeStamp
     * @return
     */
    public static String format(Timestamp timeStamp) {
        // 1天的毫秒
        int oneDayMillis = 24 * 60 * 60 * 1000;
        // 今天0点的时间错
        long todayStartMillis = 0L;
        String time = formatTimeStamp("yyyy-MM-dd HH:mm:ss", timeStamp);
        try {
            DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String tsStr = sdf.format(new Date());
            Date date = sdf.parse(tsStr);
            todayStartMillis = date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (timeStamp.getTime() >= todayStartMillis) {
            return "今天 " + time.substring(11, 16);
        }

        long yesterdayStartMilis = todayStartMillis - oneDayMillis; // 昨天
        if (timeStamp.getTime() >= yesterdayStartMilis) {
            return "昨天 " + time.substring(11, 16);
        }
        long yesterdayBeforeStartMilis = yesterdayStartMilis - oneDayMillis;
        if (timeStamp.getTime() >= yesterdayBeforeStartMilis) {
            return "前天 " + time.substring(11, 16);
        }
        return time;
    }

    public static void main(String[] args) throws ParseException {

        Timestamp ts = new Timestamp(System.currentTimeMillis());
        System.out.println(format(ts));

    }

    /**
     * 
     * Description:解析Excel文件时动态判断单元格类型并返回时间数据
     * 
     * @author:lyc
     * @date:2017年11月30日下午12:58:00
     * @param
     * @return String
     */
    @SuppressWarnings("deprecation")
    public static String getCellValue(Cell cell) {
        if (null == cell) {
            return sdfTime.format(new Date());
        }
        String result = "";
        if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
            short format = cell.getCellStyle().getDataFormat();
            if (format == 14 || format == 31 || format == 57 || format == 58) {
                result =
                sdfDay.format(org.apache.poi.ss.usermodel.DateUtil.getJavaDate(cell.getNumericCellValue()));
            } else {
                result = sdfTime.format(new Date());
            }
        }
        return result;
    }

}
