package com.walnutin.hardsdkdemo.ProductNeed.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 作者：MrJiang on 2016/8/15 10:00
 */
public class NoticeInfo implements Serializable {
    public boolean isEnablePhone;
    public boolean isEnableMsg;
    public boolean isEnableQQ;
    public boolean isEnableWeChat;
    public boolean isEnableFaceBook;
    public boolean isEnableLinkedin;
    public boolean isEnableTwitter;
    public boolean isEnableWhatsApp;
    public boolean isAllowOther;
    public List<NoticeDevice> otherApp = new ArrayList<>();

    public boolean isEnablePhone() {
        return isEnablePhone;
    }

    public void setEnablePhone(boolean enablePhone) {
        isEnablePhone = enablePhone;
    }

    public boolean isEnableMsg() {
        return isEnableMsg;
    }

    public void setEnableMsg(boolean enableMsg) {
        isEnableMsg = enableMsg;
    }

    public boolean isEnableQQ() {
        return isEnableQQ;
    }

    public void setEnableQQ(boolean enableQQ) {
        isEnableQQ = enableQQ;
    }

    public boolean isEnableWeChat() {
        return isEnableWeChat;
    }

    public void setEnableWeChat(boolean enableWeChat) {
        isEnableWeChat = enableWeChat;
    }

    public List<NoticeDevice> getOtherApp() {
        return otherApp;
    }

    public void setOtherApp(List<NoticeDevice> otherApp) {
        this.otherApp = otherApp;
    }

    public boolean isEnableFaceBook() {
        return isEnableFaceBook;
    }

    public void setEnableFaceBook(boolean enableFaceBook) {
        isEnableFaceBook = enableFaceBook;
    }

    public boolean isEnableLinkedin() {
        return isEnableLinkedin;
    }

    public void setEnableLinkedin(boolean enableLinkedin) {
        isEnableLinkedin = enableLinkedin;
    }

    public boolean isEnableTwitter() {
        return isEnableTwitter;
    }

    public void setEnableTwitter(boolean enableTwitter) {
        isEnableTwitter = enableTwitter;
    }

    public boolean isAllowOther() {
        return isAllowOther;
    }

    public void setAllowOther(boolean allowOther) {
        isAllowOther = allowOther;
    }

    public boolean isEnableWhatsApp() {
        return isEnableWhatsApp;
    }

    public void setEnableWhatsApp(boolean enableWhatsApp) {
        isEnableWhatsApp = enableWhatsApp;
    }
}
