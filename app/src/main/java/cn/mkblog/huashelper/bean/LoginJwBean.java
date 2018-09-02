package cn.mkblog.huashelper.bean;

/**
 * 教务系统登录返回信息
 */

public class LoginJwBean {

    /**
     * code : 200
     * msg : 登陆成功
     * name : xxx
     * sid : xxxxxxxxxxx
     */

    private int code;
    private String msg;
    private String name;
    private String sid;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }
}
