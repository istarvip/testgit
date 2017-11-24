package com.walnutin.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.walnutin.entity.NoticeDevice;
import com.walnutin.hard.R;
import com.walnutin.util.BitmapUtil;
import com.walnutin.util.Conversion;
import com.walnutin.view.CircleImageView;

import java.util.ArrayList;
import java.util.List;

public class AppListAdapter extends BaseAdapter {
    List<NoticeDevice> iconList = new ArrayList<>();
    Context context;

    public AppListAdapter(Context contexts) {
        super();
        context = contexts;
    }

    public void setIconList(List<NoticeDevice> list) {
        iconList = list;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return iconList.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.app_item, null);
            holder = new Holder();
            holder.mCircleImageView = (ImageView) convertView.findViewById(R.id.avatar);
            holder.txtLable = (TextView) convertView.findViewById(R.id.text_data);
            convertView.setTag(holder);

        } else {
            holder = (Holder) convertView.getTag();
        }

        NoticeDevice noticeDevice = iconList.get(position);
        holder.txtLable.setText(noticeDevice.appName);
        holder.mCircleImageView.setImageDrawable(Conversion.byteToDrawable(noticeDevice.appAvator));

        return convertView;
    }

    class Holder {
        ImageView mCircleImageView;
        TextView txtLable;
    }
}

