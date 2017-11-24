package com.walnutin.http;

import com.walnutin.eventbus.CommomStepUpLoader;
import com.walnutin.eventbus.CommonAvater;
import com.walnutin.eventbus.CommonBindThirdInfoResult;
import com.walnutin.eventbus.CommonGroupResult;
import com.walnutin.eventbus.CommonRegisterResult;
import com.walnutin.eventbus.CommonResetPwdResult;
import com.walnutin.eventbus.CommonUserAvaterResult;
import com.walnutin.eventbus.CommonUserResult;
import com.walnutin.eventbus.CommonUserUpdataResult;
import com.walnutin.eventbus.CommonloginResult;
import com.walnutin.eventbus.DailyStepListResult;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by chenliu on 16/5/24.
 */
public class HttpInterface {

    /**
     * 用来处理用户相关的请求
     */
    public interface IHttpUserBiz {
        //登录
        @POST("user/login")
        @FormUrlEncoded
        Call<CommonloginResult> commonlogin(@Field("account") String username, @Field("password") String password);

        //第三方登录
        @POST("user/third")
        @FormUrlEncoded
        Call<CommonloginResult> commonThirdlogin(@Field("third") String third);


        //注册
        @POST("user/result")
        @FormUrlEncoded
        Call<CommonRegisterResult> commonRegister(@Field("account") String username, @Field("password") String password);

        //重置密码
        @POST("user/RePassword")
        @FormUrlEncoded
        Call<CommonResetPwdResult> commonResetPwd(@Field("account") String username, @Field("password") String password);

        //更新用户数据
        @POST("user/uredate")
        @FormUrlEncoded
        Call<CommonUserUpdataResult> commonUserUpdata(@Field("user") String userBeanJson, @Field("token") String token);

        //更新用户绑定数据
        @POST("user/openi")
        @FormUrlEncoded
        Call<CommonUserResult.CommonAddBindThirdResult> addBindThirdAccount(@Field("token") String token, @Field("json") String bind);

        //增加用户手机号绑定数据
        @POST("user/openi")
        @FormUrlEncoded
        Call<CommonUserResult.CommonAddBindPhoneResult> addBindPhoneAccount(@Field("token") String token, @Field("pwd") String pwd, @Field("json") String bind);

        //解除用户第三方绑定数据
        @POST("user/opend")
        @FormUrlEncoded
        Call<CommonUserResult.CommonUnBindThirdResult> unBindThirdAccount(@Field("token") String token, @Field("json") String bind);

        //更改用户绑定的手机
        @POST("user/openu")
        @FormUrlEncoded
        Call<CommonUserResult.CommonChangeBindPhoneResult> changeBindPhone(@Field("account") String account,
                                                                           @Field("token") String token,
                                                                           @Field("openid") String openid,
                                                                           @Field("newOpenid") String newOpenid,
                                                                           @Field("pwd") String pwd);

        //更新用户头像
        @POST("user/headupload")
        @FormUrlEncoded
        Call<CommonUserAvaterResult> commonUserHeadInfo(@Field("photo") String headimage,
                                                        @Field("account") String account,
                                                        @Field("token") String token);

        //查询用户第三方绑定相关
        @POST("user/opens")
        @FormUrlEncoded
        Call<CommonBindThirdInfoResult> queryThirdBindInfo(@Field("account") String account,
                                                           @Field("token") String token);

       //查询用户第三方绑定相关
        @POST("user/thirdUser")
        @FormUrlEncoded
        Call<CommonUserResult.CommomGetAccountAndPwdResult> queryAccountAndPwd(@Field("account") String account,
                                                           @Field("token") String token);

        //获取用户头像
        @POST("user/headimage")
        @FormUrlEncoded
        Call<CommonAvater> getUserAvater(@Field("account") String account);

        @POST("step/timeStep")
        @FormUrlEncoded
        Call<DailyStepListResult> getStepList(@Field("statetime") String starttime,
                                              @Field("endtime") String endtime,
                                              @Field("time") String timeFlag,
                                              @Field("account") String account,
                                              @Field("token") String token);

        @POST("step/SNumberSteps")
        @FormUrlEncoded
        Call<CommomStepUpLoader> upLoadStepInfo(@Field("step") String step, @Field("token") String token);

        @POST("step/sNumberStepss")
        @FormUrlEncoded
        Call<CommomStepUpLoader> upLoadStepListInfo(@Field("step") String step, @Field("token") String token);

    }


    public interface IGroupService {
        // 创建群组
        @POST("group/addPersonal")
        @FormUrlEncoded
        Call<CommonGroupResult.AddGroupResult> addPersonalGroup(@Field("groupi") String groupInfo, @Field("token") String token);

