package cn.mkblog.huashelper.bean;

/**
 * 新闻类
 */

public class News {
    private String nTitle;   // 新闻名字
    private String nTime;   // 新闻发布时间
    private int nType;      // 新闻类型（ID）
    private int nID;        // 新闻编号

    public News() {
    }

    public News(String Title, String Time, int Type, int ID) {
        this.nTitle = Title;
        this.nTime = Time;
        this.nType = Type;
        this.nID = ID;
    }

    public String getnTitle() {
        return nTitle;
    }

    public String getnTime() {
        return nTime;
    }

    public int getnType() {
        return nType;
    }

    public int getnID() {
        return nID;
    }

    public void setnTitle(String Title) {
        this.nTitle = Title;
    }

    public void setnTime(String Time) {
        this.nTime = Time;
    }

    public void setnType(int Type) {
        this.nType = Type;
    }

    public void setnID(int ID) {
        this.nID = ID;
    }
}
