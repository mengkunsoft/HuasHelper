package cn.mkblog.huashelper.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.LinkedList;

import cn.mkblog.huashelper.bean.News;
import cn.mkblog.huashelper.R;

/**
 * 新闻列表
 */

public class NewsAdapter extends BaseAdapter {
    private LinkedList<News> mNews;
    private Context mContext;

    public NewsAdapter(LinkedList<News> mNews, Context mContext) {
        this.mNews = mNews;
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        return mNews.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        News news = mNews.get(position);
        ViewHolder holder;

        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.list_news, parent, false);

            holder.newsTitle = (TextView) convertView.findViewById(R.id.news_title);
            holder.newsTime = (TextView) convertView.findViewById(R.id.news_time);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.newsTitle.setText(news.getnTitle());
        holder.newsTime.setText(news.getnTime());

        return convertView;
    }

    class ViewHolder {
        TextView newsTitle;        // 新闻标题
        TextView newsTime;         // 新闻名字
    }
}
