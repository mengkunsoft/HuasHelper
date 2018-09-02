package cn.mkblog.huashelper.activity;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.mkblog.huashelper.adapter.HomeScoreAdapter;
import cn.mkblog.huashelper.api.AppApi;
import cn.mkblog.huashelper.bean.CourseBean;
import cn.mkblog.huashelper.bean.IcibaBean;
import cn.mkblog.huashelper.bean.WeatherBean;
import cn.mkblog.huashelper.R;
import cn.mkblog.huashelper.tool.CheckUpdate;
import cn.mkblog.huashelper.tool.CourseDBHelper;
import cn.mkblog.huashelper.tool.DataSave;
import cn.mkblog.huashelper.tool.DateUtil;
import cn.mkblog.huashelper.tool.DownImage;
import cn.mkblog.huashelper.tool.DownloadAsyncTask;
import cn.mkblog.huashelper.tool.Tools;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static cn.mkblog.huashelper.tool.PermissionUtils.verifyStoragePermissions;

@SuppressLint("SetTextI18n")
public class HomeAty extends BaseAty {
    private final static String TAG = HomeAty.class.getSimpleName();
    private Context mContext;
    private final MyHandler mHandler = new MyHandler(this);

    private static ImageView enBgImg;
    private TextView enContentTv;
    private String engContent, engNote;
    private static String toDay;

    private TextView WeatherTemp, WeatherCity;
    private ImageView WeatherIcon;

    private TextView dataTime;
    private TextView noCourse;
    private ListView homeCourse;

    // 首页图标组
    private final int[][] home_item = {
            {R.drawable.home_news, R.string.home_news},     // 新闻
            {R.drawable.home_sun, R.string.home_sun},       // 阳光服务
            {R.drawable.home_score, R.string.home_score},       // 成绩查询
            {R.drawable.home_library, R.string.home_library},   // 图书检索
            {R.drawable.home_timetable, R.string.home_timetable},   // 我的课表
            {R.drawable.home_pay, R.string.home_pay},       // 缴费
            {R.drawable.home_wifi, R.string.home_wifi},     // 校园网连接
            {R.drawable.home_bbs, R.string.home_bbs},       // 社区
            {R.drawable.home_vista, R.string.home_vista},       // 街景
            {R.drawable.home_tieba, R.string.home_tieba},       // 街景
            {R.drawable.home_about, R.string.home_about},       // 街景
            {R.drawable.home_update, R.string.home_update}  // 检查更新
    };

