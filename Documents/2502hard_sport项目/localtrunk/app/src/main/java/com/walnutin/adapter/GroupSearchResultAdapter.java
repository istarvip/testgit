package com.walnutin.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.walnutin.entity.GroupSearchInfo;
import com.walnutin.hard.R;
import com.walnutin.util.BitmapUtil;

import java.util.List;

/**
 * 作者：MrJiang on 2016/5/12 17:07
 */
public class GroupSearchResultAdapter extends BaseAdapter {
    private Context mContext;

    List<GroupSearchInfo> groupList;

    public GroupSearchResultAdapter(Context context, List<GroupSearchInfo> groupList) {
        mContext = context;
        //  groupList = s;
        this.groupList = groupList;
    }

    public void setGroupList(List<GroupSearchInfo> rankLists) {
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
        GroupSearchInfo rankList = groupList.get(position);
        GroupUtil groupUtil = null;

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.activity_groupsearitem, null);
            groupUtil = new GroupUtil();
            groupUtil.group_img = (ImageView) convertView.findViewById(R.id.imgGroup);
            groupUtil.groupName = (TextView) convertView.findViewById(R.id.groud_nickname);
            groupUtil.groupNum = (TextView) convertView.findViewById(R.id.groupNum);

            convertView.setTag(groupUtil);
        } else {
            groupUtil = (GroupUtil) convertView.getTag();
        }
        groupUtil.groupName.setText(rankList.getGroupName());
        groupUtil.groupNum.setText(String.valueOf(rankList.getNumber())+"人");
        BitmapUtil.loadBitmap(mContext, rankList.getHeadimage(), R.drawable.head_image, R.drawable.head_image, groupUtil.group_img);
        return convertView;
    }

    public class GroupUtil {
        ImageView group_img;
        TextView groupName;
        TextView groupNum;


    }
}