        @POST("group/addCompany")
        @FormUrlEncoded
        Call<CommonGroupResult.AddGroupResult> createCompanyGroup(@Field("groupc") String groupInfo, @Field("token") String token);

        @POST("group/qunmumber")
        @FormUrlEncoded
        Call<CommonGroupResult.GetPersonalRank> getPersonalGroupRank(@Field("account") String groupInfo,
                                                                     @Field("date") String DayType,
                                                                     @Field("begin") int begin,
                                                                     @Field("end") int end,
                                                                     @Field("groupid") int groupid,
                                                                     @Field("type") int type,
                                                                     @Field("token") String token);

        @POST("group/groupuser")
        @FormUrlEncoded
        Call<CommonGroupResult.FragGroupList> getUserGroupList(@Field("account") String groupInfo,
                                                               @Field("begin") int begin,
                                                               @Field("end") int end,
                                                               @Field("token") String token);


        @POST("group/inquire")
        @FormUrlEncoded
        Call<CommonGroupResult.SearchGroupResult> searchGroup(@Field("condition") String groupInfo);

        //根据群号显示群信息
        @POST("group/sgroup")
        @FormUrlEncoded
        Call<CommonGroupResult.GetGroupDetailResult> getGroupAllInfoByGroupId(@Field("account") String account,
                                                                              @Field("token") String token,
                                                                              @Field("groupid") int groupid,
                                                                              @Field("type") int type);

        //根据群号显示群成员
        @POST("group/member")
        @FormUrlEncoded
        Call<CommonGroupResult.GetGroupMemberResult> getGroupMemberByGroupId(@Field("account") String account,
                                                                             @Field("token") String token,
                                                                             @Field("groupid") int groupid,
                                                                             @Field("type") int type);

        //加入群组 需验证
        @POST("group/addgroup")
        @FormUrlEncoded
        Call<CommonGroupResult.AddGroupNeedVerifyResult> addGroup(@Field("account") String groupInfo,
                                                                  @Field("verify") String v,
                                                                  @Field("type") int type,
                                                                  @Field("group_id") int groupid,
                                                                  @Field("token") String token);

        //加入群组 不需验证
        @POST("group/addgroup")
        @FormUrlEncoded
        Call<CommonGroupResult.AddGroupNoVerifyResult> addGroupNoVerify(@Field("account") String groupInfo,
                                                                        @Field("type") int type,
                                                                        @Field("group_id") int groupid,
                                                                        @Field("token") String token);


        //企业群设置
        @POST("group/setC")
        @FormUrlEncoded
        Call<CommonGroupResult.GroupSettingResult> getCompanyGroupSettinInfo(@Field("account") String groupInfo,
                                                                             @Field("token") String token,
                                                                             @Field("groupid") int groupid);


        //个人群设置
        @POST("group/setI")
        @FormUrlEncoded
        Call<CommonGroupResult.GroupSettingResult> getPersonalGroupSettinInfo(@Field("account") String groupInfo,
                                                                              @Field("token") String token,
                                                                              @Field("groupid") int groupid);


        //t退出群组
        @POST("group/back")
        @FormUrlEncoded
        Call<CommonGroupResult.ExitGroupResult> exitGroup(@Field("account") String account,
                                                          @Field("token") String token,
                                                          @Field("groupid") int groupid,
                                                          @Field("type") int type);


        //转让群组
        @POST("group/transfer")
        @FormUrlEncoded
        Call<CommonGroupResult.TransferGroupResult> transferGroup(@Field("account") String account,
                                                              @Field("token") String token,
                                                              @Field("groupid") int groupid,
                                                              @Field("type") int type,
                                                              @Field("nwaccount") String nwaccount);
        //删除群成员
        @POST("group/kick")
        @FormUrlEncoded
        Call<CommonGroupResult.KickGroupMemberResult> kickMember(@Field("account") String account,
                                                              @Field("token") String token,
                                                              @Field("groupid") int groupid,
                                                              @Field("type") int type,
                                                              @Field("groupAccount") String nwaccount);

        //修改个人群信息
        @POST("group/editorI")
        @FormUrlEncoded
        Call<CommonGroupResult.SetGroupResult> setPersonalGroupInfo(@Field("token") String token,
                                                                  @Field("groupI") String groupInfo);

      //修改企业群信息
        @POST("group/editorC")
        @FormUrlEncoded
        Call<CommonGroupResult.SetGroupResult> setCompanyGroupInfo(@Field("token") String token,
                                                              @Field("groupC") String groupInfo);


    }


}
