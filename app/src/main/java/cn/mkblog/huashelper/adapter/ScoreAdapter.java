package cn.mkblog.huashelper.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.LinkedList;

import cn.mkblog.huashelper.bean.ScoreBean;
import cn.mkblog.huashelper.R;

/**
 * 成绩列表适配器
 */
public class ScoreAdapter extends BaseExpandableListAdapter {
    private LinkedList<ScoreBean.ScoresBean> scoreData;
    private Context mContext;

    public ScoreAdapter(LinkedList<ScoreBean.ScoresBean> scoreData, Context mContext) {
        this.scoreData = scoreData;
        this.mContext = mContext;
    }

    @Override
    public int getGroupCount() {
        return scoreData.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return 1;
    }

    @Override
    public ScoreBean.ScoresBean getGroup(int groupPosition) {
        return scoreData.get(groupPosition);
    }

    @Override
    public ScoreBean.ScoresBean getChild(int groupPosition, int childPosition) {
        return scoreData.get(groupPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    //取得用于显示给定分组的视图. 这个方法仅返回分组的视图对象
    @SuppressLint("SetTextI18n")
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        ViewHolderGroup groupHolder;

        // 初始化每个大类
        if(convertView == null){
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.list_score, parent, false);
            groupHolder = new ViewHolderGroup();

            groupHolder.scoreName = (TextView) convertView.findViewById(R.id.score_name);   // 课程名字
            groupHolder.scoreCount = (TextView) convertView.findViewById(R.id.score_count);   // 课程成绩

            convertView.setTag(groupHolder);
        }else{
            groupHolder = (ViewHolderGroup) convertView.getTag();
        }

        if(scoreData.size() <= groupPosition) {
            Toast.makeText(mContext, "数值越界", Toast.LENGTH_SHORT).show();
            return convertView;
        }

        groupHolder.scoreName.setText(
                scoreData.get(groupPosition).getName() + " [学分:" + scoreData.get(groupPosition).getCredit() + "]"
        );

        // 挂的科目红色显示
        String score = scoreData.get(groupPosition).getScore();
        groupHolder.scoreCount.setTextColor(Color.rgb(71, 182, 2));
        try {
            if(Float.parseFloat(score) < 60) {  // 不出错，说明是数字分数
                groupHolder.scoreCount.setTextColor(Color.rgb(164, 81, 82));
            }
            groupHolder.scoreCount.setText(score + "分");
        } catch(Exception e) {
            if(score.equals("不及格")) {
                groupHolder.scoreCount.setTextColor(Color.rgb(164, 81, 82));
            }
            groupHolder.scoreCount.setText(score);
        }

        return convertView;
    }

    //取得显示给定分组给定子位置的数据用的视图
    @SuppressLint("SetTextI18n")
    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ViewHolderItem itemHolder;
        if(convertView == null){
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.list_score_item, parent, false);
            itemHolder = new ViewHolderItem();

            itemHolder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);

            convertView.setTag(itemHolder);
        }else{
            itemHolder = (ViewHolderItem) convertView.getTag();
        }

        ScoreBean.ScoresBean scoreInfo = scoreData.get(groupPosition);
        itemHolder.tv_name.setText("课程名称：" + scoreInfo.getName() +
                "\n开课学期：" + scoreInfo.getTerm() +
                "\n课程编号：" + scoreInfo.getId() +
                "\n课程成绩：" + scoreInfo.getScore() +
                "\n课程学分：" + scoreInfo.getCredit() +
                "\n课程学时：" + scoreInfo.getPeriod() +
                "\n考核方式：" + scoreInfo.getMethods() +
                "\n课程性质：" + scoreInfo.getProperty() +
                "\n课程类型：" + scoreInfo.getTypes() +
                "\n考试时间：" + scoreInfo.getExamterm() +
                "\n考试性质：" + scoreInfo.getExamtypes()
        );

        return convertView;
    }

    //设置子列表是否可选中
    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }


    private static class ViewHolderGroup{
        private TextView scoreName;
        private TextView scoreCount;
    }

    private static class ViewHolderItem{
        private TextView tv_name;
    }

}
