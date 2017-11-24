package com.walnutin.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.walnutin.entity.FragGroupInfo;
import com.walnutin.hard.R;
import com.walnutin.util.BitmapUtil;
import com.walnutin.view.CircleImageView;

import java.util.List;

/**
 * 作者：MrJiang on 2016/5/12 17:07
 */
public class GroupListAdapter extends BaseAdapter {
    private Context mContext;
    List<FragGroupInfo> groupList;

    public GroupListAdapter(Context context, List<FragGroupInfo> s) {
        mContext = context;
        groupList = s;
    }

    public void setGroupList(List<FragGroupInfo> s) {
        groupList = s;
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
        GroupUtil groupUtil = null;
    //    System.out.println("postion:"+position);
       FragGroupInfo groupInfo = groupList.get(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.group_list_item, null);
            groupUtil = new GroupUtil();
            groupUtil.group_list_describle = (TextView) convertView.findViewById(R.id.group_list_describle);
            groupUtil.group_list_groupName = (TextView) convertView.findViewById(R.id.group_list_groupName);
            groupUtil.group_list_type = (TextView) convertView.findViewById(R.id.group_list_type);
            groupUtil.group_list_Num = (TextView) convertView.findViewById(R.id.group_list_Num);
            groupUtil.group_list_img1 = (CircleImageView) convertView.findViewById(R.id.group_list_img1);
            groupUtil.group_list_img2 = (CircleImageView) convertView.findViewById(R.id.group_list_img2);
            groupUtil.group_list_img3 = (CircleImageView) convertView.findViewById(R.id.group_list_img3);
            groupUtil.group_list_img4 = (CircleImageView) convertView.findViewById(R.id.group_list_img4);
            groupUtil.group_list_img5 = (CircleImageView) convertView.findViewById(R.id.group_list_img5);
            convertView.setTag(groupUtil);
        } else {
            groupUtil = (GroupUtil) convertView.getTag();
        }
        int headSize = groupInfo.getHeadimage().size();
        int j = 5-headSize;
            switch (j){
                case 4:
                    groupUtil.group_list_img2.setVisibility(View.INVISIBLE);
                  //  break;
                case 3: groupUtil.group_list_img3.setVisibility(View.INVISIBLE);
                 //   break;
                case 2: groupUtil.group_list_img4.setVisibility(View.INVISIBLE);
                //    break;
                case 1: groupUtil.group_list_img5.setVisibility(View.INVISIBLE);
                //    j =0;
                    break ;
                    //break;
            }

        for (int i =0; i<headSize;i++){
            switch (i){
                case 0:
                    groupUtil.group_list_img1.setVisibility(View.VISIBLE);
                    BitmapUtil.loadBitmap(mContext,groupInfo.getHeadimage().get(i),R.drawable.head_image,R.drawable.head_image,
                            groupUtil.group_list_img1);
                    break;
                case 1:
                    groupUtil.group_list_img2.setVisibility(View.VISIBLE);
                    BitmapUtil.loadBitmap(mContext,groupInfo.getHeadimage().get(i),R.drawable.head_image,R.drawable.head_image,
                        groupUtil.group_list_img2);
                    break;
                case 2:
                    groupUtil.group_list_img3.setVisibility(View.VISIBLE);
                    BitmapUtil.loadBitmap(mContext,groupInfo.getHeadimage().get(i),R.drawable.head_image,R.drawable.head_image,
                            groupUtil.group_list_img3);
                    break;
                case 3:
                    groupUtil.group_list_img4.setVisibility(View.VISIBLE);
                    BitmapUtil.loadBitmap(mContext,groupInfo.getHeadimage().get(i),R.drawable.head_image,R.drawable.head_image,
                        groupUtil.group_list_img4);
                    break;
                case 4:
                    groupUtil.group_list_img5.setVisibility(View.VISIBLE);
                    BitmapUtil.loadBitmap(mContext,groupInfo.getHeadimage().get(i),R.drawable.head_image,R.drawable.head_image,
                            groupUtil.group_list_img5);
                    break;
            }

        }


        if(!TextUtils.isEmpty(groupInfo.getGroupName())){
            groupUtil.group_list_groupName.setText(groupInfo.getGroupName());
        }
        groupUtil.group_list_Num.setText(String.valueOf(groupInfo.getHeadcount())+"人");
        if(!TextUtils.isEmpty(groupInfo.getDescription())){
            groupUtil.group_list_describle.setText(String.valueOf(groupInfo.getDescription()));
        }
        if(groupInfo.getType() ==1) {
            groupUtil.group_list_type.setText("个人");
        }else {
            groupUtil.group_list_type.setText("企业");
        }

        return convertView;
    }

    public class GroupUtil {
        TextView group_list_groupName;
        TextView group_list_Num;
        TextView group_list_type;
        TextView group_list_describle;
        CircleImageView group_list_img1;
        CircleImageView group_list_img2;
        CircleImageView group_list_img3;
        CircleImageView group_list_img4;
        CircleImageView group_list_img5;

    }
}
