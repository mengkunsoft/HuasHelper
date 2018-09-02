package cn.mkblog.huashelper.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import cn.mkblog.huashelper.bean.CourseBean;
import cn.mkblog.huashelper.R;

/**
 * 首页课程列表适配器
 */
public class HomeScoreAdapter extends BaseAdapter {
    private List<CourseBean> course;
    private Context mContext;

    public HomeScoreAdapter(List<CourseBean> course, Context mContext) {
        this.course = course;
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        return course.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CourseBean courseInfo = course.get(position);
        ViewHolder holder;

        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.list_home_course, parent, false);

            holder.tvName = (TextView) convertView.findViewById(R.id.home_course_name);
            holder.tvNO = (TextView) convertView.findViewById(R.id.home_course_no);
            holder.tvTeacher = (TextView) convertView.findViewById(R.id.home_course_teacher);
            holder.tvClassroom = (TextView) convertView.findViewById(R.id.home_course_classroom);

            // 一些数据处理下
            if (courseInfo.getTeacher().equals("")) {
                courseInfo.setTeacher("授课：(未设置)");
            } else {
                courseInfo.setTeacher("授课：" + courseInfo.getTeacher());
            }
            if (courseInfo.getClassroom().equals("")) {
                courseInfo.setClassroom("教室：(未设置)");
            } else {
                courseInfo.setClassroom("教室：" + courseInfo.getClassroom());
            }

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.tvName.setText(courseInfo.getName());
        holder.tvNO.setText(String.valueOf(courseInfo.getSection() + 1));
        holder.tvTeacher.setText(courseInfo.getTeacher());
        holder.tvClassroom.setText(courseInfo.getClassroom());

        return convertView;
    }

    class ViewHolder{
        TextView tvName;        // 课程名称
        TextView tvNO;          // 节次
        TextView tvTeacher;     // 授课
        TextView tvClassroom;   // 教室
    }
}
