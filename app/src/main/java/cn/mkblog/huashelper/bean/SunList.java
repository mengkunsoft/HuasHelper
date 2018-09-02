package cn.mkblog.huashelper.bean;

/**
 * 阳光服务数据
 */

public class SunList {
    private String title;
    private String data;
    private String types;
    private String id;
    private String no;
    private String auth;
    private String status;

    public SunList() {
    }

    public SunList(String title, String data, String types,
                   String id, String no, String auth, String status) {
        this.title = title;
        this.data = data;
        this.types = types;
        this.id = id;
        this.no = no;
        this.auth = auth;
        this.status = status;
    }

    public String getTitle() {
        return title;
    }

    public String getData() {
        return data;
    }

    public String getTypes() {
        return types;
    }

    public String getID() {
        return id;
    }

    public String getNo() {
        return no;
    }

    public String getAuth() {
        return auth;
    }

    public String getStatus() {
        return status;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setData(String data) {
        this.data = data;
    }

    public void setTypes(String types) {
        this.types = types;
    }

    public void setID(String ID) {
        this.id = ID;
    }

    public void setNo(String No) {
        this.no = No;
    }

    public void setAuth(String auth) {
        this.auth = auth;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
