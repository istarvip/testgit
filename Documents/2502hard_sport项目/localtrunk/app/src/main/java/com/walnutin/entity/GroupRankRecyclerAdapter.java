package com.walnutin.entity;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.walnutin.activity.GroupRankActivity;
import com.walnutin.hard.R;
import com.walnutin.util.BitmapUtil;
import com.walnutin.view.CircleImageView;

import java.util.List;

/**
 * Created by chenliu on 16/6/7.
 */
public class GroupRankRecyclerAdapter extends RecyclerView.Adapter {

    //上拉加载更多
    public static final int  PULLUP_LOAD_MORE=0;
    //正在加载中
    public static final int  LOADING_MORE=1;
    public static final int  NO_MORE_DATA=2;
    //上拉加载更多状态-默认为0
    private int load_more_status=0;
    private LayoutInflater inflater;
    private Context mContext;
    private List<RankList> mList;
    private static final int TYPE_ITEM =0;  //普通Item View
    private static final int TYPE_FOOTER = 1;  //底部FootView

    public GroupRankRecyclerAdapter(GroupRankActivity groupRankActivity, List<RankList> rankWeekLists) {
        mContext = groupRankActivity;
        mList = rankWeekLists;
        inflater = LayoutInflater.from(mContext);
    }

    public void setRankListData(List<RankList>listData){
        mList = listData;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if (position + 1 == getItemCount()) {
            return TYPE_FOOTER;
        } else {
            return TYPE_ITEM;
        }
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


     //   LayoutInflater inflater = LayoutInflater.from(mContext);
        if(viewType == TYPE_ITEM) {
            View convertView = inflater.inflate(R.layout.group_rank_item, null, false);
            MyHolder holder = new MyHolder(convertView);
            return holder;
        }else if(viewType == TYPE_FOOTER){
            View foot_view=inflater.inflate(R.layout.recycler_load_more_layout,parent,false);
            //这边可以做一些属性设置，甚至事件监听绑定
            //view.setBackgroundColor(Color.RED);
            FootViewHolder footViewHolder=new FootViewHolder(foot_view);
            return footViewHolder;
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if(holder instanceof MyHolder) {
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
        }else if(holder instanceof FootViewHolder){
            FootViewHolder footViewHolder=(FootViewHolder)holder;
            if(position <=18) {
                footViewHolder.foot_view_item_tv.setVisibility(View.GONE);
                footViewHolder.progressBar.setVisibility(View.GONE);
                return;
            }else{
                footViewHolder.foot_view_item_tv.setVisibility(View.VISIBLE);
            }

            switch (load_more_status){
                case PULLUP_LOAD_MORE:
                    footViewHolder.foot_view_item_tv.setText("上拉加载更多...");
                    footViewHolder.progressBar.setVisibility(View.GONE);
                    break;
                case LOADING_MORE:
                    footViewHolder.foot_view_item_tv.setText("正在加载更多数据...");
                    footViewHolder.progressBar.setVisibility(View.VISIBLE);
                    break;
                case NO_MORE_DATA:
                    footViewHolder.foot_view_item_tv.setText("没有更多数据了...");
                    footViewHolder.progressBar.setVisibility(View.GONE);

            }
        }

    }

    public void addMoreItem(List<RankList> newDatas) {
        mList.addAll(newDatas);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mList.size()+1;
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

    public static class FootViewHolder extends  RecyclerView.ViewHolder{
        private TextView foot_view_item_tv;
        private ProgressBar progressBar;
        public FootViewHolder(View view) {
            super(view);
            foot_view_item_tv=(TextView)view.findViewById(R.id.foot_view_item_tv);
            progressBar= (ProgressBar) view.findViewById(R.id.progress);
        }
    }

    /**
     * //上拉加载更多
     * PULLUP_LOAD_MORE=0;
     *  //正在加载中
     * LOADING_MORE=1;
     * //加载完成已经没有更多数据了
     * NO_MORE_DATA=2;
     * @paramstatus
     */

    public int getMoreDataState(){
        return load_more_status;
    }

    public void changeMoreStatus(int status){
        load_more_status=status;
        notifyDataSetChanged();
    }
}
