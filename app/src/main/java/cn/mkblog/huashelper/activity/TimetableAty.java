package cn.mkblog.huashelper.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import cn.mkblog.huashelper.adapter.TimetableAdapter;
import cn.mkblog.huashelper.api.AppApi;
import cn.mkblog.huashelper.bean.CourseBean;
import cn.mkblog.huashelper.bean.JWCourseBean;
import cn.mkblog.huashelper.R;
import cn.mkblog.huashelper.tool.CourseDBHelper;
import cn.mkblog.huashelper.tool.DataSave;
import cn.mkblog.huashelper.tool.MkDialog;
import cn.mkblog.huashelper.tool.Tools;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static cn.mkblog.huashelper.tool.DateUtil.getTeachingWeek;
import static cn.mkblog.huashelper.tool.DateUtil.setTeachingWeek;

/**
 * 课程表
 */
public class TimetableAty extends BaseAty {
    private Context mContext;
    private CourseDBHelper courseDB;    // 数据库辅助操作类

    private Spinner circleSelector;
    private GridView gridCourse;

    private TimetableAdapter timetableAdapter;

    private List<String> circleList;      // 周次信息

    private List<CourseBean> courseList;      // 所有的课程信息
    private CourseBean[][] courseInfo;          // 本周的课程信息

    public static int CHANGE_COURSE_CALLBACK = 0;      // 课程信息发生了改变，要刷新课表
    public static int JW_LOGIN_IMPORT_COURSE = 0x3001;      // 教务系统进行了登录，执行导入课程

    int initAction = 0;         // 启动后执行的事件

    private String stuCookie;

    @Override
    public void widgetClick(View v) {
        switch (v.getId()) {
            default:
        }
    }

    @Override
    public void initParams(Bundle params) {
        // 获取启动事件
        if (params != null && params.containsKey("actions")) {
            initAction = params.getInt("actions");
        }
    }

    @Override
    public View bindView() {
        return null;
    }

    @Override
    public int bindLayout() {
        return R.layout.aty_timetable;
    }

    @Override
    public void initView(View view) {
        gridCourse = $(R.id.course_grid);   // 课程表
    }

    @Override
    public void setListener() {
    }

    @Override
    public void doBusiness(Context mContext) {
        setTitle("");   // 不显示标题

        this.mContext = mContext;

        // 在 ToolBar 上强行插入一个下拉控件。。
        circleSelector = new Spinner(mContext);

        RelativeLayout toolbarLayout = new RelativeLayout(mContext);
        RelativeLayout.LayoutParams btParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                (int) getResources().getDimension(R.dimen.toolbar_height));
        btParams.addRule(RelativeLayout.CENTER_IN_PARENT);

        toolbarLayout.addView(circleSelector, btParams);

