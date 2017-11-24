package com.walnutin.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.walnutin.activity.AddGroupInfoActivity;
import com.walnutin.hard.R;
import com.walnutin.util.BitmapUtil;
import com.walnutin.view.CircleImageView;

import java.util.ArrayList;
import java.util.List;

public class IconAdapter extends BaseAdapter {
    List<String> iconList = new ArrayList<>();
    Context context;

    public IconAdapter(Context contexts) {
        super();
        context = contexts;
    }

    public void setIconList(List<String> list) {
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
            convertView = LayoutInflater.from(context).inflate(R.layout.group_gridviewitem, null);
            holder = new Holder();
            holder.mCircleImageView = (CircleImageView) convertView.findViewById(R.id.Im_portrait);
            convertView.setTag(holder);

        } else {
            holder = (Holder) convertView.getTag();
        }


        BitmapUtil.loadBitmap(context, iconList.get(position), R.drawable.head_image,
                R.drawable.head_image, holder.mCircleImageView);
        return convertView;
    }

    class Holder {
        CircleImageView mCircleImageView;
    }
}

