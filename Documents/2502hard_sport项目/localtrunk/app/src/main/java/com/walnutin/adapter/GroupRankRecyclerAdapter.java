package com.walnutin.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.walnutin.activity.GroupRankActivity;
import com.walnutin.entity.RankList;
import com.walnutin.hard.R;
import com.walnutin.util.BitmapUtil;
import com.walnutin.view.CircleImageView;

import java.util.List;

/**
 * Created by chenliu on 16/6/7.
 */
public class GroupRankRecyclerAdapter extends RecyclerView.Adapter {


    private Context mContext;
    private List<RankList> mList;

    public GroupRankRecyclerAdapter(GroupRankActivity groupRankActivity, List<RankList> rankWeekLists) {
        mContext = groupRankActivity;
        mList = rankWeekLists;
    }

    public void setRankListData(List<RankList>listData){
        mList = listData;
        notifyDataSetChanged();
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View convertView = inflater.inflate(R.layout.group_rank_item, null,false);
        MyHolder holder = new MyHolder(convertView);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        MyHolder myHolder = (MyHolder) holder;
        RankList rankList = mList.get(position);

        if (position == 0) {
            myHolder.rankNum.setBackgroundResource(R.drawable.firstprize);

            myHolder.rankNum.setText("");
        } else if (position == 1) {
            //      groupUtil.group_img.setBackgroundResource(R.drawable.sec);
            myHolder.rankNum.setBackgroundResource(R.drawable.twoprize);
            myHolder.rankNum.setText("");

        } else if (position == 2) {
            myHolder.rankNum.setBackgroundResource(R.drawable.thirdprize);
            myHolder.rankNum.setText("");
        } else {
            myHolder.rankNum.setBackgroundColor(mContext.getResources().getColor(R.color.white));
            myHolder.rankNum.setText(String.valueOf(position + 1));
        }
        myHolder.userstep.setText(String.valueOf(rankList.getStepnumber()));
        myHolder.username.setText(rankList.getNickname());
        //  groupUtil.group_img.setBackgroundResource(String.valueOf(rankList.getStepnumber()));
        //myHolder.group_img.getLayoutParams().height
        BitmapUtil.loadBitmap(mContext, rankList.getHeadimage(), R.drawable.head_image, R.drawable.head_image, myHolder.group_img);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }


    private class MyHolder extends RecyclerView.ViewHolder {
        private CircleImageView group_img;
        private TextView rankNum;
        private TextView username;
        private TextView userstep;

        public MyHolder(View itemView) {
            super(itemView);
            group_img = (CircleImageView) itemView.findViewById(R.id.avatar);
            rankNum = (TextView) itemView.findViewById(R.id.firstRank);
            username = (TextView) itemView.findViewById(R.id.group_rank_name);
            userstep = (TextView) itemView.findViewById(R.id.group_rank_tstep);
        }
    }

}