        RelativeLayout.LayoutParams tblParam = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                (int) getResources().getDimension(R.dimen.toolbar_height));
        tblParam.addRule(RelativeLayout.CENTER_IN_PARENT);

        toolbar.addView(toolbarLayout, tblParam);

        // 创建Adapter
        courseDB = new CourseDBHelper(mContext);    // 初始化数据库
        courseList = courseDB.queryAll();   // 获取所有课程信息

        // 初始化课表相关内容
        courseInfo = new CourseBean[6][7];
        timetableAdapter = new TimetableAdapter(this);
        timetableAdapter.setContent(courseInfo, 6, 7);
        gridCourse.setAdapter(timetableAdapter);

        // 初始化周次选择下拉
        ArrayAdapter<String> spinnerAdapter;
        fillDataList();
        spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, circleList);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        circleSelector.setAdapter(spinnerAdapter);

        // 监听选择周次变化
        circleSelector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                loadCourse(i);

                // 记录周数的变更
                setTeachingWeek(TimetableAty.this, i + 1);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        // 单击课程，显示课程详细信息
        gridCourse.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                int row = position / 7;
                int column = position % 7;
                if (courseInfo[row][column] != null) {
                    String tmp =
                            "课程名称：" + courseInfo[row][column].getName() + "\n" +
                                    "授课教师：" + courseInfo[row][column].getTeacher() + "\n" +
                                    "上课时间：" + Tools.weekName[courseInfo[row][column].getWeek()] + " " +
                                    Tools.sectionName[courseInfo[row][column].getSection()] + "\n" +
                                    "上课地点：" + courseInfo[row][column].getClassroom() + "\n" +
                                    "上课周数：" + Tools.listToString(courseInfo[row][column].getCircle());

                    alert("课程详情", tmp);
                } else {
                    showToast("长按添加/编辑课程");
                }
            }
        });

        // 长按课程，进行编辑
        gridCourse.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {
                int row = position / 7;
                int column = position % 7;
                CourseBean tmpCourse = new CourseBean();
                if (courseInfo[row][column] != null) {
                    tmpCourse = courseInfo[row][column];
                } else {
                    tmpCourse.setWeek(column);
                    tmpCourse.setSection(row);
                }
                // 进入编辑页面
                Intent intent = new Intent(TimetableAty.this, CourseEditAty.class);
                intent.putExtra("dataBean", new Gson().toJson(tmpCourse));
                startActivityForResult(intent, CHANGE_COURSE_CALLBACK);
                return true;
            }
        });

        // 定位到当前周
        circleSelector.setSelection(getTeachingWeek(mContext) - 1);

        if (initAction == JW_LOGIN_IMPORT_COURSE) {  // 导入课程
            loadJWCourse();
        }
    }       // end init

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // 课表编辑 Aty 的回调
        if (requestCode == CHANGE_COURSE_CALLBACK) {
            if (data.hasExtra("changed")) {
                Boolean isRefresh = data.getBooleanExtra("changed", false);
                if (isRefresh) {
                    refreshCourse();    // 重新加载课表
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_timetable, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_timetable_add:   // 添加课程
                startActivityForResult(CourseEditAty.class, null, CHANGE_COURSE_CALLBACK);
                break;

            case R.id.menu_timetable_import:    // 从教务系统导入
                if (initAction == JW_LOGIN_IMPORT_COURSE) {  // 已经登录过了
                    loadJWCourse();
                } else {
                    showToast("请先登录教务系统");
                    jwLogin();
                }
                break;

            case R.id.menu_timetable_clear:     // 清空所有课程信息
                // 弹窗确认
                MkDialog.Builder mkBuilder = new MkDialog.Builder(this);
                mkBuilder
                        .setMessage("确定要删除所有课程信息吗？")
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        })
                        .setPositiveButton("清空", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                courseDB.deleteAll(); // 删除课程

                                showToast("所有课程信息均已被删除");

                                refreshCourse();    // 刷新课程表显示
                                dialog.dismiss();
                            }
                        });
                mkBuilder.create().show();
                break;

            default:
        }
        return true;
    }

    /**
     * 刷新课程表中的课程
     */
    private void refreshCourse() {
        courseList = courseDB.queryAll();   // 获取所有课程信息
        loadCourse((int) circleSelector.getSelectedItemId());
    }

    /**
     * 加载指定周次的课表信息
     */
    private void loadCourse(int circle) {
        circle++;
        // 循环清空数组
        for (int j = 0; j < 7; j++) {
            for (int i = 0; i < 6; i++) {
                courseInfo[i][j] = null;
            }
        }

        // 查找课程信息
        for (int i = 0; i < courseList.size(); i++) {
            List<Integer> tmpCircle = courseList.get(i).getCircle();

            for (int j = 0; j < tmpCircle.size(); j++) {
                if (tmpCircle.get(j) == circle) {
                    courseInfo[courseList.get(i).getSection()][courseList.get(i).getWeek()] = courseList.get(i);
                    break;
                }
            }
        }

        // 刷新 Adapter
        timetableAdapter.notifyDataSetChanged();
    }

    /**
     * 给周次下拉框填充周次信息
     */
    public void fillDataList() {
        circleList = new ArrayList<>();
        for (int i = 0; i < 25; ) {
            i++;
            circleList.add("第" + i + "周");
        }
    }

    /**
     * 从教务系统读取课程信息
     */
    private void loadJWCourse() {
        showToast("课程导入中...");

        // 获取 Cookie
        stuCookie = (String) DataSave.get(mContext, "stu_cookie", "");
        if (stuCookie == null) {     // 没有获取到 Cookie，弹出登录教务系统
            showToast("请先登录教务系统");
            jwLogin();
            return;
        }

        // 初始化 retrofit2
        Retrofit retrofit2 = new Retrofit.Builder()
                .baseUrl(AppApi.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(new OkHttpClient())
                .build();
        AppApi appApi = retrofit2.create(AppApi.class);

        // 发出请求
        Call<JWCourseBean> call = appApi.getCourse(stuCookie);
        call.enqueue(new Callback<JWCourseBean>() {
            @Override
            public void onResponse(Call<JWCourseBean> call, Response<JWCourseBean> response) {
                JWCourseBean JWCourse = response.body();

                switch (JWCourse.getCode()) {
                    case 4101:      // 登录已失效
                        showToast("教务系统登录已失效\n请重新登录");
                        jwLogin();
                        break;

                    case 200:   // 数据获取成功
                        if (JWCourse.getItems() == null) {
                            showToast("教务系统中似乎还没有录入课程...");
                            return;
                        }

                        int courseCount = JWCourse.getItems().size();

                        if (courseCount > 0) {
                            // 逐项显示到界面中
                            for (int i = 0; i < courseCount; i++) {
                                String tmpCircle = JWCourse.getItems().get(i).getCircle().toString();
                                // Log.e("读取课程", tmpCircle.substring(1, tmpCircle.length()-1));

                                // 写入数据库
                                int j = courseDB.addCourse(
                                        JWCourse.getItems().get(i).getName(),
                                        JWCourse.getItems().get(i).getTeacher(),
                                        JWCourse.getItems().get(i).getClassroom(),
                                        String.valueOf(JWCourse.getItems().get(i).getWeek()),
                                        String.valueOf(JWCourse.getItems().get(i).getSection()),
                                        tmpCircle.substring(1, tmpCircle.length() - 1));

                                Log.i("课程ID:", "id-" + j);

                            }

                            showToast("读取成功，共导入 " + courseCount + " 条课程信息");
                            refreshCourse();    // 重新加载课表
                        } else {
                            showToast("教务系统中似乎还没有录入课程...");
                        }
                        break;

                    default:
                        showToast(JWCourse.getMsg() + "\n请稍后再试");
                }

            }

            @Override
            public void onFailure(Call<JWCourseBean> call, Throwable t) {
                showToast("课程导入失败\n" + Tools.connErrHandle(t) + "\n请稍后再试");
            }
        });
    }
}
