package cn.mkblog.huashelper.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import cn.mkblog.huashelper.R;
import cn.mkblog.huashelper.tool.MkDialog;

/**
 * 基础 aty
 * 来自 http://www.jianshu.com/p/4d4b54c98f5d
 * 参考 https://blog.csdn.net/black_dreamer/article/details/69666961 https://www.zhihu.com/question/47045239
 */
public abstract class BaseAty extends AppCompatActivity implements View.OnClickListener {
    /**
     * 是否沉浸状态栏
     **/
    private boolean isSetStatusBar = false;
    /**
     * 是否允许全屏
     **/
    private boolean mAllowFullScreen = false;
    /**
     * 是否禁止旋转屏幕
     **/
    private boolean isAllowScreenRotate = false;
    /**
     * 当前Activity渲染的视图View
     **/
    private View mContextView = null;
    /**
     * 日志输出标志
     **/
    protected final String TAG = this.getClass().getSimpleName();
    /**
     * 顶部工具栏
     **/
    public Toolbar toolbar;

    /**
     * View点击
     **/
    public abstract void widgetClick(View v);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "BaseActivity-->onCreate()");
        Bundle bundle = getIntent().getExtras();
        initParams(bundle);
        View mView = bindView();
        if (null == mView) {
            mContextView = LayoutInflater.from(this)
                    .inflate(bindLayout(), null);
        } else {
            mContextView = mView;
        }
        // 是否全屏
        if (mAllowFullScreen) requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 是否沉浸
        if (isSetStatusBar) steepStatusBar();
        setContentView(mContextView);
        // 是否允许旋屏
        if (!isAllowScreenRotate) setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        initView(mContextView);
        setListener();
        doBusiness(this);
    }

    /**
     * [沉浸状态栏] 有BUG
     */
    private void steepStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // 透明状态栏
            getWindow().addFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            // 透明导航栏
            getWindow().addFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
    }

    /**
     * [初始化参数]
     *
     * @param params Bundle参数
     */
    public abstract void initParams(Bundle params);

    /**
     * [绑定视图]
     *
     * @return null
     */
    public abstract View bindView();

    /**
     * [绑定布局]
     *
     * @return null
     */
    public abstract int bindLayout();

    /**
     * [初始化控件]
     *
     * @param view view
     */
    public abstract void initView(final View view);

    /**
     * [绑定控件]
     *
     * @param resId 资源ID
     * @return 查找到的控件
     */
    protected <T extends View> T $(int resId) {
        return (T) super.findViewById(resId);
    }

    /**
     * [设置监听]
     */
    public abstract void setListener();

    @Override
    public void onClick(View v) {
        widgetClick(v);
    }

    /**
     * [业务操作]
     *
     * @param mContext Context
     */
    public abstract void doBusiness(Context mContext);


    /**
     * [设置页面标题]
     *
     * @param title 标题
     */
    public void setTitle(String title) {
        if (toolbar == null) {
            toolbar = $(R.id.toolbar);
            setSupportActionBar(toolbar);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();    // 返回上一页
                }
            });
        }
        toolbar.setTitle(title);
    }

    /**
     * [页面跳转]
     *
     * @param clz 新的 Aty 类
     */
    public void startActivity(Class<?> clz) {
        startActivity(new Intent(BaseAty.this, clz));
    }

    /**
     * [携带数据的页面跳转]
     *
     * @param clz    目标类
     * @param bundle bundle数据包
     */
    public void startActivity(Class<?> clz, Bundle bundle) {
        Intent intent = new Intent();
        intent.setClass(this, clz);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivity(intent);
    }

    /**
     * [含有Bundle通过Class打开编辑界面]
     *
     * @param cls         目标类
     * @param bundle      bundle数据包
     * @param requestCode 返回标识代码
     */
    public void startActivityForResult(Class<?> cls, Bundle bundle,
                                       int requestCode) {
        Intent intent = new Intent();
        intent.setClass(this, cls);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivityForResult(intent, requestCode);
    }

    /**
     * [打开教务系统登录窗口]
     */
    public void jwLogin() {
        Bundle mBundle = new Bundle();
        mBundle.putString("classname", this.getClass().getName());
        startActivity(LoginJwAty.class, mBundle);
        finish();
    }

    /**
     * [打开指定网址]
     *
     * @param url 要打开的网址
     */
    public void openWeb(String url) {
        openWeb(url, null);
    }

    /**
     * [带post信息打开指定网址]
     *
     * @param url  要打开的网址
     * @param post 发送的post内容
     */
    public void openWeb(String url, String post) {
        Bundle mBundle = new Bundle();
        mBundle.putString("url", url);
        mBundle.putString("post", post);
        startActivity(WebAty.class, mBundle);
    }


    /**
     * [简化Toast]
     *
     * @param msg 消息内容
     */
    protected void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    /**
     * [简化弹窗]
     *
     * @param title 弹窗标题
     * @param msg   消息内容
     */
    public void alert(String title, String msg) {

        // 弹出选择框
        MkDialog.Builder mkBuilder = new MkDialog.Builder(this);
        mkBuilder
                .setTitle(title)
                .setMessage(msg)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        mkBuilder.create().show();
    }

    /**
     * [是否允许全屏]
     *
     * @param allowFullScreen 是否允许全屏
     */
    public void setAllowFullScreen(boolean allowFullScreen) {
        this.mAllowFullScreen = allowFullScreen;
    }

    /**
     * [是否设置沉浸状态栏]
     *
     * @param isSetStatusBar 是否沉浸
     */
    public void setSteepStatusBar(boolean isSetStatusBar) {
        this.isSetStatusBar = isSetStatusBar;
    }

    /**
     * [是否允许屏幕旋转]
     *
     * @param isAllowScreenRotate 是否允许屏幕旋转
     */
    public void setScreenRotate(boolean isAllowScreenRotate) {
        this.isAllowScreenRotate = isAllowScreenRotate;
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "onRestart()");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart()");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume()");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause()");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop()");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy()");
    }
}
