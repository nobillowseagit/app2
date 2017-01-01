package com.demo.floatwindowdemo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by lyt on 2016/12/31.
 */

public class DemoGridAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private List<PakageMod> datas;

    public DemoGridAdapter(Context context, List<PakageMod> datas) {
        super();
        inflater = LayoutInflater.from(context);
        this.datas = datas;
    }

    @Override
    public int getCount() {
        return datas.size();
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
        ViewHolder holder;
        if (convertView == null) {
            // 使用View的对象itemView与R.layout.item关联
            convertView = inflater.inflate(R.layout.app, null);
            holder = new ViewHolder();
            holder.icon = (ImageView) convertView.findViewById(R.id.apps_image);
            holder.label = (TextView) convertView
                    .findViewById(R.id.apps_textview);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.icon.setImageDrawable(datas.get(position).icon);
        holder.label.setText(datas.get(position).appName);

        return convertView;

    }

    class ViewHolder {
        private ImageView icon;
        private TextView label;
    }
}