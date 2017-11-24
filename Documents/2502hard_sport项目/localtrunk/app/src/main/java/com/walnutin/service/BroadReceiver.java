package com.walnutin.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.walnutin.activity.MyApplication;
import com.walnutin.util.Config;
import com.walnutin.util.DeviceSharedPf;

public class BroadReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equals("android.provider.Telephony.SMS_RECEIVED")) {
            System.out.println("NLService sms receive");
            sendSmsInfo(context, intent);
        } else if (action.equals("android.intent.action.PHONE_STATE")) {
            sendPhoneState(context, intent);
        }
    }

    void sendPhoneState(Context context, Intent intent) {
        if (MyApplication.isDevConnected == false) {
            return;
        }
        if (DeviceSharedPf.getInstance(context).getBoolean("enablePhoneRemind")) {
            String mIncomingNumber;
            TelephonyManager tManager = (TelephonyManager) context
                    .getSystemService(Service.TELEPHONY_SERVICE);
            switch (tManager.getCallState()) {
                case TelephonyManager.CALL_STATE_RINGING:
                    mIncomingNumber = intent.getStringExtra("incoming_number");
                    Intent intent1 = new Intent(Config.NOTICE_PHONE_ACTION);
                    intent1.putExtra("contacts", mIncomingNumber);
                    intent1.putExtra("state", "CALL_STATE_RINGING");
                    context.sendBroadcast(intent1);
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK: // 挂电话


                case TelephonyManager.CALL_STATE_IDLE:  // 接电话

                  default:
                      Intent intent2 = new Intent(Config.NOTICE_PHONE_ACTION);
                      intent2.putExtra("contacts", "null");
                      intent2.putExtra("state", "CALL_STATE_OFFHOOK");
                      context.sendBroadcast(intent2);
                      break;
            }
        }

    }

    void sendSmsInfo(Context context, Intent intent) {
        if (MyApplication.isDevConnected == false) {
            return;
        }
        if (DeviceSharedPf.getInstance(context).getBoolean("enableMsgRemind")) {
            SmsManager sms = SmsManager.getDefault();
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                Object[] pdus = (Object[]) bundle.get("pdus");
                String contact = "";
                for (Object p : pdus) {
                    byte[] pdu = (byte[]) p;
                    SmsMessage message = SmsMessage.createFromPdu(pdu); //根据获得的byte[]封装成SmsMessage
                    String body = message.getMessageBody();             //发送内容
                    contact = message.getOriginatingAddress();    //短信发送方
                }
                Intent intent1 = new Intent(Config.NOTICE_MSG_ACTION);
                intent1.putExtra("contacts", contact);
                context.sendBroadcast(intent1);
            }

        }
    }
}