package cn.mkblog.huashelper.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.LinkedList;

import cn.mkblog.huashelper.bean.SunList;
import cn.mkblog.huashelper.R;

/**
 * 阳光服务列表
 */
public class SunAdapter extends BaseAdapter {
    private LinkedList<SunList> sunData;
    private Context mContext;

    public SunAdapter(LinkedList<SunList> sunData, Context mContext) {
        this.sunData = sunData;
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        return sunData.size();
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
        SunList data = sunData.get(position);
        ViewHolder holder;

        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.list_sun, parent, false);

            holder.sunTitle = (TextView) convertView.findViewById(R.id.sun_title);
            holder.sunTypes = (TextView) convertView.findViewById(R.id.sun_types);
            holder.sunStatus = (ImageView) convertView.findViewById(R.id.sun_status);

            data.setTypes("[" + data.getTypes() + "]");

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.sunTitle.setText(data.getTitle());
        holder.sunTypes.setText(data.getTypes());

        if (data.getStatus().equals("已办结")) {
            holder.sunStatus.setImageResource(R.drawable.dot_green);
        } else {
            holder.sunStatus.setImageResource(R.drawable.dot_red);
        }

        return convertView;
    }

    class ViewHolder {
        TextView sunTitle;        // 新闻标题
        TextView sunTypes;         // 新闻名字
        ImageView sunStatus;         // 新闻名字
    }
}
