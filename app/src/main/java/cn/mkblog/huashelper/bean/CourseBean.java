package cn.mkblog.huashelper.bean;

import java.util.List;

/**
 * 课程信息
 */

public class CourseBean {

    /**
     * name : 课程名字
     * teacher : 老师名字
     * classroom : 教室地址
     * week : 1
     * section : 2
     * circle : [1,2,3,4,5,6]
     */

    private int id;
    private String name;
    private String teacher;
    private String classroom;
    private int week;
    private int section;
    private List<Integer> circle;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTeacher() {
        return teacher;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }

    public String getClassroom() {
        return classroom;
    }

    public void setClassroom(String classroom) {
        this.classroom = classroom;
    }

    public int getWeek() {
        return week;
    }

    public void setWeek(int week) {
        this.week = week;
    }

    public int getSection() {
        return section;
    }

    public void setSection(int section) {
        this.section = section;
    }

    public List<Integer> getCircle() {
        return circle;
    }

    public void setCircle(List<Integer> circle) {
        this.circle = circle;
    }
}
