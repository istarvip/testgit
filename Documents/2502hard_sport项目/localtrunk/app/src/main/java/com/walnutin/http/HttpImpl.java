package com.walnutin.http;

import android.util.Log;

import com.google.gson.Gson;
import com.walnutin.entity.DailyInfo;
import com.walnutin.entity.GroupInfo;
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
import com.walnutin.activity.MyApplication;
import com.walnutin.entity.UserBean;
import com.walnutin.util.MySharedPf;

import org.greenrobot.eventbus.EventBus;

import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by chenliu on 16/5/24.
 */
public class HttpImpl {


    private static HttpImpl httpUtil = new HttpImpl();
    private String HTTP_CONNECT_FAIL = "连接超时";

    // http://192.168.31.217:8080/User/;
    //2502.walnutin.com
    private static final String BASE_URL = "http://2502.walnutin.com:8080/User/";
//    private static final String BASE_URL = "http://192.168.31.217:8080/User/";
    private Retrofit mRetrofit;

    private HttpImpl() {
//        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
//        .addInterceptor(interceptor)
//                .addNetworkInterceptor(mTokenInterceptor)
//        .retryOnConnectionFailure(true)
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.SECONDS)
                .readTimeout(5, TimeUnit.SECONDS)
                .build();


        mRetrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }


    public static HttpImpl getInstance() {
        return httpUtil;
    }



    private int times = 0;
    public void userLogin(String name, String password) {
        HttpInterface.IHttpUserBiz userBiz = mRetrofit.create(HttpInterface.IHttpUserBiz.class);
        Call<CommonloginResult> call = userBiz.commonlogin(name, password);
        call.enqueue(new Callback<CommonloginResult>() {
            @Override
            public void onResponse(Call<CommonloginResult> call, Response<CommonloginResult> response) {
                if (response != null && response.body() != null) {

                    Log.w("执行登录后服务器返回了", times+++"次");
                    EventBus.getDefault().post(response.body());
                } else {
                    Log.w("执行登录后服务器返回了else", times+++"次");
                    EventBus.getDefault().post(new CommonloginResult(-1, HTTP_CONNECT_FAIL, null));
                }
            }


            @Override
            public void onFailure(Call<CommonloginResult> call, Throwable t) {
                Log.w("执行登录后服务器返回了onfailure", times+++"次");
                EventBus.getDefault().post(new CommonloginResult(-1, HTTP_CONNECT_FAIL, null));
            }
        });
    }

    public void userThirdLogin(String userJson) {
        HttpInterface.IHttpUserBiz userBiz = mRetrofit.create(HttpInterface.IHttpUserBiz.class);
        Call<CommonloginResult> call = userBiz.commonThirdlogin(userJson);
        call.enqueue(new Callback<CommonloginResult>() {
            @Override
            public void onResponse(Call<CommonloginResult> call, Response<CommonloginResult> response) {
                if (response != null && response.body() != null) {
                    EventBus.getDefault().post(response.body());
                }else{
                    EventBus.getDefault().post(new CommonloginResult(-1, HTTP_CONNECT_FAIL, null));

                }
            }

            @Override
            public void onFailure(Call<CommonloginResult> call, Throwable t) {

                EventBus.getDefault().post(new CommonloginResult(-1, HTTP_CONNECT_FAIL, null));
            }
        });
    }


    public void userRegister(String name, String password) {
        HttpInterface.IHttpUserBiz userBiz = mRetrofit.create(HttpInterface.IHttpUserBiz.class);
        Call<CommonRegisterResult> call = userBiz.commonRegister(name, password);
        call.enqueue(new Callback<CommonRegisterResult>() {
            @Override
            public void onResponse(Call<CommonRegisterResult> call, Response<CommonRegisterResult> response) {
                if (response != null && response.body() != null) {
                    EventBus.getDefault().post(response.body());
                }else{
                    EventBus.getDefault().post(new CommonRegisterResult(-1, HTTP_CONNECT_FAIL));

                }
            }


            @Override
            public void onFailure(Call<CommonRegisterResult> call, Throwable t) {
                EventBus.getDefault().post(new CommonRegisterResult(-1, HTTP_CONNECT_FAIL));
            }
        });

    }

    public void userResetPwd(String name, String password) {

        HttpInterface.IHttpUserBiz userBiz = mRetrofit.create(HttpInterface.IHttpUserBiz.class);
        Call<CommonResetPwdResult> call = userBiz.commonResetPwd(name, password);
        call.enqueue(new Callback<CommonResetPwdResult>() {
            @Override
            public void onResponse(Call<CommonResetPwdResult> call, Response<CommonResetPwdResult> response) {
                if (response != null && response.body() != null) {
                    EventBus.getDefault().post(response.body());
                }else{
                    EventBus.getDefault().post(new CommonResetPwdResult(-1,HTTP_CONNECT_FAIL));

                }
            }

            @Override
            public void onFailure(Call<CommonResetPwdResult> call, Throwable t) {
                EventBus.getDefault().post(new CommonResetPwdResult(-1,HTTP_CONNECT_FAIL));
            }
        });

    }

    public void userUpdata(UserBean user) {
        HttpInterface.IHttpUserBiz userBiz = mRetrofit.create(HttpInterface.IHttpUserBiz.class);
        Gson gson = new Gson();
        Log.w("测试更新个人数据时的token: ", MySharedPf.getInstance(MyApplication.getContext()).getToken());
        Call<CommonUserUpdataResult> call = userBiz.commonUserUpdata(gson.toJson(user), MySharedPf.getInstance(MyApplication.getContext()).getToken());
//        Call<CommonUserUpdataResult> call = userBiz.commonUserUpdata(gson.toJson(user), "33e8e6ec328d43c483b3fb6c7d85fabc");
        call.enqueue(new Callback<CommonUserUpdataResult>() {
            @Override
            public void onResponse(Call<CommonUserUpdataResult> call, Response<CommonUserUpdataResult> response) {
                if (response != null && response.body() != null) {
                    EventBus.getDefault().post(response.body());
                }else{
                    EventBus.getDefault().post(new CommonUserUpdataResult(-1, HTTP_CONNECT_FAIL));

                }
            }

            @Override
            public void onFailure(Call<CommonUserUpdataResult> call, Throwable t) {
                EventBus.getDefault().post(new CommonUserUpdataResult(-1, HTTP_CONNECT_FAIL));
            }
        });
    }

    public void addThidBind(String json) {
        HttpInterface.IHttpUserBiz userBiz = mRetrofit.create(HttpInterface.IHttpUserBiz.class);
        Call<CommonUserResult.CommonAddBindThirdResult> call = userBiz.addBindThirdAccount(MySharedPf.getInstance(MyApplication.getContext()).getToken(), json);
        call.enqueue(new Callback<CommonUserResult.CommonAddBindThirdResult>() {
            @Override
            public void onResponse(Call<CommonUserResult.CommonAddBindThirdResult> call, Response<CommonUserResult.CommonAddBindThirdResult> response) {
                if (response != null && response.body() != null) {
                    EventBus.getDefault().post(response.body());
                }else{
                    EventBus.getDefault().post(new CommonUserUpdataResult(-1, HTTP_CONNECT_FAIL));

                }
            }

            @Override
            public void onFailure(Call<CommonUserResult.CommonAddBindThirdResult> call, Throwable t) {
                EventBus.getDefault().post(new CommonUserUpdataResult(-1, HTTP_CONNECT_FAIL));
            }
        });
    }

    public void addPhoneBind(String pwd, String json) {
        HttpInterface.IHttpUserBiz userBiz = mRetrofit.create(HttpInterface.IHttpUserBiz.class);
        Gson gson = new Gson();
        Call<CommonUserResult.CommonAddBindPhoneResult> call = userBiz.addBindPhoneAccount(MySharedPf.getInstance(MyApplication.getContext()).getToken(), pwd, json);
        call.enqueue(new Callback<CommonUserResult.CommonAddBindPhoneResult>() {
            @Override
            public void onResponse(Call<CommonUserResult.CommonAddBindPhoneResult> call, Response<CommonUserResult.CommonAddBindPhoneResult> response) {
                if (response != null && response.body() != null) {
                    EventBus.getDefault().post(response.body());
                }else{
                    EventBus.getDefault().post(new CommonUserUpdataResult(-1, HTTP_CONNECT_FAIL));

                }
            }

            @Override
            public void onFailure(Call<CommonUserResult.CommonAddBindPhoneResult> call, Throwable t) {
                EventBus.getDefault().post(new CommonUserUpdataResult(-1, HTTP_CONNECT_FAIL));
            }
        });
    }

    public void unBindThird(String json) {
        HttpInterface.IHttpUserBiz userBiz = mRetrofit.create(HttpInterface.IHttpUserBiz.class);
        Gson gson = new Gson();
        Call<CommonUserResult.CommonUnBindThirdResult> call = userBiz.unBindThirdAccount(MySharedPf.getInstance(MyApplication.getContext()).getToken(), json);
        call.enqueue(new Callback<CommonUserResult.CommonUnBindThirdResult>() {
            @Override
            public void onResponse(Call<CommonUserResult.CommonUnBindThirdResult> call, Response<CommonUserResult.CommonUnBindThirdResult> response) {
                if (response != null && response.body() != null) {
                    EventBus.getDefault().post(response.body());
                }else{
                    EventBus.getDefault().post(new CommonUserUpdataResult(-1, HTTP_CONNECT_FAIL));

                }
            }

            @Override
            public void onFailure(Call<CommonUserResult.CommonUnBindThirdResult> call, Throwable t) {
                EventBus.getDefault().post(new CommonUserUpdataResult(-1, HTTP_CONNECT_FAIL));
            }
        });
    }

    public void changeBindedPhone(String oldPhone, String newPhone, String pwd) {
        HttpInterface.IHttpUserBiz userBiz = mRetrofit.create(HttpInterface.IHttpUserBiz.class);
        Gson gson = new Gson();
        Call<CommonUserResult.CommonChangeBindPhoneResult> call = userBiz.changeBindPhone(MyApplication.account,
                MySharedPf.getInstance(MyApplication.getContext()).getToken(), oldPhone, newPhone, pwd);
        call.enqueue(new Callback<CommonUserResult.CommonChangeBindPhoneResult>() {
            @Override
            public void onResponse(Call<CommonUserResult.CommonChangeBindPhoneResult> call, Response<CommonUserResult.CommonChangeBindPhoneResult> response) {
                if (response != null && response.body() != null) {
                    EventBus.getDefault().post(response.body());
                }else{
                    EventBus.getDefault().post(new CommonUserUpdataResult(-1, HTTP_CONNECT_FAIL));

                }
            }

            @Override
            public void onFailure(Call<CommonUserResult.CommonChangeBindPhoneResult> call, Throwable t) {
                EventBus.getDefault().post(new CommonUserUpdataResult(-1, HTTP_CONNECT_FAIL));
            }
        });
    }

    public void userHead(String head) {
        HttpInterface.IHttpUserBiz userBiz = mRetrofit.create(HttpInterface.IHttpUserBiz.class);
        Call<CommonUserAvaterResult> call = userBiz.commonUserHeadInfo(head, MyApplication.account, MySharedPf.getInstance(MyApplication.getContext()).getToken());
        call.enqueue(new Callback<CommonUserAvaterResult>() {
            @Override
            public void onResponse(Call<CommonUserAvaterResult> call, Response<CommonUserAvaterResult> response) {
                if (response != null && response.body() != null) {
                    EventBus.getDefault().post(response.body());
                }else{
                    EventBus.getDefault().post(new CommonUserUpdataResult(-1, HTTP_CONNECT_FAIL));

                }
            }

            @Override
            public void onFailure(Call<CommonUserAvaterResult> call, Throwable t) {
                EventBus.getDefault().post(new CommonUserUpdataResult(-1, HTTP_CONNECT_FAIL));
            }
        });
    }

    public void queryThirdBindInfo() {
        HttpInterface.IHttpUserBiz userBiz = mRetrofit.create(HttpInterface.IHttpUserBiz.class);
        Call<CommonBindThirdInfoResult> call = userBiz.queryThirdBindInfo(MySharedPf.getInstance(MyApplication.instance()).getString("account"),
                MySharedPf.getInstance(MyApplication.getContext()).getToken());
        call.enqueue(new Callback<CommonBindThirdInfoResult>() {
            @Override
            public void onResponse(Call<CommonBindThirdInfoResult> call, Response<CommonBindThirdInfoResult> response) {
                if (response != null && response.body() != null) {
                    EventBus.getDefault().post(response.body());
                }else{
                    EventBus.getDefault().post(new CommonBindThirdInfoResult(-1, HTTP_CONNECT_FAIL));

                }
            }

            @Override
            public void onFailure(Call<CommonBindThirdInfoResult> call, Throwable t) {
                EventBus.getDefault().post(new CommonBindThirdInfoResult(-1, HTTP_CONNECT_FAIL));
            }
        });
    }

    public void queryAccountAndPwd() {
        HttpInterface.IHttpUserBiz userBiz = mRetrofit.create(HttpInterface.IHttpUserBiz.class);
        Call<CommonUserResult.CommomGetAccountAndPwdResult> call = userBiz.queryAccountAndPwd(MySharedPf.getInstance(MyApplication.instance()).getString("account"),
                MySharedPf.getInstance(MyApplication.getContext()).getToken());
        call.enqueue(new Callback<CommonUserResult.CommomGetAccountAndPwdResult>() {
            @Override
            public void onResponse(Call<CommonUserResult.CommomGetAccountAndPwdResult> call, Response<CommonUserResult.CommomGetAccountAndPwdResult> response) {
                if (response != null && response.body() != null) {
                    EventBus.getDefault().post(response.body());
                }else{
                    EventBus.getDefault().post(new CommonUserResult.CommomGetAccountAndPwdResult(-1, HTTP_CONNECT_FAIL));

                }
            }

            @Override
            public void onFailure(Call<CommonUserResult.CommomGetAccountAndPwdResult> call, Throwable t) {
                EventBus.getDefault().post(new CommonUserResult.CommomGetAccountAndPwdResult(-1, HTTP_CONNECT_FAIL));
            }
        });
    }

    public void getUserAvater() {
        HttpInterface.IHttpUserBiz userBiz = mRetrofit.create(HttpInterface.IHttpUserBiz.class);
        Call<CommonAvater> call = userBiz.getUserAvater(MyApplication.account);
        call.enqueue(new Callback<CommonAvater>() {
            @Override
            public void onResponse(Call<CommonAvater> call, Response<CommonAvater> response) {
                if (response != null && response.body() != null) {
                    EventBus.getDefault().post(response.body());
                }else{
                    EventBus.getDefault().post(new CommonAvater(-1, HTTP_CONNECT_FAIL));

                }
            }

            @Override
            public void onFailure(Call<CommonAvater> call, Throwable t) {
                EventBus.getDefault().post(new CommonAvater(-1, HTTP_CONNECT_FAIL));
            }
        });
    }


    public void getStepList(String startTime, String endTime, String timeFlag) {
        HttpInterface.IHttpUserBiz userBiz = mRetrofit.create(HttpInterface.IHttpUserBiz.class);
        Call<DailyStepListResult> call = userBiz.getStepList(startTime, endTime,
                timeFlag, MySharedPf.getInstance(MyApplication.instance()).getString("account"),
                MySharedPf.getInstance(MyApplication.getContext()).getToken());
        call.enqueue(new Callback<DailyStepListResult>() {
            @Override
            public void onResponse(Call<DailyStepListResult> call, Response<DailyStepListResult> response) {
                if (response != null && response.body() != null) {
                    EventBus.getDefault().post(response.body());
                }else{
                    EventBus.getDefault().post(new DailyStepListResult(-1, HTTP_CONNECT_FAIL));

                }
            }

            @Override
            public void onFailure(Call<DailyStepListResult> call, Throwable t) {
                EventBus.getDefault().post(new DailyStepListResult(-1, HTTP_CONNECT_FAIL));
            }
        });
    }


    public void upLoadStep(DailyInfo step) {
        HttpInterface.IHttpUserBiz userBiz = mRetrofit.create(HttpInterface.IHttpUserBiz.class);
        Gson gson = new Gson();
        Call<CommomStepUpLoader> call = userBiz.upLoadStepInfo(gson.toJson(step), MySharedPf.getInstance(MyApplication.getContext()).getToken());
        System.out.println("gson:" + gson.toJson(step));
        call.enqueue(new Callback<CommomStepUpLoader>() {
            @Override
            public void onResponse(Call<CommomStepUpLoader> call, Response<CommomStepUpLoader> response) {
                if (response != null && response.body() != null) {
                    EventBus.getDefault().post(response.body());
                }else{
                    EventBus.getDefault().post(new CommomStepUpLoader(-1, HTTP_CONNECT_FAIL));

                }
            }

            @Override
            public void onFailure(Call<CommomStepUpLoader> call, Throwable t) {
                EventBus.getDefault().post(new CommomStepUpLoader(-1, HTTP_CONNECT_FAIL));
            }
        });
    }

    public void upLoadListStep(List<DailyInfo> step) {
        HttpInterface.IHttpUserBiz userBiz = mRetrofit.create(HttpInterface.IHttpUserBiz.class);
        Gson gson = new Gson();
        Call<CommomStepUpLoader> call = userBiz.upLoadStepListInfo(gson.toJson(step), MySharedPf.getInstance(MyApplication.getContext()).getToken());
        System.out.println("gsonlist:" + gson.toJson(step));
        call.enqueue(new Callback<CommomStepUpLoader>() {
            @Override
            public void onResponse(Call<CommomStepUpLoader> call, Response<CommomStepUpLoader> response) {
                if (response != null && response.body() != null) {
                    EventBus.getDefault().post(response.body());
                }else{
                    EventBus.getDefault().post(new CommomStepUpLoader(-1, HTTP_CONNECT_FAIL));

                }
            }

            @Override
            public void onFailure(Call<CommomStepUpLoader> call, Throwable t) {
                EventBus.getDefault().post(new CommomStepUpLoader(-1, HTTP_CONNECT_FAIL));
            }
        });
    }

    /*
    *
    * 创建群组
    * */

    public void addPersonalGroup(GroupInfo gpInfo) {
        HttpInterface.IGroupService groupService = mRetrofit.create(HttpInterface.IGroupService.class);
        Gson gson = new Gson();
        Call<CommonGroupResult.AddGroupResult> call = groupService.addPersonalGroup(gson.toJson(gpInfo),
                MySharedPf.getInstance(MyApplication.getContext()).getToken());
        call.enqueue(new Callback<CommonGroupResult.AddGroupResult>() {
            @Override
            public void onResponse(Call<CommonGroupResult.AddGroupResult> call, Response<CommonGroupResult.AddGroupResult> response) {
                if (response != null && response.body() != null) {
                    EventBus.getDefault().post(response.body());
                }else{
                    EventBus.getDefault().post(new CommonGroupResult.AddGroupResult(-1, HTTP_CONNECT_FAIL));

                }
            }

            @Override
            public void onFailure(Call<CommonGroupResult.AddGroupResult> call, Throwable t) {
                EventBus.getDefault().post(new CommonGroupResult.AddGroupResult(-1, HTTP_CONNECT_FAIL));
            }
        });
    }

    public void createCompanyGroup(GroupInfo gpInfo) {
        HttpInterface.IGroupService groupService = mRetrofit.create(HttpInterface.IGroupService.class);
        Gson gson = new Gson();
        Call<CommonGroupResult.AddGroupResult> call = groupService.createCompanyGroup(gson.toJson(gpInfo),
                MySharedPf.getInstance(MyApplication.getContext()).getToken());
        call.enqueue(new Callback<CommonGroupResult.AddGroupResult>() {
            @Override
            public void onResponse(Call<CommonGroupResult.AddGroupResult> call, Response<CommonGroupResult.AddGroupResult> response) {
                if (response != null && response.body() != null) {
                    EventBus.getDefault().post(response.body());
                }else{
                    EventBus.getDefault().post(new CommonGroupResult.AddGroupResult(-1, HTTP_CONNECT_FAIL));

                }
            }

            @Override
            public void onFailure(Call<CommonGroupResult.AddGroupResult> call, Throwable t) {
                EventBus.getDefault().post(new CommonGroupResult.AddGroupResult(-1, HTTP_CONNECT_FAIL));
            }
        });
    }

    public void getPersonalGroupRank(String account, String dayType, int begin, int end, int groupId, int type) {
        HttpInterface.IGroupService groupService = mRetrofit.create(HttpInterface.IGroupService.class);
        Call<CommonGroupResult.GetPersonalRank> call = groupService.getPersonalGroupRank(account, dayType, begin,
                end, groupId, type,
                MySharedPf.getInstance(MyApplication.getContext()).getToken());
        call.enqueue(new Callback<CommonGroupResult.GetPersonalRank>() {
            @Override
            public void onResponse(Call<CommonGroupResult.GetPersonalRank> call, Response<CommonGroupResult.GetPersonalRank> response) {
                if (response != null && response.body() != null) {
                    EventBus.getDefault().post(response.body());
                }else {
                    EventBus.getDefault().post(new CommonGroupResult.GetPersonalRank(-1, HTTP_CONNECT_FAIL));

                }
            }

            @Override
            public void onFailure(Call<CommonGroupResult.GetPersonalRank> call, Throwable t) {
                EventBus.getDefault().post(new CommonGroupResult.GetPersonalRank(-1, HTTP_CONNECT_FAIL));
            }
        });
    }

    public void getPersonalGroupList(String account, int pageNum, int end) {
        HttpInterface.IGroupService groupService = mRetrofit.create(HttpInterface.IGroupService.class);
        Call<CommonGroupResult.FragGroupList> call = groupService.getUserGroupList(account, pageNum, end,
                MySharedPf.getInstance(MyApplication.getContext()).getToken());
        call.enqueue(new Callback<CommonGroupResult.FragGroupList>() {
            @Override
            public void onResponse(Call<CommonGroupResult.FragGroupList> call, Response<CommonGroupResult.FragGroupList> response) {
                if (response != null && response.body() != null) {
                    EventBus.getDefault().post(response.body());
                }else {
                    EventBus.getDefault().post(new CommonGroupResult.FragGroupList(-1, HTTP_CONNECT_FAIL));

                }
            }

            @Override
            public void onFailure(Call<CommonGroupResult.FragGroupList> call, Throwable t) {
                EventBus.getDefault().post(new CommonGroupResult.FragGroupList(-1, HTTP_CONNECT_FAIL));
            }
        });
    }

    public void searchGroupList(String condition) {
        HttpInterface.IGroupService groupService = mRetrofit.create(HttpInterface.IGroupService.class);
        Call<CommonGroupResult.SearchGroupResult> call = groupService.searchGroup(condition);
        call.enqueue(new Callback<CommonGroupResult.SearchGroupResult>() {
            @Override
            public void onResponse(Call<CommonGroupResult.SearchGroupResult> call, Response<CommonGroupResult.SearchGroupResult> response) {
                if (response != null && response.body() != null) {
                    EventBus.getDefault().post(response.body());
                }
            }

            @Override
            public void onFailure(Call<CommonGroupResult.SearchGroupResult> call, Throwable t) {
                EventBus.getDefault().post(new CommonGroupResult.SearchGroupResult(-1, HTTP_CONNECT_FAIL));
            }
        });
    }

