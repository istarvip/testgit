package com.walnutin.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter;
import com.daimajia.swipe.implments.SwipeItemRecyclerMangerImpl;
import com.walnutin.activity.GroupAllMemberActivity;
import com.walnutin.activity.GroupRankActivity;
import com.walnutin.activity.MyApplication;
import com.walnutin.entity.RankList;
import com.walnutin.entity.TestInfo;
import com.walnutin.entity.UserBean;
import com.walnutin.hard.R;
import com.walnutin.manager.GroupManager;
import com.walnutin.util.BitmapUtil;
import com.walnutin.view.CircleImageView;
import com.walnutin.view.ConfirmDialog;

import java.util.List;

/**
 * Created by chenliu on 16/6/7.
 */
public class GroupMemberRecyclerAdapter extends RecyclerSwipeAdapter<GroupMemberRecyclerAdapter.MyHolder> {

    GroupManager groupManager;
    private GroupAllMemberActivity mContext;
    private List<UserBean> mList;
    protected SwipeItemRecyclerMangerImpl mItemManger;

    public interface  IDeleteItem{
        public void deleteItem(int position);
    }

    public IDeleteItem iDeleteItem;
    public void setOnItemDelete(IDeleteItem itemDelete){
        this.iDeleteItem = itemDelete;
    }

    public GroupMemberRecyclerAdapter(GroupAllMemberActivity groupRankActivity, List<UserBean> rankWeekLists) {
        mContext = groupRankActivity;
        mList = rankWeekLists;
        mItemManger = new SwipeItemRecyclerMangerImpl(this);
        groupManager = GroupManager.getInstance(mContext);
    }

    public void setRankListData(List<UserBean> listData) {
        mList = listData;
        notifyDataSetChanged();
    }

    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View convertView = inflater.inflate(R.layout.group_member_item, null, false);
        MyHolder holder = new MyHolder(convertView);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyHolder viewHolder, final int position) {
        final MyHolder myHolder = (MyHolder) viewHolder;
        UserBean rankList = mList.get(position);
        viewHolder.swipeLayout.setShowMode(SwipeLayout.ShowMode.LayDown);
        viewHolder.buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mList.get(position).getAccount().equals(MyApplication.account)) {
                    Toast.makeText(mContext, "不能移除自己", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(iDeleteItem !=null){
                    iDeleteItem.deleteItem(position);
                }
                mItemManger.removeShownLayouts(myHolder.swipeLayout);
                mList.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, mList.size());
                mItemManger.closeAllItems();
        //        groupManager.addDeletedUser(mList.get(position));
                //       Toast.makeText(view.getContext(), "Deleted " + viewHolder.textViewData.getText().toString() + "!", Toast.LENGTH_SHORT).show();
            }
        });
        viewHolder.username.setText(rankList.getNickname());
        BitmapUtil.loadBitmap(mContext, rankList.getHeadimage(), R.drawable.head_image, R.drawable.head_image, viewHolder.group_img);
        mItemManger.bindView(viewHolder.itemView, position);
//        mItemManger.b
    }


    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.swipe;
    }


    public static class MyHolder extends RecyclerView.ViewHolder {
        SwipeLayout swipeLayout;
        CircleImageView group_img;
        TextView username;
        TextView buttonDelete;

        public MyHolder(View itemView) {
            super(itemView);
            swipeLayout = (SwipeLayout) itemView.findViewById(R.id.swipe);
            group_img = (CircleImageView) itemView.findViewById(R.id.avatar);
            username = (TextView) itemView.findViewById(R.id.text_data);
            buttonDelete = (TextView) itemView.findViewById(R.id.delete);
        }
    }

}
