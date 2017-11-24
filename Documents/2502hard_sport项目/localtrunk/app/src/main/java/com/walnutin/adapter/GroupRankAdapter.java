package com.walnutin.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.walnutin.entity.RankList;
import com.walnutin.hard.R;
import com.walnutin.util.BitmapUtil;
import com.walnutin.view.CircleImageView;

import java.util.List;

/**
 * 作者：MrJiang on 2016/5/12 17:07
 */
public class GroupRankAdapter extends BaseAdapter {
    private Context mContext;

    List<RankList> groupList;

    public GroupRankAdapter(Context context, List<RankList> mgroupList) {
        mContext = context;
        //  groupList = s;
        // groupList.clear();
        groupList = mgroupList;
        //groupList.addAll(groupList);

    }

    public void setGroupList(List<RankList> rankLists) {
        // groupList = rankLists;
//        System.out.println(rankLists.size());
//        groupList.clear();
//        int size = rankLists.size();
//        for (int i = 0;i< size ; i++) {
//
//            groupList.add(rankLists.get(i));
//        }
        groupList = rankLists;

        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return groupList.size();
    }

    @Override
    public Object getItem(int position) {
        return groupList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        RankList rankList = groupList.get(position);
        GroupUtil groupUtil = null;

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.group_rank_item, null);
            groupUtil = new GroupUtil();
            groupUtil.group_img = (CircleImageView) convertView.findViewById(R.id.avatar);
            groupUtil.rankNum = (TextView) convertView.findViewById(R.id.firstRank);
            groupUtil.username = (TextView) convertView.findViewById(R.id.group_rank_name);
            groupUtil.userstep = (TextView) convertView.findViewById(R.id.group_rank_tstep);
            convertView.setTag(groupUtil);
        } else {
            groupUtil = (GroupUtil) convertView.getTag();
        }

        if (position == 0) {
            groupUtil.rankNum.setBackgroundResource(R.drawable.firstprize);

            groupUtil.rankNum.setText("");
        } else if (position == 1) {
            //      groupUtil.group_img.setBackgroundResource(R.drawable.sec);
            groupUtil.rankNum.setBackgroundResource(R.drawable.twoprize);
            groupUtil.rankNum.setText("");

        } else if (position == 2) {
            groupUtil.rankNum.setBackgroundResource(R.drawable.thirdprize);
            groupUtil.rankNum.setText("");
        } else {
            groupUtil.rankNum.setBackgroundColor(mContext.getResources().getColor(R.color.white));
            groupUtil.rankNum.setText(String.valueOf(position+1));
        }
        groupUtil.userstep.setText(String.valueOf(rankList.getStepnumber()));
        groupUtil.username.setText(rankList.getNickname());
        //  groupUtil.group_img.setBackgroundResource(String.valueOf(rankList.getStepnumber()));
        BitmapUtil.loadBitmap(mContext, rankList.getHeadimage(), R.drawable.head_image, R.drawable.head_image, groupUtil.group_img);
        return convertView;
    }

    public class GroupUtil {

        CircleImageView group_img;
        TextView rankNum;
        TextView username;
        TextView userstep;


    }
}