/*
* 加入群组
*
* */

    public void addGroup(String account, String verify, int type, int groupId) {
        HttpInterface.IGroupService groupService = mRetrofit.create(HttpInterface.IGroupService.class);
        //   Gson gson = new Gson();
        Call<CommonGroupResult.AddGroupNeedVerifyResult> call = groupService.addGroup(account, verify, type, groupId, MySharedPf.getInstance(MyApplication.getContext()).getToken());
        call.enqueue(new Callback<CommonGroupResult.AddGroupNeedVerifyResult>() {
            @Override
            public void onResponse(Call<CommonGroupResult.AddGroupNeedVerifyResult> call, Response<CommonGroupResult.AddGroupNeedVerifyResult> response) {
                if (response != null && response.body() != null) {
                    EventBus.getDefault().post(response.body());
                }else{
                    EventBus.getDefault().post(new CommonGroupResult.AddGroupNeedVerifyResult(-1, HTTP_CONNECT_FAIL));

                }
            }

            @Override
            public void onFailure(Call<CommonGroupResult.AddGroupNeedVerifyResult> call, Throwable t) {
                EventBus.getDefault().post(new CommonGroupResult.AddGroupNeedVerifyResult(-1, HTTP_CONNECT_FAIL));
            }
        });
    }

    public void addGroupNoVerify(String account, int type, int groupId) {
        HttpInterface.IGroupService groupService = mRetrofit.create(HttpInterface.IGroupService.class);
        // Gson gson = new Gson();
        Call<CommonGroupResult.AddGroupNoVerifyResult> call = groupService.addGroupNoVerify(account, type, groupId, MySharedPf.getInstance(MyApplication.getContext()).getToken());
        call.enqueue(new Callback<CommonGroupResult.AddGroupNoVerifyResult>() {
            @Override
            public void onResponse(Call<CommonGroupResult.AddGroupNoVerifyResult> call, Response<CommonGroupResult.AddGroupNoVerifyResult> response) {
                if (response != null && response.body() != null) {
                    EventBus.getDefault().post(response.body());
                }else{
                    EventBus.getDefault().post(new CommonGroupResult.AddGroupNoVerifyResult(-1, HTTP_CONNECT_FAIL));
                }
            }

            @Override
            public void onFailure(Call<CommonGroupResult.AddGroupNoVerifyResult> call, Throwable t) {
                EventBus.getDefault().post(new CommonGroupResult.AddGroupNoVerifyResult(-1, HTTP_CONNECT_FAIL));
            }
        });
    }

    public void getGroupInfoById(int groupId, int type) {
        HttpInterface.IGroupService groupService = mRetrofit.create(HttpInterface.IGroupService.class);
        Gson gson = new Gson();
        Call<CommonGroupResult.GetGroupDetailResult> call = groupService.getGroupAllInfoByGroupId(MyApplication.account,
                MySharedPf.getInstance(MyApplication.getContext()).getToken(), groupId, type);
        call.enqueue(new Callback<CommonGroupResult.GetGroupDetailResult>() {
            @Override
            public void onResponse(Call<CommonGroupResult.GetGroupDetailResult> call, Response<CommonGroupResult.GetGroupDetailResult> response) {
                if (response != null && response.body() != null) {
                    EventBus.getDefault().post(response.body());
                }else{
                    EventBus.getDefault().post(new CommonGroupResult.AddGroupResult(-1, HTTP_CONNECT_FAIL));

                }
            }

            @Override
            public void onFailure(Call<CommonGroupResult.GetGroupDetailResult> call, Throwable t) {
                EventBus.getDefault().post(new CommonGroupResult.AddGroupResult(-1, HTTP_CONNECT_FAIL));
            }
        });
    }

    public void getGroupAllMemberById(int groupId, int type) {
        HttpInterface.IGroupService groupService = mRetrofit.create(HttpInterface.IGroupService.class);
        //   Gson gson = new Gson();
        Call<CommonGroupResult.GetGroupMemberResult> call = groupService.getGroupMemberByGroupId(MyApplication.account,
                MySharedPf.getInstance(MyApplication.getContext()).getToken(), groupId, type);
        call.enqueue(new Callback<CommonGroupResult.GetGroupMemberResult>() {
            @Override
            public void onResponse(Call<CommonGroupResult.GetGroupMemberResult> call, Response<CommonGroupResult.GetGroupMemberResult> response) {
                if (response != null && response.body() != null) {
                    EventBus.getDefault().post(response.body());
                } else {
                    EventBus.getDefault().post(new CommonGroupResult.GetGroupMemberResult(-1, HTTP_CONNECT_FAIL));

                }
            }

            @Override
            public void onFailure(Call<CommonGroupResult.GetGroupMemberResult> call, Throwable t) {
                EventBus.getDefault().post(new CommonGroupResult.GetGroupMemberResult(-1, HTTP_CONNECT_FAIL));
            }
        });
    }


    public void getPersonalGroupSettingInfo(int groupId) {
        HttpInterface.IGroupService groupService = mRetrofit.create(HttpInterface.IGroupService.class);
        //   Gson gson = new Gson();
        Call<CommonGroupResult.GroupSettingResult> call = groupService.getPersonalGroupSettinInfo(MyApplication.account,
                MySharedPf.getInstance(MyApplication.getContext()).getToken(), groupId);
        call.enqueue(new Callback<CommonGroupResult.GroupSettingResult>() {
            @Override
            public void onResponse(Call<CommonGroupResult.GroupSettingResult> call, Response<CommonGroupResult.GroupSettingResult> response) {
                if (response != null && response.body() != null) {
                    EventBus.getDefault().post(response.body());
                } else {
                    EventBus.getDefault().post(new CommonGroupResult.GroupSettingResult(-1, HTTP_CONNECT_FAIL));

                }
            }

            @Override
            public void onFailure(Call<CommonGroupResult.GroupSettingResult> call, Throwable t) {
                EventBus.getDefault().post(new CommonGroupResult.GroupSettingResult(-1, HTTP_CONNECT_FAIL));
            }
        });
    }

    public void getCompanyGroupSettingInfo(int groupId) {
        HttpInterface.IGroupService groupService = mRetrofit.create(HttpInterface.IGroupService.class);
        //   Gson gson = new Gson();
        Call<CommonGroupResult.GroupSettingResult> call = groupService.getCompanyGroupSettinInfo(MyApplication.account,
                MySharedPf.getInstance(MyApplication.getContext()).getToken(), groupId);
        call.enqueue(new Callback<CommonGroupResult.GroupSettingResult>() {
            @Override
            public void onResponse(Call<CommonGroupResult.GroupSettingResult> call, Response<CommonGroupResult.GroupSettingResult> response) {
                if (response != null && response.body() != null) {
                    EventBus.getDefault().post(response.body());
                } else {
                    EventBus.getDefault().post(new CommonGroupResult.GroupSettingResult(-1, HTTP_CONNECT_FAIL));

                }
            }

            @Override
            public void onFailure(Call<CommonGroupResult.GroupSettingResult> call, Throwable t) {
                EventBus.getDefault().post(new CommonGroupResult.GroupSettingResult(-1, HTTP_CONNECT_FAIL));
            }
        });
    }

    public void exitGroup(int type, int groupId) {
        HttpInterface.IGroupService groupService = mRetrofit.create(HttpInterface.IGroupService.class);
        // Gson gson = new Gson();
        Call<CommonGroupResult.ExitGroupResult> call = groupService.exitGroup(MyApplication.account, MySharedPf.getInstance(MyApplication.getContext()).getToken(), groupId, type);
        call.enqueue(new Callback<CommonGroupResult.ExitGroupResult>() {
            @Override
            public void onResponse(Call<CommonGroupResult.ExitGroupResult> call, Response<CommonGroupResult.ExitGroupResult> response) {
                if (response != null && response.body() != null) {
                    EventBus.getDefault().post(response.body());
                }else{
                    EventBus.getDefault().post(new CommonGroupResult.ExitGroupResult(-1, HTTP_CONNECT_FAIL));

                }
            }

            @Override
            public void onFailure(Call<CommonGroupResult.ExitGroupResult> call, Throwable t) {
                EventBus.getDefault().post(new CommonGroupResult.ExitGroupResult(-1, HTTP_CONNECT_FAIL));
            }
        });
    }

    public void transferGroup(int type, int groupId, String newAccount) {
        HttpInterface.IGroupService groupService = mRetrofit.create(HttpInterface.IGroupService.class);
        // Gson gson = new Gson();
        Call<CommonGroupResult.TransferGroupResult> call = groupService.transferGroup(MyApplication.account, MySharedPf.getInstance(MyApplication.getContext()).getToken(), groupId, type, newAccount);
        call.enqueue(new Callback<CommonGroupResult.TransferGroupResult>() {
            @Override
            public void onResponse(Call<CommonGroupResult.TransferGroupResult> call, Response<CommonGroupResult.TransferGroupResult> response) {
                if (response != null && response.body() != null) {
                    EventBus.getDefault().post(response.body());
                }else {
                    EventBus.getDefault().post(new CommonGroupResult.TransferGroupResult(-1, HTTP_CONNECT_FAIL));
                }
            }

            @Override
            public void onFailure(Call<CommonGroupResult.TransferGroupResult> call, Throwable t) {
                EventBus.getDefault().post(new CommonGroupResult.TransferGroupResult(-1, HTTP_CONNECT_FAIL));
            }
        });
    }

    public void kickMember(int type, int groupId, String newAccount) {
        HttpInterface.IGroupService groupService = mRetrofit.create(HttpInterface.IGroupService.class);
        // Gson gson = new Gson();
        Call<CommonGroupResult.KickGroupMemberResult> call = groupService.kickMember(MyApplication.account,
                MySharedPf.getInstance(MyApplication.getContext()).getToken(), groupId, type, newAccount);
        call.enqueue(new Callback<CommonGroupResult.KickGroupMemberResult>() {
            @Override
            public void onResponse(Call<CommonGroupResult.KickGroupMemberResult> call, Response<CommonGroupResult.KickGroupMemberResult> response) {
                if (response != null && response.body() != null) {
                    EventBus.getDefault().post(response.body());
                }else{
                    EventBus.getDefault().post(new CommonGroupResult.ExitGroupResult(-1, HTTP_CONNECT_FAIL));

                }
            }

            @Override
            public void onFailure(Call<CommonGroupResult.KickGroupMemberResult> call, Throwable t) {
                EventBus.getDefault().post(new CommonGroupResult.KickGroupMemberResult(-1, HTTP_CONNECT_FAIL));
            }
        });
    }

  public void setPersonalGroupInfo(String groupInof) {
        HttpInterface.IGroupService groupService = mRetrofit.create(HttpInterface.IGroupService.class);
        // Gson gson = new Gson();
        Call<CommonGroupResult.SetGroupResult> call = groupService.setPersonalGroupInfo(MySharedPf.getInstance(MyApplication.getContext()).getToken(),
                groupInof);
        call.enqueue(new Callback<CommonGroupResult.SetGroupResult>() {
            @Override
            public void onResponse(Call<CommonGroupResult.SetGroupResult> call, Response<CommonGroupResult.SetGroupResult> response) {
                if (response != null && response.body() != null) {
                    EventBus.getDefault().post(response.body());
                }else{
                    EventBus.getDefault().post(new CommonGroupResult.SetGroupResult(-1, HTTP_CONNECT_FAIL));

                }
            }

            @Override
            public void onFailure(Call<CommonGroupResult.SetGroupResult> call, Throwable t) {
                EventBus.getDefault().post(new CommonGroupResult.SetGroupResult(-1, HTTP_CONNECT_FAIL));
            }
        });
    }

  public void setCompanyGroupInfo(String groupInof) {
        HttpInterface.IGroupService groupService = mRetrofit.create(HttpInterface.IGroupService.class);
        // Gson gson = new Gson();
        Call<CommonGroupResult.SetGroupResult> call = groupService.setCompanyGroupInfo(MySharedPf.getInstance(MyApplication.getContext()).getToken(),
                groupInof);
        call.enqueue(new Callback<CommonGroupResult.SetGroupResult>() {
            @Override
            public void onResponse(Call<CommonGroupResult.SetGroupResult> call, Response<CommonGroupResult.SetGroupResult> response) {
                if (response != null && response.body() != null) {
                    EventBus.getDefault().post(response.body());
                }else{
                    EventBus.getDefault().post(new CommonGroupResult.SetGroupResult(-1, HTTP_CONNECT_FAIL));

                }
            }

            @Override
            public void onFailure(Call<CommonGroupResult.SetGroupResult> call, Throwable t) {
                EventBus.getDefault().post(new CommonGroupResult.SetGroupResult(-1, HTTP_CONNECT_FAIL));
            }
        });
    }


}
