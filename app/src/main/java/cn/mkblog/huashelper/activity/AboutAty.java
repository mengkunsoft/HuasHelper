package cn.mkblog.huashelper.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import cn.mkblog.huashelper.R;
import cn.mkblog.huashelper.tool.CheckUpdate;

@SuppressLint("SetTextI18n")
public class AboutAty extends BaseAty {
    private TextView tvAbout;

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
        return R.layout.aty_about;
    }

    @Override
    public void initView(View view) {
        tvAbout = $(R.id.tv_about);
    }

    @Override
    public void setListener() {
    }


    @Override
    public void doBusiness(Context mContext) {
        setTitle(getResources().getString(R.string.app_name));

        // 获取版本号信息
        String versionName;
        PackageInfo info = CheckUpdate.getAppVersion(mContext);
        if (info == null) {
            versionName = "未知";
        } else {
            versionName = info.versionName;
        }

        // 填充关于信息
        tvAbout.setText("编译版本：" + versionName + "\n\n" +
                "程序开发：孤帆远影\n" +
                "特别感谢：@xbdcc 帮忙测试程序\n\n" +
                "采用(参照)的模块：\n" +
                "[1].Retrofit2 https://square.github.io/retrofit \n" +
                "[2].ViewPagerIndicator https://github.com/LuckyJayce/ViewPagerIndicator \n" +
                "[3].设计一个通用的BaseActivity http://www.jianshu.com/p/4d4b54c98f5d \n" +
                "[4].HuasTools https://github.com/xbdcc/HuasTools \n" +
                "[5].模仿课程表的布局 http://blog.csdn.net/supervictim/article/details/52809516 \n" +
                "[6].使用GridView实现九宫格布局 http://blog.csdn.net/qq1123655345/article/details/48029623 \n\n" +
                "程序中部分图标素材来自 https://www.flaticon.com 和 http://iconfont.cn"
        );
    }   // init

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_about, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_about_homepage:      // 官网
                openWeb("http://huas.mkblog.cn/app/");
                break;

            case R.id.menu_about_feedback:      // 反馈
                openWeb("https://support.qq.com/embed/phone/18008#/new-topic");
                break;

            case R.id.menu_about_like:          // 点赞
                showToast("谢谢支持~\n我们会继续努力 : )");
                break;

            case R.id.menu_about_share:         // 分享
                Intent textIntent = new Intent(Intent.ACTION_SEND);
                textIntent.setType("text/plain");
                textIntent.putExtra(Intent.EXTRA_TEXT,
                        "hi~我发现了一个巨好玩的专属于文理人的 App！\n戳链接下载：http://huas.mkblog.cn/app/");
                startActivity(Intent.createChooser(textIntent, "邀请好友下载"));
                break;

            default:
        }
        return true;
    }
}
