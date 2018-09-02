package cn.mkblog.huashelper.api;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

/**
 *
 */
public interface WifiApi {
    // 上网登录窗地址
    String BASE_URL = "http://172.30.4.129/";

    // 获取流量信息
    @GET("/")
    Call<String> getHtml();

    // 注销登录
    @GET("/F.htm")
    Call<String> logOff();

    // 登录
    @FormUrlEncoded
    @POST("a70.htm")
    Call<String> login(@Field("DDDDD")String DDDDD,
                       @Field("upass")String upass);
}
