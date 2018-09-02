package cn.mkblog.huashelper.tool;

import android.util.Log;

import java.util.List;

/**
 * 一个工具类
 */
public class Tools {
    // 星期转中文
    public static final String[] weekName = new String[] {
            "星期一", "星期二", "星期三", "星期四", "星期五", "星期六", "星期日"
    };

    // 课程节数转中文
    public static final String[] sectionName = new String[] {
            "第一大节", "第二大节", "第三大节", "第四大节", "第五大节", "第六大节"
    };

    // list 数据类型转换为字符串，用逗号分隔
    public static String listToString(List<?>  tmp) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < tmp.size(); i++) {
            sb.append(tmp.get(i));
            sb.append(",");
        }
        if (sb.length() > 0) sb.deleteCharAt(sb.length() - 1);   // 去除最后一个逗号

        return sb.toString();
    }

    /**
     * Retrofit2 的错误处理
     * */
    public static String connErrHandle(Throwable t) {
        Log.d("通信错误", "错误原因：" + t.toString());
        t.printStackTrace();
        if(t.getMessage() != null) {
            if(t.getMessage().toLowerCase().startsWith("failed to connect to")) {
                return "网络连接超时";
            } else {
                return t.getMessage();  //t.toString();
            }
        } else {
            return "未知错误";
        }
    }
}
