package cn.mkblog.huashelper.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.mkblog.huashelper.api.WifiApi;
import cn.mkblog.huashelper.R;
import cn.mkblog.huashelper.tool.DataSave;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * 校园网一键连接
 */
@SuppressLint("SetTextI18n")
public class WifiAty extends BaseAty {
    private TextView tvFlow, tvFlowUnit, tvTime;
    private LinearLayout wifiData;
    private Button btnWifiLogin;
    private TextView tvWifiStatus;

    private connStatus curStatus;

    private Timer timerRefresh;

    String wifiUserName, wifiPassWord, wifiIsp;     // 热点账号信息
    public static int CHANGE_WIFI_CALLBACK = 0;      // wifi账号信息发生了改变，要重新读取

    @Override
    public void widgetClick(View v) {
        switch (v.getId()) {
            case R.id.btn_wifi_login:   // 登录
                doAction();
                break;

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
        return R.layout.aty_wifi;
    }

    @Override
    public void initView(View view) {
        tvFlow = $(R.id.tv_wifi_data);      // 已用流量
        tvFlowUnit = $(R.id.tv_wifi_unit);  // 流量单位
        tvTime = $(R.id.tv_wifi_time);      // 已用时间

        tvWifiStatus = $(R.id.tv_wifi_status);  // 连接状态
        btnWifiLogin = $(R.id.btn_wifi_login);  // 连接按钮

        wifiData = $(R.id.wifi_data_area);  // 流量、时间显示区域
    }

    @Override
    public void setListener() {
        btnWifiLogin.setOnClickListener(this);
    }

    @Override
    public void doBusiness(Context mContext) {
        setTitle("校园网一键连");

        // 初始状态为加载中
        changeStatus(connStatus.waiting);

        // 隐藏信息栏
        wifiData.setVisibility(View.INVISIBLE);

        // 获取热点账号信息
        initData();

        // 判断登录状态并获取信息
        getData();
    }

    // 获取用户保存的数据
    private void initData() {
        wifiUserName = (String) DataSave.get(WifiAty.this, "wifiUserName", "");
        wifiPassWord = (String) DataSave.get(WifiAty.this, "wifiPassWord", "");
        wifiIsp = (String) DataSave.get(WifiAty.this, "wifiIsp", "@cmcc");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 销毁定时器
        if (timerRefresh != null) timerRefresh.cancel();
    }

    // 按下了连接按钮。。
    private void doAction() {
        if (curStatus == connStatus.onLine) {    // 在线
            // 掉线处理
            logOff();
        } else {
            // 连接
            login();
        }

    }

    // 登录
    private void login() {
        if (wifiUserName == null || wifiUserName.equals("") ||
                wifiPassWord == null || wifiPassWord.equals("")) {
            // 没有获取账号信息，要求先设置账号信息
            showToast("请先设置校园网账号信息");
            startActivityForResult(WifiSetAty.class, null, CHANGE_WIFI_CALLBACK);
            return;
        }

        // 切换状态为加载中
        changeStatus(connStatus.waiting);

        // 初始化 retrofit2
        Retrofit retrofit2 = new Retrofit.Builder()
                .baseUrl(WifiApi.BASE_URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .client(new OkHttpClient())
                .build();
        WifiApi wifiApi = retrofit2.create(WifiApi.class);

        // 发出请求
        Call<String> call = wifiApi.login(wifiUserName + wifiIsp, wifiPassWord);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                String tmpHtml = response.body();

                Pattern p = Pattern.compile("<title>认证成功页</title>");
                Matcher matcher = p.matcher(tmpHtml);
                if (matcher.find()) {       // 已连接
                    changeStatus(connStatus.onLine);
                    showToast("连接成功！");
                    return;
                }

                // 获取上网时长
                p = Pattern.compile("<html><body>([\\s\\S]*)</body></html>");
                matcher = p.matcher(tmpHtml);
                if (matcher.find()) {
                    String tmpResult = matcher.group(1);
                    if (tmpResult.contains("")) {
                        alert("登录失败", "账号或密码错误！请核对并重新设置账号密码！");
                    } else {
                        alert("登录失败", "错误原因-\n" + tmpResult);
                    }
                    Log.i("登录", matcher.group(1));
                } else {
                    alert("登录失败", "错误原因未知！请尝试使用浏览器登录查看登录失败原因");
                }

                // 离线
                changeStatus(connStatus.offLine);
                Log.e("登录", tmpHtml);
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                alert("登录失败", "无法访问登录页面地址\n请连接到校园网热点后再尝试登录！");
                // 离线
                changeStatus(connStatus.offLine);
            }
        });
    }

    // 掉线
    private void logOff() {
        // 初始化 retrofit2
        Retrofit retrofit2 = new Retrofit.Builder()
                .baseUrl(WifiApi.BASE_URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .client(new OkHttpClient())
                .build();
        WifiApi wifiApi = retrofit2.create(WifiApi.class);

        // 发出请求
        Call<String> call = wifiApi.logOff();
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {

            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });

        // 更新状态为未连接
        changeStatus(connStatus.offLine);

        showToast("已注销登录");
    }

    // 改变软件状态
    private void changeStatus(connStatus newStatus) {
        if (newStatus == curStatus) return;
        curStatus = newStatus;

        if (timerRefresh != null) timerRefresh.cancel();  // 销毁流量信息获取定时器

        btnWifiLogin.setEnabled(true);
        btnWifiLogin.setText("一键连接");

        switch (curStatus) {
            case disconnect:    // 没连上热点
                wifiData.setVisibility(View.INVISIBLE);
                tvWifiStatus.setText("未连接至校园网 Wifi");
                break;

            case onLine:        // 正常在线
                wifiData.setVisibility(View.VISIBLE);
                btnWifiLogin.setText("断开连接");
                tvWifiStatus.setText("您已连接至校园网");

                refreshData();  // 开始自动刷新信息
                tvTime.setText("0");    // 上网时长
                tvFlow.setText("0");    // 流量信息
                break;

            case offLine:       // 离线
                wifiData.setVisibility(View.INVISIBLE);
                tvWifiStatus.setText("校园网未连接");
                break;

            case waiting:       // 执行操作中
                btnWifiLogin.setEnabled(false);
                tvWifiStatus.setText("请稍候...");
                break;

            default:

        }
    }

    // 当前状态
    private enum connStatus {
        disconnect,     // 没连上校园网热点
        offLine,        // 离线状态
        onLine,          // 正常在线
        waiting         // 执行操作中
    }


    // 刷新流量信息
    private void refreshData() {
        getData();    // 刷新获取流量信息
        timerRefresh = new Timer();
        timerRefresh.schedule(new TimerTask() {
            @Override
            public void run() {
                getData();    // 刷新获取流量信息
            }
        }, 0, 5000);    // 每五秒钟刷新一次
    }

    // 获取连接流量信息
    private void getData() {
        // 初始化 OkHttp，设置超时时间
        OkHttpClient client = new OkHttpClient.Builder().
                connectTimeout(500, TimeUnit.MILLISECONDS).
                readTimeout(500, TimeUnit.MILLISECONDS).
                writeTimeout(500, TimeUnit.MILLISECONDS).build();

        // 初始化 retrofit2
        Retrofit retrofit2 = new Retrofit.Builder()
                .baseUrl(WifiApi.BASE_URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .client(client)
                .build();
        WifiApi wifiApi = retrofit2.create(WifiApi.class);

        // 发出请求
        Call<String> call = wifiApi.getHtml();
        call.enqueue(new Callback<String>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                String tmpHtml = response.body();

                if (!tmpHtml.contains("注销页")) {
                    // 未登录
                    changeStatus(connStatus.offLine);
                    return;
                }

                String time, flow;
                // 获取上网时长
                Pattern p = Pattern.compile("time='([0-9]*)");
                Matcher matcher = p.matcher(tmpHtml);
                if (matcher.find()) {
                    time = matcher.group(1);
                } else {
                    time = "NaN";
                }
                tvTime.setText(time);

                // 获取上网流量
                p = Pattern.compile("flow='([0-9]*)");
                matcher = p.matcher(tmpHtml);
                if (matcher.find()) {
                    flow = matcher.group(1);
                    int byteSize = Integer.parseInt(flow);

                    // 流量格式化 当 byteSize 大于是1024字节时，开始循环，当循环到第4次时跳出
                    int i = 0;
                    while (Math.abs(byteSize) >= 1024) {
                        byteSize = byteSize / 1024;
                        i++;
                        if (i >= 4) break;
                    }

                    // 将KB,MB,GB,TB定义成一维数组；
                    String[] units = {"KB", "MB", "GB", "TB"};
                    flow = String.valueOf(Math.round(byteSize));
                    tvFlowUnit.setText(units[i]);
                } else {
                    flow = "NaN";
                }
                tvFlow.setText(flow);

                // 是不是两个信息都获取失败。
                if (flow.equals("NaN") && time.equals("NaN")) {
                    Log.e("返回登录页", response.body());

                    // 正常在线
                    changeStatus(connStatus.onLine);
                } else {
                    // 正常在线
                    changeStatus(connStatus.onLine);
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                // 未连接到热点
                changeStatus(connStatus.disconnect);

                showToast("未连接校园网");
                // showToast(Tools.connErrHandle(t));
            }
        });

    }   // getData

    public void createShortCut() {
        // 创建快捷方式的Intent
        Intent sIntent = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
        // 不允许重复创建
        sIntent.putExtra("duplicate", false);
        // 需要现实的名称
        sIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "校园网一键连");
        // 快捷图片
        Parcelable icon = Intent.ShortcutIconResource.fromContext(getApplicationContext(), R.drawable.wifi_logo);
        sIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, icon);
        // 点击快捷图片，运行的程序主入口
        sIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, new Intent(getApplicationContext(), WifiAty.class));
        // 发送广播。OK
        sendBroadcast(sIntent);

        showToast("创建桌面快捷方式成功！");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // 课表编辑 Aty 的回调
        if (requestCode == CHANGE_WIFI_CALLBACK) {
            if (data.hasExtra("changed")) {
                Boolean isRefresh = data.getBooleanExtra("changed", false);
                if (isRefresh) {
                    // 重新读取保存的账号信息
                    initData();
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_wifi, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_wifi_set:   // 设置校园网账号
                startActivityForResult(WifiSetAty.class, null, CHANGE_WIFI_CALLBACK);
                break;

            case R.id.menu_wifi_shortcut:   // 创建桌面快捷方式
                createShortCut();
                break;

            default:
        }
        return true;
    }
}
