package cn.mkblog.huashelper.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import cn.mkblog.huashelper.api.AppApi;
import cn.mkblog.huashelper.bean.SunDetailBean;
import cn.mkblog.huashelper.R;
import cn.mkblog.huashelper.tool.Tools;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/*
 * 阳光平台帖子浏览页
 * */
public class SunViewAty extends BaseAty {
    private TextView sunNo, sunFrom, sunSendTime, sunTypes,
            sunTo, sunStatus, sunSubject, sunContent, sunReplyTime, sunReply;

    @Override
    public void widgetClick(View v) {
        switch (v.getId()) {
            default:
        }
    }

    @Override
    public void initParams(Bundle params) {
    }

    @Override
    public View bindView() {
        return null;
    }

    @Override
    public int bindLayout() {
        return R.layout.aty_sunview;
    }

    @Override
    public void initView(View view) {
        sunNo = $(R.id.sun_no);
        sunFrom = $(R.id.sun_from);
        sunSendTime = $(R.id.sun_sendtime);
        sunTypes = $(R.id.sun_types);
        sunTo = $(R.id.sun_to);
        sunStatus = $(R.id.sun_status);
        sunSubject = $(R.id.sun_subject);
        sunContent = $(R.id.sun_content);
        sunReplyTime = $(R.id.sun_replytime);
        sunReply = $(R.id.sun_reply);
    }

    @Override
    public void setListener() {
    }


    @Override
    public void doBusiness(Context mContext) {
        setTitle("帖子浏览");

        // 获取帖子数据
        loadSunDetail();
    }


    /**
     * 获取帖子数据
     */
    private void loadSunDetail() {
        Intent intent = getIntent();
        // 获取帖子列表数据
        Retrofit retrofit2 = new Retrofit.Builder()
                .baseUrl(AppApi.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(new OkHttpClient())
                .build();
        AppApi appApi = retrofit2.create(AppApi.class);

        Call<SunDetailBean> call = appApi.sunDetail(intent.getStringExtra("id"), intent.getStringExtra("no"));
        call.enqueue(new Callback<SunDetailBean>() {
            @Override
            public void onResponse(Call<SunDetailBean> call, Response<SunDetailBean> response) {
                SunDetailBean sunDetailBean = response.body();
                if (sunDetailBean.getCode() == 200) {
                    sunNo.setText(sunDetailBean.getData().getNo());
                    sunFrom.setText(sunDetailBean.getData().getFrom());
                    sunSendTime.setText(sunDetailBean.getData().getSendtime());
                    sunTypes.setText(sunDetailBean.getData().getTypes());
                    sunTo.setText(sunDetailBean.getData().getTo());
                    sunStatus.setText(sunDetailBean.getData().getStatus());
                    sunSubject.setText(sunDetailBean.getData().getSubject());
                    sunContent.setText(sunDetailBean.getData().getContent().replace("\\n", "\n"));
                    sunReplyTime.setText(sunDetailBean.getData().getReplytime());
                    sunReply.setText(sunDetailBean.getData().getReply().replace("\\n", "\n"));
                } else {
                    showToast("帖子内容读取失败 - " + sunDetailBean.getCode() + "\n" + sunDetailBean.getMsg());
                }
            }

            @Override
            public void onFailure(Call<SunDetailBean> call, Throwable t) {
                showToast(Tools.connErrHandle(t));
            }
        });
    }

}
