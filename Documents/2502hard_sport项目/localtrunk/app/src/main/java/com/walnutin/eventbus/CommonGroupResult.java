package com.walnutin.eventbus;

import com.walnutin.entity.FragGroupInfo;
import com.walnutin.entity.GroupDetailInfo;
import com.walnutin.entity.GroupInfo;
import com.walnutin.entity.GroupSearchInfo;
import com.walnutin.entity.RankList;
import com.walnutin.entity.UserBean;

import java.util.List;

/**
 * 作者：MrJiang on 2016/6/3 11:44
 */
public class CommonGroupResult {

    public static class AddGroupResult {
        public int state;
        public String msg;

        public AddGroupResult(int s, String m) {
            state = s;
            msg = m;
        }
    }

    public static class ExitGroupResult {
        public int state;
        public String msg;

        public ExitGroupResult(int s, String m) {
            state = s;
            msg = m;
        }
    }

    public static class KickGroupMemberResult {
        public int state;
        public String msg;

        public KickGroupMemberResult(int s, String m) {
            state = s;
            msg = m;
        }
    }

    public static class TransferGroupResult {
        public int state;
        public String msg;

        public TransferGroupResult(int s, String m) {
            state = s;
            msg = m;
        }
    }

    public static class SetGroupResult {
        public int state;
        public String msg;

        public SetGroupResult(int s, String m) {
            state = s;
            msg = m;
        }
    }


    public static class GetPersonalRank {
        public int state;
        public String msg;
        public List<RankList> ranking;

        public GetPersonalRank(int s, String m) {
            state = s;
            msg = m;
        }

        public int getState() {
            return state;
        }

        public void setState(int state) {
            this.state = state;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        public List<RankList> getRanking() {
            return ranking;
        }

        public void setRanking(List<RankList> ranking) {
            this.ranking = ranking;
        }
    }


    public static class FragGroupList {
        public int state;
        public String msg;
        public List<FragGroupInfo> groupuser;

        public FragGroupList(int s, String m) {
            state = s;
            msg = m;
        }

        public int getState() {
            return state;
        }

        public void setState(int state) {
            this.state = state;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        public List<FragGroupInfo> getGroupuser() {
            return groupuser;
        }

        public void setGroupuser(List<FragGroupInfo> groupuser) {
            this.groupuser = groupuser;
        }
    }

    public static class NoticeUpdate {
        public boolean isUpdate = false;

        public NoticeUpdate(boolean isUp) {
            isUpdate = isUp;
        }
    }

    public static class SearchGroupResult {
        public int state;
        public String msg;
        public List<GroupSearchInfo> groups;

        public SearchGroupResult(int s, String m) {
            state = s;
            msg = m;
        }

        public int getState() {
            return state;
        }

        public void setState(int state) {
            this.state = state;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        public List<GroupSearchInfo> getGroups() {
            return groups;
        }

        public void setGroups(List<GroupSearchInfo> groups) {
            this.groups = groups;
        }

    }


    public static class GetGroupDetailResult {
        public int state;
        public String msg;
        public GroupDetailInfo group;

        public GetGroupDetailResult(int s, String m) {
            state = s;
            msg = m;
        }
    }

    public static class GetGroupMemberResult {
        public int state;
        public String msg;
        public List<UserBean> group;

        public GetGroupMemberResult(int s, String m) {
            state = s;
            msg = m;
        }
    }

    public static class AddGroupNoVerifyResult {
        public int state;
        public String msg;

        public AddGroupNoVerifyResult(int s, String m) {
            state = s;
            msg = m;
        }
    }
    public static class AddGroupNeedVerifyResult {
        public int state;
        public String msg;

        public AddGroupNeedVerifyResult(int s, String m) {
            state = s;
            msg = m;
        }
    }

    public static class GroupSettingResult{
        public int state;
        public String msg;
        public GroupInfo groupI;

        public GroupSettingResult(int s, String m) {
            state = s;
            msg = m;
        }
    }

}
