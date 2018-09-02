package cn.mkblog.huashelper.bean;

import java.util.List;

/**
 * 教务系统读取课程
 */

public class JWCourseBean {

    private int code;
    private String msg;
    private List<CourseBean> items;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public List<CourseBean> getItems() {
        return items;
    }

    public void setItems(List<CourseBean> items) {
        this.items = items;
    }
}
