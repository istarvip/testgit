package com.walnutin.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.daimajia.swipe.SwipeLayout;
import com.walnutin.entity.UserBean;
import com.walnutin.hard.R;
import com.walnutin.util.BitmapUtil;
import com.walnutin.view.CircleImageView;

import java.util.List;

/**
 * Created by chenliu on 16/6/7.
 */
public class GroupTransferAdapter extends BaseAdapter {


    private Context mContext;
    private List<UserBean> mList;


    @Override
    public int getCount() {
        return mList==null?0:mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        UserBean rankList = mList.get(position);
        MyHolder holder = null;
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(R.layout.group_member_transfer_item, null, false);
            holder = new MyHolder();
            holder.group_img = (CircleImageView) convertView.findViewById(R.id.avatar);
            holder.username = (TextView) convertView.findViewById(R.id.text_data);
            convertView.setTag(holder);
        } else {
            holder = (MyHolder) convertView.getTag();
        }
        holder.username.setText(rankList.getNickname());
        BitmapUtil.loadBitmap(mContext, rankList.getHeadimage(), R.drawable.head_image, R.drawable.head_image, holder.group_img);

        return convertView;
    }


    public GroupTransferAdapter(Context groupRankActivity, List<UserBean> rankWeekLists) {
        mContext = groupRankActivity;
        mList = rankWeekLists;
    }

    public void setRankListData(List<UserBean> listData) {
        mList = listData;
        notifyDataSetChanged();
    }


    public  class MyHolder {
        CircleImageView group_img;
        TextView username;

    }

}
