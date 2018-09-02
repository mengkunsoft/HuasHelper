package cn.mkblog.huashelper.bean;

import java.util.List;


/**
 * 新闻列表
 * NewsBean
 */
public class NewsBean {
    public int code;    // 返回的标识码
    public String msg;  // 返回信息
    public int counts;  // 获取到的新闻数
    public List<items> items;   // 新闻条目

    public class items{
        public int types;       // 新闻类型
        public int id;          // 新闻ID
        public String title;    // 新闻标题
        public String data;     // 新闻发布日期
    }
}