    @Override
    public void widgetClick(View v) {
        switch (v.getId()) {
            case R.id.home_english_txt:
                // 点击每日一句，切换中英文
                if (enContentTv.getTag() == "cn") {
                    enContentTv.setText(engContent);
                    enContentTv.setTag("en");
                } else {
                    enContentTv.setText(engNote);
                    enContentTv.setTag("cn");
                }
                break;
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
        return R.layout.aty_home;
    }

    @Override
    public void initView(View view) {
        enBgImg = $(R.id.home_english_img);
        enContentTv = $(R.id.home_english_txt);

        WeatherTemp = $(R.id.home_weather_temp);
        WeatherCity = $(R.id.home_weather_city);
        WeatherIcon = $(R.id.home_weather_icon);

        dataTime = $(R.id.home_data_time);
        noCourse = $(R.id.home_course_null);
        homeCourse = $(R.id.home_course);
    }

    @Override
    public void setListener() {
        enContentTv.setOnClickListener(this);
    }

    @Override
    public void doBusiness(final Context mContext) {
        this.mContext = mContext;

        // 请求敏感权限
        verifyStoragePermissions(this);

        /*
         * 初始化网格布局
         * 参考资料：http://blog.csdn.net/qq1123655345/article/details/48029623
         * */
        GridView gridview = (GridView) findViewById(R.id.home_grid);

        // 生成动态数组，并且转入数据
        ArrayList<HashMap<String, Object>> lstImageItem = new ArrayList<>();
        for (int[] aHome_item : home_item) {
            HashMap<String, Object> map = new HashMap<>();
            map.put("ItemImage", aHome_item[0]);  // 获取图标
            map.put("ItemText", getResources().getText(aHome_item[1]));   // 获取文字
            lstImageItem.add(map);
        }

        // 生成适配器的ImageItem 与动态数组的元素相对应
        SimpleAdapter sAdapter = new SimpleAdapter(this,
                lstImageItem,   // 数据来源
                R.layout.home_item, // item的XML实现

                // 动态数组与ImageItem对应的子项
                new String[]{"ItemImage", "ItemText"},

                // ImageItem的XML文件里面的一个ImageView,两个TextView ID
                new int[]{R.id.iv_home_item, R.id.tv_home_item});

        // 添加并且显示
        gridview.setAdapter(sAdapter);

        // 监听点击执行对应操作
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (home_item[position][0]) {
                    case R.drawable.home_news:   // 新闻
                        startActivity(NewsAty.class);
                        break;

                    case R.drawable.home_sun:   // 阳光服务
                        startActivity(SunListAty.class);
                        break;

                    case R.drawable.home_score:   // 分数查询
                        startActivity(CheckScoresAty.class);
                        break;

                    case R.drawable.home_library:   // 图书馆
                        openWeb(AppApi.BASE_URL + "api.php?types=goto&target=library");
                        break;

                    case R.drawable.home_vista:   // 文理街景
                        openWeb(AppApi.BASE_URL + "api.php?types=goto&target=vista");
                        break;

                    case R.drawable.home_pay:   // 在线缴费
                        openWeb(AppApi.BASE_URL + "api.php?types=goto&target=pay");
                        break;

                    case R.drawable.home_wifi:      // 一键联网
                        startActivity(WifiAty.class);
                        break;

                    case R.drawable.home_bbs:       // 校园论坛
                        openWeb("https://support.qq.com/products/18008?d-wx-push=1");
                        break;

                    case R.drawable.home_timetable:     // 课程表
                        startActivity(TimetableAty.class);
                        break;

                    case R.drawable.home_tieba:     // 百度贴吧
                        openWeb("https://tieba.baidu.com/f?kw=湖南文理学院");
                        break;

                    case R.drawable.home_about:     // 关于
                        startActivity(AboutAty.class);
                        break;

                    case R.drawable.home_update:    // 检查更新
                        CheckUpdate.check(mContext, mHandler, false);
                        break;

                    default:
                        showToast("功能建设中...");

                }
            }
        });

        // 启动时检查更新
        CheckUpdate.check(mContext, mHandler, true);

        // 获取今天日期
        toDay = DateUtil.getSysDate();

        // 获取天气
        loadWeather();

        // 加载课表以及日期
        loadTimeTable();

        // 加载英语每日一句
        loadEnglish();

    }   // doBusiness

    // 加载天气信息
    private void loadWeather() {
        // 初始化 retrofit2
        Retrofit retrofit2 = new Retrofit.Builder()
                .baseUrl(AppApi.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(new OkHttpClient())
                .build();
        AppApi appApi = retrofit2.create(AppApi.class);

        // 发出请求
        Call<WeatherBean> call = appApi.getWeather();
        call.enqueue(new Callback<WeatherBean>() {
            @Override
            public void onResponse(Call<WeatherBean> call, Response<WeatherBean> response) {
                WeatherBean weather = response.body();

                if (!weather.getSuccess().equals("1")) {
                    WeatherCity.setText("天气读取失败");
                    return;
                }

                // 温度/摄氏度
                WeatherTemp.setText(weather.getResult().getTemp_curr() + "°C");

                // 地区·天气·风力
                if (weather.getResult().getWinp().equals("0级")) {    // 无风
                    WeatherCity.setText(weather.getResult().getCitynm() + "·" + weather.getResult().getWeather_curr());
                } else {
                    WeatherCity.setText(weather.getResult().getCitynm() + "·" + weather.getResult().getWeather_curr() + "·" +
                            weather.getResult().getWind() + weather.getResult().getWinp());
                }

                if (Integer.parseInt(weather.getResult().getWeatid()) > 33)
                    weather.getResult().setWeaid("0");
                WeatherIcon.setImageBitmap(getBitmapByName("weather_" + weather.getResult().getWeatid()));
            }

            @Override
            public void onFailure(Call<WeatherBean> call, Throwable t) {
                WeatherCity.setText("读取失败-" + Tools.connErrHandle(t));
            }
        });
    }

    // 根据名字获取 drawable 里的bitmap
    public Bitmap getBitmapByName(String name) {
        ApplicationInfo appInfo = getApplicationInfo();
        int resID = getResources().getIdentifier(name, "drawable", appInfo.packageName);
        return BitmapFactory.decodeResource(getResources(), resID);
    }

    // 加载今日课表
    private void loadTimeTable() {
        int curWeek = DateUtil.getTeachingWeek(mContext);
        dataTime.setText(DateUtil.getSysDate(2) + " " +
                DateUtil.getWeek(true) + " 第" +
                curWeek + "周");

        List<CourseBean> courseList;      // 所有的课程信息
        CourseDBHelper courseDB = new CourseDBHelper(mContext);    // 初始化数据库
        courseList = courseDB.todayCourse(curWeek, Integer.parseInt(DateUtil.getWeek(false)));

        if (courseList.size() > 0) {
            noCourse.setVisibility(View.GONE);
            homeCourse.setVisibility(View.VISIBLE);

            HomeScoreAdapter mAdapter = new HomeScoreAdapter(courseList, mContext);
            homeCourse.setAdapter(mAdapter);
            homeCourse.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    startActivity(TimetableAty.class);  // 打开课表
                }
            });
        } else {
            noCourse.setVisibility(View.VISIBLE);
            homeCourse.setVisibility(View.GONE);
        }

    }

    // 加载英语每日一句
    private void loadEnglish() {
        String lastGet = (String) DataSave.get(mContext, "english_last", "");

        Log.i("获取每日英语", "上次获取日期" + lastGet);

        // 显示图片
        showEngImg();
        engContent = (String) DataSave.get(mContext, "english_content", "每日英语");
        enContentTv.setText(engContent);
        engNote = (String) DataSave.get(mContext, "english_note", "每日英语");
        enContentTv.setTag("en");
        enContentTv.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                assert cm != null;
                cm.setPrimaryClip(ClipData.newPlainText(null, enContentTv.getText()));
                showToast("文本内容复制成功！");
                return true;
            }
        });

        // 今天已获取
        if (lastGet != null && lastGet.equals(toDay)) return;

        // 初始化 retrofit2
        Retrofit retrofit2 = new Retrofit.Builder()
                .baseUrl(AppApi.CIBA_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(new OkHttpClient())
                .build();
        AppApi appApi = retrofit2.create(AppApi.class);

        // 发出请求
        Call<IcibaBean> call = appApi.getEnglish();
        call.enqueue(new Callback<IcibaBean>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<IcibaBean> call, Response<IcibaBean> response) {
                final IcibaBean cibaInfo = response.body();

                engContent = cibaInfo.getContent();
                engNote = cibaInfo.getNote();

                enContentTv.setText(engContent);

                // 保存到本地
                DataSave.put(mContext, "english_content", engContent);
                DataSave.put(mContext, "english_note", engNote);

                // 存储图片
                DownImage.SaveImage("english/img.png", cibaInfo.getPicture(), mHandler);
            }

            @Override
            public void onFailure(Call<IcibaBean> call, Throwable t) {

            }
        });
    }

    // 更换英语背景图像
    private static void showEngImg() {
        boolean isSdCardExist = Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);// 判断sdcard是否存在
        if (isSdCardExist) {
            String filepath = Environment.getExternalStorageDirectory().getPath() + "/huasHelper/english/img.png";
            File file = new File(filepath);
            if (file.exists()) {
                Bitmap bm = BitmapFactory.decodeFile(filepath);
                // 将图片显示到ImageView中
                enBgImg.setImageBitmap(bm);
            }
        } else {
            Log.e("加载图片", "sd卡不存在！");
        }
    }

    private static class MyHandler extends Handler {
        WeakReference<HomeAty> outerClass;

        MyHandler(HomeAty activity) {
            outerClass = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(android.os.Message msg) {
            HomeAty activity = outerClass.get();

            if (msg.what == CheckUpdate.NO_UPDATE) {
                Toast.makeText(activity, "当前版本已是最新", Toast.LENGTH_SHORT).show();
            } else if (msg.what == CheckUpdate.ERR_UPDATE) {
                Toast.makeText(activity, "检查更新失败 - " + msg.obj, Toast.LENGTH_SHORT).show();
            } else if (msg.what == DownloadAsyncTask.FINISH) {
                Toast.makeText(activity, "更新包下载完成", Toast.LENGTH_SHORT).show();
                CheckUpdate.InstallApk(activity);   // 弹出安装界面
            } else if (msg.what == DownloadAsyncTask.NET_ERR) {
                Toast.makeText(activity, "更新包下载失败 - 网络错误", Toast.LENGTH_SHORT).show();
            } else if (msg.what == DownloadAsyncTask.URL_ERR) {
                Toast.makeText(activity, "更新包下载失败 - 下载地址错误", Toast.LENGTH_SHORT).show();
            } else if (msg.what == DownImage.OK) {
                showEngImg();
                DataSave.put(activity, "english_last", toDay);
            } else Log.w(TAG, "未知的Handler Message:" + msg.what);

        }
    }   // handler

}