package cn.mkblog.huashelper.tool;

import android.content.Context;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * 日期
 * https://www.cnblogs.com/Yang2/p/3441018.html
 */
public class DateUtil {
    static Calendar today = Calendar.getInstance();

    // 2015-01-05 是星期一，程序内周次计时以此为“零点”
    private static String absData = "2015-01-05";

    /* 获取日期 */
    public static String getDay() {
        return getSysDate("dd");
    }

    /* 获取月份 */
    public static String getMonth() {
        return getSysDate("MM");
    }

    /* 获取年份 */
    public static String getYear() {
        return getSysDate("yyyy");
    }

    /* 获取当前系统时间 */
    public static String getSysDate() {
        return getSysDate(0);
    }

    public static String getSysDate(int format) {
        String[] form = {
                "yyyy-MM-dd",
                "yyyy-MM-dd HH:mm",
                "yyyy年MM月dd日",
                "yyyy年MM月dd日 HH:mm"
        };
        return getSysDate(form[format]);
    }

    public static String getSysDate(String format) {
        SimpleDateFormat formatter = new SimpleDateFormat(format, Locale.getDefault());
        Date curDate = new Date(System.currentTimeMillis());
        return formatter.format(curDate);
    }

    public static String formatDatetime(String date) throws ParseException{
        DateFormat fmt = new SimpleDateFormat("yyyy年MM月dd日", Locale.getDefault());
        Date d = fmt.parse(date);
        return d.toString();
    }



    /* 获取当前时间的毫秒 */
    public String getSysTimeMillis(){
        long i = System.currentTimeMillis();
        return String.valueOf(i);
    }

    /* 获取星期几 */
    public static String getWeek(Boolean format) {
        Calendar cal = Calendar.getInstance();
        int day = cal.get(Calendar.DAY_OF_WEEK);

        // 纠正别扭的星期值
        if(day == 1) day = 8;
        day = day - 2;

        if(format) {
            String[] weeks = {"星期一", "星期二", "星期三", "星期四", "星期五", "星期六", "星期日"};
            return weeks[day];
        } else {
            return String.valueOf(day);
        }
    }

    // 获取教学周次
    public static Integer getTeachingWeek(Context mContext) {
        int curWeek = 1;
        int absWeek = getAbsWeek();

        // 获取程序记录的起始周
        int startWeek = (int) DataSave.get(mContext, "start_weeks", 0);

        // 范围界定
        if(startWeek != 0 && absWeek != 0) {
            curWeek = absWeek - startWeek;
        }
        if(curWeek < 1 || curWeek > 25) curWeek = 1;

        return curWeek;
    }

    // 设置当前教学周
    public static void setTeachingWeek(Context mContext, int curWeek) {
        int absWeek = getAbsWeek();

        // 记录起始周
        if(absWeek != 0) DataSave.put(mContext, "start_weeks", absWeek - curWeek);
    }

    // 获取程序相对周数
    public static int getAbsWeek() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        try {
            long startDay = dateFormat.parse(absData).getTime();
            long toDay = new Date().getTime(); // dateFormat.parse("2017-11-12").getTime();//new Date().getTime();
            long absWeek = (toDay-startDay)/(1000*3600*24*7);  // 计算相差的周数
            System.out.println("相差周数为：" + absWeek);

            return (int) absWeek;
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static String formatCommentTime(String str){

        Date date = parse(str, "EEE MMM dd HH:mm:ss zzz yyyy", Locale.US);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
        String dateStr = sdf.format(date);

        return dateStr;
    }

    public static Date parse(String str, String pattern, Locale locale) {
        if(str == null || pattern == null) {
            return null;
        }
        try {
            return new SimpleDateFormat(pattern, locale).parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String minCaseMax(String str){
        switch (Integer.parseInt(str)) {
            case 0:
                return "零";
            case 1:
                return "一";
            case 2:
                return "二";
            case 3:
                return "三";
            case 4:
                return "四";
            case 5:
                return "五";
            case 6:
                return "六";
            case 7:
                return "七";
            case 8:
                return "八";
            case 9:
                return "九";

            default:
                return "null";
        }
    }

}
