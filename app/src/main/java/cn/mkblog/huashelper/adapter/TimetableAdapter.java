package cn.mkblog.huashelper.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.Objects;

import cn.mkblog.huashelper.bean.CourseBean;
import cn.mkblog.huashelper.R;

/**
 * 课程表适配器
 */
public class TimetableAdapter extends BaseAdapter {

    private Context mContext;

    private CourseBean[][] courseInfo;
    private int rowTotal;
    private int columnTotal;
    private int positionTotal;

    private final int[] bgRes = {R.drawable.course_bg_1, R.drawable.course_bg_2, R.drawable.course_bg_3,
            R.drawable.course_bg_4, R.drawable.course_bg_5, R.drawable.course_bg_6,
            R.drawable.course_bg_7, R.drawable.course_bg_8};

    public TimetableAdapter(Context context) {
        this.mContext = context;
    }

    public int getCount() {
        return positionTotal;
    }

    public long getItemId(int position) {
        return position;
    }

    public Objects getItem(int position) {
        return null;
    }

    @SuppressLint("InflateParams")
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.course_item, null);

            holder.courseItem = (TextView) convertView.findViewById(R.id.text);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        // 求余得到二维索引
        int column = position % columnTotal;
        // 求商得到二维索引
        int row = position / columnTotal;

        // 如果有课,那么添加数据
        if (courseInfo[row][column] != null) {
            holder.courseItem.setText(courseInfo[row][column].getName());
            holder.courseItem.setBackground(mContext.getResources().getDrawable(bgRes[position % columnTotal]));
        } else {
            holder.courseItem.setText("");
            holder.courseItem.setBackground(null);
        }

        return convertView;
    }

    /**
     * 设置内容、行数、列数
     */
    public void setContent(CourseBean[][] courseInfo, int row, int column) {
        this.courseInfo = courseInfo;
        this.rowTotal = row;
        this.columnTotal = column;
        positionTotal = rowTotal * columnTotal;
    }

    class ViewHolder {
        TextView courseItem;        // 课程
    }

}
