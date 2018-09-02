package cn.mkblog.huashelper.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.google.gson.Gson;

import java.util.List;

import cn.mkblog.huashelper.bean.CourseBean;
import cn.mkblog.huashelper.R;
import cn.mkblog.huashelper.tool.CourseDBHelper;
import cn.mkblog.huashelper.tool.MkDialog;
import cn.mkblog.huashelper.tool.Tools;

/**
 * 课程编辑
 */
public class CourseEditAty extends BaseAty {

    private CourseDBHelper courseDB;    // 数据库辅助操作类
    private CourseBean courseInfo;

    private AutoCompleteTextView courseName, courseTeacher, courseRoom;
    private Spinner courseWeek, courseSection;
    private TextView courseCircle;

    private Button btnSave, btnDelete;

    private ToggleButton tbCircle[];

    @Override
    public void widgetClick(View v) {
        switch (v.getId()) {
            case R.id.course_circle:    // 选择周次
                showSelect();
                break;

            case R.id.btn_save_course:  // 保存课程信息
                saveCourse();
                break;

            case R.id.btn_delete_course:    // 删除课程信息
                deleteCourse();
                break;
            default:
        }
    }

    @Override
    public void initParams(Bundle params) {
        // 获取是编辑还是新增
        if (params != null && params.containsKey("dataBean")) {
            courseInfo = new Gson().fromJson(params.getString("dataBean"), CourseBean.class);
        }
    }

    @Override
    public View bindView() {
        return null;
    }

    @Override
    public int bindLayout() {
        return R.layout.aty_course_edit;
    }

    @Override
    public void initView(View view) {
        courseName = $(R.id.course_name);       // 课程名字
        courseTeacher = $(R.id.course_teacher);     // 讲课教师
        courseRoom = $(R.id.course_room);       // 上课地点
        courseWeek = $(R.id.course_week);       // 星期
        courseSection = $(R.id.course_section);     // 节次
        courseCircle = $(R.id.course_circle);     // 周次

        btnSave = $(R.id.btn_save_course);  // 保存
        btnDelete = $(R.id.btn_delete_course);  // 删除
    }

    @Override
    public void setListener() {
        courseCircle.setOnClickListener(this);
        btnSave.setOnClickListener(this);
        btnDelete.setOnClickListener(this);
    }

    @Override
    public void doBusiness(Context mContext) {
        // 初始化数据库
        courseDB = new CourseDBHelper(mContext);

        // 课程名字自动联想
        List<String> courseArr = courseDB.queryAllName("course");
        ArrayAdapter<String> courseAdapter = new ArrayAdapter<>(this, R.layout.simple_list_item, courseArr);
        courseName.setAdapter(courseAdapter);

        // 教师名字自动联想
        List<String> teacherArr = courseDB.queryAllName("teacher");
        ArrayAdapter<String> teacherAdapter = new ArrayAdapter<>(this, R.layout.simple_list_item, teacherArr);
        courseTeacher.setAdapter(teacherAdapter);

        // 教室地点自动联想
        List<String> classroomArr = courseDB.queryAllName("classroom");
        ArrayAdapter<String> classroomAdapter = new ArrayAdapter<>(this, R.layout.simple_list_item, classroomArr);
        courseRoom.setAdapter(classroomAdapter);

        // 填充星期值
        ArrayAdapter<String> weekAdapter = new ArrayAdapter<>(this, R.layout.spiner_text_item, Tools.weekName);
        weekAdapter.setDropDownViewResource(R.layout.simple_list_item);
        courseWeek.setAdapter(weekAdapter);

        // 填充课程节
        ArrayAdapter<String> sectionAdapter = new ArrayAdapter<>(this, R.layout.spiner_text_item, Tools.sectionName);
        sectionAdapter.setDropDownViewResource(R.layout.simple_list_item);
        courseSection.setAdapter(sectionAdapter);

        if (courseInfo != null) {
            courseWeek.setSelection(courseInfo.getWeek());       // 星期
            courseSection.setSelection(courseInfo.getSection());     // 节次

            if (courseInfo.getName() != null) {
                setTitle("修改课程");
                courseName.setText(courseInfo.getName());       // 课程名字
                courseTeacher.setText(courseInfo.getTeacher());     // 讲课教师
                courseRoom.setText(courseInfo.getClassroom());       // 上课地点
                courseCircle.setText(Tools.listToString(courseInfo.getCircle()));     // 周次
            } else {
                setTitle("添加课程");
                btnDelete.setVisibility(View.GONE);
            }
        } else {
            setTitle("添加课程");
            btnDelete.setVisibility(View.GONE);
        }

        // 默认返回信息：未发生改变
        Intent mIntent = new Intent();
        mIntent.putExtra("changed", false);
        this.setResult(TimetableAty.CHANGE_COURSE_CALLBACK, mIntent);
    }

    // 保存课程
    private void saveCourse() {
        if (courseInfo != null && courseInfo.getName() != null) {
            courseDB.deleteCourse(String.valueOf(courseInfo.getId()));
        }

        String name = courseName.getText().toString();
        if (name.equals("")) {
            showToast("请输入课程名字");
            courseName.requestFocus();
            return;
        }

        String circle = courseCircle.getText().toString();
        if (circle.equals("")) {
            showToast("请选择上课周次");
            return;
        }

        int i = courseDB.addCourse(
                name,
                courseTeacher.getText().toString(),
                courseRoom.getText().toString(),
                String.valueOf(courseWeek.getSelectedItemId()),
                String.valueOf(courseSection.getSelectedItemId()),
                circle);

        Log.i("课程ID:", "id-" + i);

        showToast("课程信息保存成功");

        // 返回课程界面，并刷新课表
        returnWithChange();
    }

