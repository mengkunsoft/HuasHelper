package cn.mkblog.huashelper.api;

import cn.mkblog.huashelper.bean.IcibaBean;
import cn.mkblog.huashelper.bean.JWCourseBean;
import cn.mkblog.huashelper.bean.JwVerifyBean;
import cn.mkblog.huashelper.bean.LoginJwBean;
import cn.mkblog.huashelper.bean.NewsBean;
import cn.mkblog.huashelper.bean.ScoreBean;
import cn.mkblog.huashelper.bean.SunDetailBean;
import cn.mkblog.huashelper.bean.SunListBean;
import cn.mkblog.huashelper.bean.UpdateBean;
import cn.mkblog.huashelper.bean.WeatherBean;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * 软件请求接口
 */
public interface AppApi {
    // 软件后台地址
    String BASE_URL = "http://huas.mkblog.cn/";

    // 检查更新
    @GET("api.php?types=update")
    Call<UpdateBean> checkUpdate();

    // 获取新闻列表
    @GET("api.php?types=newslist")
    Call<NewsBean> newsList(@Query("treeID")int treeID, @Query("pages")int pages);

    // 获取阳光服务帖子列表
    @GET("api.php?types=sunlist")
    Call<SunListBean> sunList(@Query("pages")int pages);

    // 获取阳光服务帖子详细内容
    @GET("api.php?types=sundetail")
    Call<SunDetailBean> sunDetail(@Query("id")String id, @Query("no")String no);

    // 获取验证码图片
    @GET("api.php?types=verifypic")
    Call<JwVerifyBean> verifyPic();

    // 获取验证码图片
    @POST("api.php?types=loginjw")
    Call<LoginJwBean> loginJw(@Query("verify")String verify, @Query("cookie")String cookie, @Query("sid")String sid, @Query("pw")String pw);

    // 教务系统分数查询
    @POST("api.php?types=getscore")
    Call<ScoreBean> getScore(@Query("cookie")String cookie);

    // 教务系统课程读取
    @POST("api.php?types=timetable")
    Call<JWCourseBean> getCourse(@Query("cookie")String cookie);

    // 获取天气信息
    @GET("api.php?types=weather")
    Call<WeatherBean> getWeather();

    // 金山词霸开放API
    String CIBA_URL = "http://open.iciba.com/";

    // 获取词霸每日一句
    @GET("dsapi/")
    Call<IcibaBean> getEnglish();
}