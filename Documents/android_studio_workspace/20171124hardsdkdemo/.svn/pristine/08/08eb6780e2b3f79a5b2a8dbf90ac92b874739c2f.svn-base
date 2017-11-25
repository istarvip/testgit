package com.walnutin.hardsdkdemo.ProductNeed.manager;

import android.content.Context;

import com.walnutin.hardsdkdemo.ProductNeed.entity.NoticeDevice;
import com.walnutin.hardsdkdemo.ProductNeed.entity.NoticeInfo;
import com.walnutin.hardsdkdemo.utils.Conversion;
import com.walnutin.hardsdkdemo.utils.DeviceSharedPf;

import java.util.List;

/**
 * 作者：MrJiang on 2016/8/15 10:25
 */
public class NoticeInfoManager {
    private NoticeInfo noticeInfo;
    DeviceSharedPf deviceSharedPf;
    static Context mContext;
    private static NoticeInfoManager noticeInfoManager;

    static public NoticeInfoManager getInstance(Context context) {
        mContext = context;
        if (noticeInfoManager == null) {
            noticeInfoManager = new NoticeInfoManager();
        }
        return noticeInfoManager;
    }

    private NoticeInfoManager() {
        deviceSharedPf = DeviceSharedPf.getInstance(mContext);
        noticeInfo = new NoticeInfo();
    }

    public void setNoticeInfo(NoticeInfo noticeInfo) {
        this.noticeInfo = noticeInfo;
    }

    public NoticeInfo getLocalNoticeInfo() {
        noticeInfo = (NoticeInfo) Conversion.stringToObject(deviceSharedPf.getString("deviceNotice", null));
        if (noticeInfo == null) {
            noticeInfo = new NoticeInfo();

        }
        return noticeInfo;
    }

    public void saveNoticeInfo() {
        //   Gson gson = new Gson();
        //     String st = gson.toJson(noticeInfo);
        //  System.out.println("noticeInfo: " + st);
        deviceSharedPf.setString("deviceNotice", Conversion.objectToString(noticeInfo)); // 保存推送设置
    }

    public boolean isEnablePhone() {
        return noticeInfo.isEnablePhone();
    }

    public boolean isEnableMsg() {
        return noticeInfo.isEnableMsg;
    }

    public boolean isEnableQQ() {
        return noticeInfo.isEnableQQ;
    }

    public boolean isEnableWeChat() {
        return noticeInfo.isEnableWeChat;
    }

    public void setIsEnablePhoneRemind(boolean enablePhoneRemind) {
        noticeInfo.isEnablePhone = enablePhoneRemind;
        deviceSharedPf.setBoolean("enablePhoneRemind", enablePhoneRemind);
    }

    public void setIsEnableMsgRemind(boolean enableMsgRemind) {
        noticeInfo.isEnableMsg = enableMsgRemind;
        deviceSharedPf.setBoolean("enableMsgRemind", enableMsgRemind);

    }

    public void setIsEnableQQRemind(boolean enableQQRemind) {
        noticeInfo.isEnableQQ = enableQQRemind;
    }

    public boolean isEnableFaceBook() {
        return noticeInfo.isEnableFaceBook;
    }

    public void setEnableFaceBook(boolean enableFaceBook) {
        noticeInfo.isEnableFaceBook = enableFaceBook;
    }

    public void setIsEnableWeChatRemind(boolean enableWeChatRemind) {
        noticeInfo.isEnableWeChat = enableWeChatRemind;
    }

    public void addOtherSoftRemind(String softName) {
//        if (!noticeInfo.otherApp.contains(softName)) {
//         //   noticeInfo.otherApp.add(softName);
//        }
    }

    public void addoftRemind(NoticeDevice noticeDevice) {

        for (NoticeDevice device : noticeInfo.otherApp) {
            if (noticeDevice.appName.equals(device.appName)) {
                return;
            }

        }
        noticeInfo.otherApp.add(noticeDevice);
    }

    public List getSoftRemindList() {
        return noticeInfo.otherApp;
    }


    public boolean isEnableLinkedin() {
        return noticeInfo.isEnableLinkedin;
    }

    public void setEnableLinkedin(boolean enableLine) {
        noticeInfo.isEnableLinkedin = enableLine;
    }

    public boolean isEnableTwitter() {
        return noticeInfo.isEnableTwitter;
    }

    public void setEnableTwitter(boolean enableTwitter) {
        noticeInfo.isEnableTwitter = enableTwitter;
    }

    public boolean isAllowOther() {
        return noticeInfo.isAllowOther();
    }

    public void setAllowOther(boolean allowOther) {
        noticeInfo.isAllowOther = allowOther;
    }

    public boolean isEnableWhatsApp() {
        return noticeInfo.isEnableWhatsApp;
    }

    public void setEnableWhatsApp(boolean enableWhatsApp) {
        noticeInfo.isEnableWhatsApp = enableWhatsApp;
    }

//
//    public boolean isAllowMoreAppNotice() {
//        return noticeInfo.isAllowMoreAppNotice;
//    }
//
//    public void setAllowMoreAppNotice(boolean allowMoreAppNotice) {
//        noticeInfo.isAllowMoreAppNotice = allowMoreAppNotice;
//    }
}
