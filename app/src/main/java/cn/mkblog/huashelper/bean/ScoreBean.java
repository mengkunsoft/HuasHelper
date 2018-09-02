package cn.mkblog.huashelper.bean;

import java.util.List;

/**
 * 分数
 */

public class ScoreBean {
    private String stuName;
    private String scoreAll;
    private String scoreNow;
    private String scoreRest;
    private String GPA;
    private int code;
    private String msg;
    private List<String> head;
    private List<ScoresBean> score;

    public String getStuName() {
        return stuName;
    }

    public void setStuName(String stuName) {
        this.stuName = stuName;
    }

    public String getScoreAll() {
        return scoreAll;
    }

    public void setScoreAll(String scoreAll) {
        this.scoreAll = scoreAll;
    }

    public String getScoreNow() {
        return scoreNow;
    }

    public void setScoreNow(String scoreNow) {
        this.scoreNow = scoreNow;
    }

    public String getScoreRest() {
        return scoreRest;
    }

    public void setScoreRest(String scoreRest) {
        this.scoreRest = scoreRest;
    }

    public String getGPA() {
        return GPA;
    }

    public void setGPA(String GPA) {
        this.GPA = GPA;
    }

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

    public List<String> getHead() {
        return head;
    }

    public void setHead(List<String> head) {
        this.head = head;
    }

    public List<ScoresBean> getScore() {
        return score;
    }

    public void setScore(List<ScoresBean> score) {
        this.score = score;
    }

    public static class ScoresBean {
        private String no;
        private String term;
        private String id;
        private String name;
        private String score;
        private String credit;
        private String period;
        private String methods;
        private String property;
        private String types;
        private String examterm;
        private String examtypes;

        public ScoresBean() {}

        public ScoresBean(String no, String term, String id, String name, String score,
                String credit, String period, String methods, String property, String types,
                                       String examterm, String examtypes) {
            this.no = no;
            this.term = term;
            this.id = id;
            this.name = name;
            this.score = score;
            this.credit = credit;
            this.period = period;
            this.methods = methods;
            this.property = property;
            this.types = types;
            this.examterm = examterm;
            this.examtypes = examtypes;
        }

        public String getNo() {
            return no;
        }

        public void setNo(String no) {
            this.no = no;
        }

        public String getTerm() {
            return term;
        }

        public void setTerm(String term) {
            this.term = term;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getScore() {
            return score;
        }

        public void setScore(String score) {
            this.score = score;
        }

        public String getCredit() {
            return credit;
        }

        public void setCredit(String credit) {
            this.credit = credit;
        }

        public String getPeriod() {
            return period;
        }

        public void setPeriod(String period) {
            this.period = period;
        }

        public String getMethods() {
            return methods;
        }

        public void setMethods(String methods) {
            this.methods = methods;
        }

        public String getProperty() {
            return property;
        }

        public void setProperty(String property) {
            this.property = property;
        }

        public String getTypes() {
            return types;
        }

        public void setTypes(String types) {
            this.types = types;
        }

        public String getExamterm() {
            return examterm;
        }

        public void setExamterm(String examterm) {
            this.examterm = examterm;
        }

        public String getExamtypes() {
            return examtypes;
        }

        public void setExamtypes(String examtypes) {
            this.examtypes = examtypes;
        }
    }
}
