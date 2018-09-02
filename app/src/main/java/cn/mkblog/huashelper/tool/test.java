package cn.mkblog.huashelper.tool;

import android.database.Cursor;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import cn.mkblog.huashelper.api.AppApi;
import cn.mkblog.huashelper.bean.CourseBean;
import cn.mkblog.huashelper.bean.News;
import cn.mkblog.huashelper.bean.NewsBean;
import cn.mkblog.huashelper.fragment.NewsListFrag;
import cn.mkblog.huashelper.tool.Tools;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class test {

/**
 * 获取所有的课程信息
 */
public List<CourseBean> queryAll() {
    List<CourseBean> courseList = new ArrayList<>();
    // 查询所有的课程
    Cursor cursor = getReadableDatabase().query("timetable",
            null, null, null, null, null, null);
    if (cursor.moveToFirst()) {
        do {    // 循环读取
            CourseBean courseInfo = new CourseBean();
            int id = cursor.getInt(cursor.getColumnIndex("id"));    // 获取课程编号
            String courseid = cursor.getString(cursor.getColumnIndex("courseid"));
            String teacherid = cursor.getString(cursor.getColumnIndex("teacherid"));
            String classroomid = cursor.getString(cursor.getColumnIndex("classroomid"));
            int week = cursor.getInt(cursor.getColumnIndex("week"));
            int section = cursor.getInt(cursor.getColumnIndex("section"));
            courseInfo.setId(id);
            courseInfo.setName(getName("course", courseid));  // 根据编号获取课程名称
            courseInfo.setTeacher(getName("teacher", teacherid));  // 根据编号获取教师姓名
            courseInfo.setClassroom(getName("classroom", classroomid)); // 根据编号获取教室地点
            courseInfo.setWeek(week);
            courseInfo.setSection(section);
            // 获取所有的上课周次信息
            List<Integer> circleList = new ArrayList<>();
            Cursor time = getReadableDatabase().query("coursetime", null, "classid=?", new String[]{String.valueOf(id)}, null, null, null);
            if (time.moveToFirst()) {
                do {
                    circleList.add(time.getInt(time.getColumnIndex("circle")));
                } while (time.moveToNext());
            }
            time.close();
            courseInfo.setCircle(circleList);
            courseList.add(courseInfo);
        } while (cursor.moveToNext());
    }
    cursor.close();
    return courseList;
}

}
