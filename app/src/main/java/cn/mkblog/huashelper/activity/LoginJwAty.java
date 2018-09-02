package cn.mkblog.huashelper.activity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import cn.mkblog.huashelper.api.AppApi;
import cn.mkblog.huashelper.bean.JwVerifyBean;
import cn.mkblog.huashelper.bean.LoginJwBean;
import cn.mkblog.huashelper.R;
import cn.mkblog.huashelper.tool.DataSave;
import cn.mkblog.huashelper.tool.Tools;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/*
 * 登录教务系统
 * */
public class LoginJwAty extends BaseAty {
    private final static String TAG = LoginJwAty.class.getSimpleName();
    private Context mContext;

    private Button btnLogin;        // 登录按钮
    private EditText etSID, etPW, etVf;     // 学号、密码、验证码
    private ImageView VfCode;       // 验证码图片

    private String cookie;      // 验证码对应的 COOKIE
    private String fromAty = null;

    @Override
    public void widgetClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login_jw: // 登录
                login();
                break;

            case R.id.iv_jw_verify:     // 刷新验证码
                loadVerify();      // 加载验证码
                break;
        }
    }

    @Override
    public void initParams(Bundle params) {
        // 获取回传activity
        if (params != null && params.containsKey("classname")) {
            fromAty = params.getString("classname");
        }
    }

    @Override
    public View bindView() {
        return null;
    }

    @Override
    public int bindLayout() {
        return R.layout.aty_loginjw;
    }

    @Override
    public void initView(View view) {
        btnLogin = $(R.id.btn_login_jw);
        etSID = $(R.id.et_jw_sid);
        etPW = $(R.id.et_jw_password);
        etVf = $(R.id.et_jw_verify);
        VfCode = $(R.id.iv_jw_verify);
    }

    @Override
    public void setListener() {
        btnLogin.setOnClickListener(this);
        VfCode.setOnClickListener(this);
    }

    @Override
    public void doBusiness(Context mContext) {
        this.mContext = mContext;

        setTitle("登录教务系统");

        loadVerify();      // 加载验证码

        etPW.setText((String) DataSave.get(mContext, "stu_pw", ""));
        etSID.setText((String) DataSave.get(mContext, "stu_id", ""));

        // 自动焦点
        if (etSID.getText().toString().equals("")) {
            etSID.requestFocus();
        } else if (etPW.getText().toString().equals("")) {
            etPW.requestFocus();
        } else {
            etVf.requestFocus();
        }
    }


    // 加载验证码
    private void loadVerify() {
        VfCode.setImageResource(R.drawable.verifycode);

        // 初始化 retrofit2
        Retrofit retrofit2 = new Retrofit.Builder()
                .baseUrl(AppApi.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(new OkHttpClient())
                .build();
        AppApi appApi = retrofit2.create(AppApi.class);

        // 发出请求
        Call<JwVerifyBean> call = appApi.verifyPic();
        call.enqueue(new Callback<JwVerifyBean>() {
            @Override
            public void onResponse(Call<JwVerifyBean> call, Response<JwVerifyBean> response) {
                JwVerifyBean jwVerify = response.body();
                if (jwVerify.getCode() == 200) {
                    cookie = jwVerify.getCookie();
                    try {
                        // base64 解码验证码图片
                        byte[] bitmapArray = Base64.decode(jwVerify.getVerify(), Base64.DEFAULT);
                        Bitmap bitmap = BitmapFactory.decodeByteArray(bitmapArray, 0, bitmapArray.length);

                        // 显示验证码图片
                        VfCode.setImageBitmap(bitmap);
                        // 清空验证码输入框的内容
                        etVf.setText("");
                    } catch (Exception e) {
                        e.printStackTrace();
                        showToast("验证码解析失败");
                    }
                } else {
                    showToast("验证码获取失败\n" + jwVerify.getMsg());
                }
            }

            @Override
            public void onFailure(Call<JwVerifyBean> call, Throwable t) {
                showToast("验证码刷新失败 - " + Tools.connErrHandle(t));
            }
        });

    }

    // 登录教务系统
    private void login() {
        final String sid = etSID.getText().toString(); // 学号
        if (sid.equals("")) {
            showToast("请输入学号");
            etSID.requestFocus();
            return;
        }

        final String pw = etPW.getText().toString(); // 密码
        if (pw.equals("")) {
            showToast("请输入密码");
            etPW.requestFocus();
            return;
        }

        final String vf = etVf.getText().toString(); // 验证码
        if (vf.equals("")) {
            showToast("请输入验证码");
            etVf.requestFocus();
            return;
        }

        // 登录按钮禁用
        btnLogin.setEnabled(false);

        // 初始化 retrofit2
        Retrofit retrofit2 = new Retrofit.Builder()
                .baseUrl(AppApi.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(new OkHttpClient())
                .build();
        AppApi appApi = retrofit2.create(AppApi.class);

        // 发出请求
        Call<LoginJwBean> call = appApi.loginJw(vf, cookie, sid, pw);
        call.enqueue(new Callback<LoginJwBean>() {
            @Override
            public void onResponse(Call<LoginJwBean> call, Response<LoginJwBean> response) {
                LoginJwBean jwLogin = response.body();
                if (jwLogin.getCode() == 200) {
                    // showToast(jwLogin.getName() + "，欢迎回来~");
                    Log.e(TAG, "COOKIE:" + cookie);

                    // DataSave.put(mContext, "stu_name", jwLogin.getName());
                    DataSave.put(mContext, "stu_id", sid);
                    DataSave.put(mContext, "stu_pw", pw);
                    DataSave.put(mContext, "stu_cookie", cookie);

                    // 返回来时的 activity
                    if (fromAty != null) {
                        try {
                            if (Class.forName(fromAty) == TimetableAty.class) {
                                // 来自课程表界面，通知读取课表
                                Bundle mBundle = new Bundle();
                                mBundle.putInt("actions", TimetableAty.JW_LOGIN_IMPORT_COURSE);
                                startActivity(TimetableAty.class, mBundle);
                            } else {
                                startActivity(Class.forName(fromAty));
                            }

                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                    }

                    finish();
                    return;
                } else if (jwLogin.getCode() == 4003) {  // 验证码错误
                    loadVerify();      // 刷新验证码
                    etVf.setText("");   // 清空输入框
                    etVf.requestFocus();    // 获取焦点
                }

                showToast("登录失败\n" + jwLogin.getMsg());
                btnLogin.setEnabled(true);
            }

            @Override
            public void onFailure(Call<LoginJwBean> call, Throwable t) {
                showToast("登录失败\n" + Tools.connErrHandle(t));
                btnLogin.setEnabled(true);
            }
        });
    }   // 登录教务系统结束


}