    // 删除课程
    private void deleteCourse() {
        if (courseInfo != null && courseInfo.getName() != null) {

            // 弹出选择框
            MkDialog.Builder mkBuilder = new MkDialog.Builder(this);
            mkBuilder
                    .setMessage("确定要删除 " + courseInfo.getName() + " 这门课程吗？")
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    })
                    .setPositiveButton("删除", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            courseDB.deleteCourse(String.valueOf(courseInfo.getId()));
                            showToast("课程已被删除");

                            // 返回课程界面，并刷新课表
                            returnWithChange();

                            dialog.dismiss();
                        }
                    });
            mkBuilder.create().show();
        }
    }

    // 返回课程表界面并提示信息已发生变更，刷新课表
    private void returnWithChange() {
        Intent mIntent = new Intent();
        mIntent.putExtra("changed", true);
        this.setResult(TimetableAty.CHANGE_COURSE_CALLBACK, mIntent);

        finish();
    }

    // 弹出周次选择框
    private void showSelect() {
        // 获取屏幕大小，以合理设定 按钮 大小及位置
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels * 7 / 10;
        int btnWidth = width / 5, btnHeight = 120, topMargin = 0;
        String[] btnNameArr = {"单周", "双周", "全选"};

        // 自定义layout组件
        RelativeLayout layout = new RelativeLayout(this);

        // 创建 25 个按钮 + 3 个快捷功能键
        tbCircle = new ToggleButton[28];
        for (int i = 0; i < tbCircle.length; i++) {
            String btnText = Integer.toString(i + 1);
            tbCircle[i] = new ToggleButton(this);
            if (i > 24) {
                btnWidth = width / 3;
                btnText = btnNameArr[i - 25];
                //if(i == 27) btnWidth += 2;
            }
            tbCircle[i].setTag(i);
            tbCircle[i].setTextOff(btnText);
            tbCircle[i].setTextOn(btnText);
            tbCircle[i].setText(btnText);
            tbCircle[i].setBackgroundResource(R.drawable.course_circle_selete);

            // 设置按钮的宽度和高度
            RelativeLayout.LayoutParams btParams = new RelativeLayout.LayoutParams(btnWidth, btnHeight);

            // 排满五个换行
            if (i % 5 == 0 && i != 0) topMargin += (btnHeight + 1);

            // 相对定位
            btParams.leftMargin = (btnWidth + 1) * (i % 5);   //横坐标定位
            btParams.topMargin = topMargin;   // 纵坐标定位
            btParams.rightMargin = 0;
            btParams.bottomMargin = 0;
            layout.addView(tbCircle[i], btParams);   // 将按钮放入layout组件
        }

        // 弹出选择框
        MkDialog.Builder mkBuilder = new MkDialog.Builder(this);
        mkBuilder
                .setTitle("周数选择")
                .setContentView(layout)
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        getSelect();
                        dialog.dismiss();
                    }
                });
        mkBuilder.create().show();

        // 选定初始量
        String[] all = courseCircle.getText().toString().split(",");
        for (String tmp : all) {
            try {
                tbCircle[Integer.parseInt(tmp) - 1].setChecked(true);
            } catch (Exception ignored) {
            }
        }

        // 批量设置监听
        for (ToggleButton aBtn : tbCircle) {

            aBtn.setOnClickListener(new Button.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int i = (Integer) v.getTag();
                    if (i == 25) {
                        selectSingle();     // 单周
                    } else if (i == 26) {
                        selectDouble();     // 双周
                    } else if (i == 27) {
                        selectAll();        // 全选
                    } else {
                        tbCircle[25].setChecked(false);
                        tbCircle[26].setChecked(false);
                        tbCircle[27].setChecked(false);
                    }
                }
            });
        }
    }

    // 选单周
    private void selectSingle() {
        if (tbCircle[25].isChecked()) {
            for (int i = 0; i < tbCircle.length - 3; i++) {
                if (i % 2 == 0) {
                    tbCircle[i].setChecked(true);
                } else {
                    tbCircle[i].setChecked(false);
                }
            }
        } else {
            for (int i = 0; i < tbCircle.length - 3; i += 2) {
                tbCircle[i].setChecked(false);
            }
        }
        tbCircle[26].setChecked(false);
        tbCircle[27].setChecked(false);
    }

    // 选双周
    private void selectDouble() {
        if (tbCircle[26].isChecked()) {
            for (int i = 0; i < tbCircle.length - 3; i++) {
                if (i % 2 != 0) {
                    tbCircle[i].setChecked(true);
                } else {
                    tbCircle[i].setChecked(false);
                }
            }
        } else {
            for (int i = 1; i < tbCircle.length - 3; i += 2) {
                tbCircle[i].setChecked(false);
            }
        }
        tbCircle[25].setChecked(false);
        tbCircle[27].setChecked(false);
    }

    // 全部选定
    private void selectAll() {
        if (tbCircle[27].isChecked()) {
            for (int i = 0; i < tbCircle.length - 3; i++) {
                tbCircle[i].setChecked(true);
            }
        } else {
            for (int i = 0; i < tbCircle.length - 3; i++) {
                tbCircle[i].setChecked(false);
            }
        }
        tbCircle[25].setChecked(false);
        tbCircle[26].setChecked(false);
    }

    // 获取选定的元素
    private void getSelect() {
        StringBuilder tmp = new StringBuilder();
        for (int i = 0; i < tbCircle.length - 3; i++) {
            if (tbCircle[i].isChecked()) {
                tmp.append(tbCircle[i].getText());
                tmp.append(",");
            }
        }
        if (tmp.length() > 0) tmp.deleteCharAt(tmp.length() - 1);   // 去除最后一个逗号
        courseCircle.setText(tmp);
    }
}
