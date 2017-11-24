package com.walnutin.hardsdkdemo.ProductList.hch;

import android.os.Handler;
import android.util.Log;

import com.walnutin.hardsdkdemo.ProductList.BluetoothLeService;
import com.walnutin.hardsdkdemo.utils.DigitalTrans;
import com.walnutin.hardsdkdemo.utils.GlobalValue;
import com.walnutin.hardsdkdemo.utils.TimeUtil;

import java.io.UnsupportedEncodingException;
import java.util.Calendar;

/**
 * Created by chenliu on 2017/1/13.
 */

public class WriteCommand {
    final String TAG = WriteCommand.class.getSimpleName();

    private final BluetoothLeService bluetoothLeService;

    public WriteCommand(BluetoothLeService bluetoothLeService) {
        this.bluetoothLeService = bluetoothLeService;
    }


    public void command1() {
        byte[] bytes = getCommand1Char();
        bluetoothLeService.writeRXCharacteristic(bytes);
    }

    private byte[] getCommand1Char() {
        return new byte[20];
    }


    public void command2() {
        //...
    }

    public void getRealtimeData() {
        //  String conf = "68060100006F16";
        bluetoothLeService.writeRXCharacteristic(DigitalTrans.hex2byte("68060100006F16"));
    }

    public void sendRateTestCommand(int status) {

        if (status == GlobalValue.RATE_START) {
            bluetoothLeService.writeRXCharacteristic(DigitalTrans.hex2byte("68060100017016"));
            mHandler.postDelayed(runnable, 1000);

        } else if (status == GlobalValue.RATE_STOP) {
            mHandler.removeCallbacks(runnable);
            bluetoothLeService.writeRXCharacteristic(DigitalTrans.hex2byte("68060100027116"));
        }
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            bluetoothLeService.writeRXCharacteristic(DigitalTrans.hex2byte("68060100006f16")); //
            mHandler.postDelayed(this, 1000);
        }
    };

    Handler mHandler = new Handler() {

    };

    Runnable findRunable = new Runnable() { // 找手环线程
        @Override
        public void run() {

            if (findNum > 1) {
                bluetoothLeService.writeRXCharacteristic(DigitalTrans.hex2byte("68130100007c16")); //开启找手环
                mHandler.postDelayed(this, 1000);
                findNum--;
            } else {
                bluetoothLeService.writeRXCharacteristic(DigitalTrans.hex2byte("68130100017d16")); //关闭 找手环

            }

        }
    };

    int findNum = 0;

    public void findBand(int num) { //寻找手环
        findNum = num;
        bluetoothLeService.writeRXCharacteristic(DigitalTrans.hex2byte("68130100007c16")); //开启找手环
        mHandler.postDelayed(findRunable, 1000);

    }

    public void setUnLostRemind(boolean unLostRemind) { // 防丢开关
        if (unLostRemind == true) {
            bluetoothLeService.writeRXCharacteristic(DigitalTrans.hex2byte("680503000001017216")); // 防丢提醒
        } else {
            bluetoothLeService.writeRXCharacteristic(DigitalTrans.hex2byte("680503000001007116")); // 防丢提醒 关闭

        }

    }


    public void correctTime() {  // 纠正时间

        try {
            String zone = TimeUtil.getCurrentTimeZone();
            String times = "";

            long gapSecond = 0;
            if (zone.contains("+")) {
                times = zone.split("\\+")[1];
                int hour = Integer.valueOf(times.split(":")[0]);
                int minitue = Integer.valueOf(times.split(":")[1]);
                gapSecond = (hour * 3600 + minitue * 60);

            } else if (zone.contains("-")) {
                times = zone.split("\\-")[1];
                int hour = Integer.valueOf(times.split(":")[0]);
                int minitue = Integer.valueOf(times.split(":")[1]);
                gapSecond = -(hour * 3600 + minitue * 60);
            }


            long time = (int) (System.currentTimeMillis() / 1000) + gapSecond; // (int) (System.currentTimeMillis() / 1000);     //1486655970
            String hexTime = DigitalTrans.algorismToHEXString(time);
            String relHexTime = DigitalTrans.reverseHexHighTwoLow(hexTime); // 正确时间戳

            String mValue = "68200400" + relHexTime;
            int correctValue = Integer.parseInt(DigitalTrans.algorismToHEXString(DigitalTrans.addeachTwoValue(mValue)), 16); // 校验码值
            correctValue = correctValue % 256;
            String value = mValue + DigitalTrans.algorismToHEXString(correctValue) + "16";

            bluetoothLeService.writeRXCharacteristic(DigitalTrans.hex2byte(value));
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public void deleteDevicesAllData() { // 恢复出厂设置
        bluetoothLeService.writeRXCharacteristic(DigitalTrans.hex2byte("68110100017b16"));
    }

    public void setAlarmClock(int flag, byte weekPeroid, int hour, int minitue, boolean isOpen, String type, String tip) {

        String fg = DigitalTrans.algorismToHEXString(flag); //编号
        String wp = DigitalTrans.algorismToHEXString(weekPeroid);     // 重复 周期
        String h = DigitalTrans.algorismToHEXString(hour); // 时
        String m = DigitalTrans.algorismToHEXString(minitue); // 分
        String mValue = "";
        if (isOpen == true && !type.equals(GlobalValue.OTHER_TYPE)) {
            mValue = "6809070001" + fg + DigitalTrans.formatData(type) + "01" + h + m + wp;
        } else if (isOpen == true && type.equals(GlobalValue.OTHER_TYPE)) {
            try {
                byte[] bodyByte = tip.getBytes("unicode");
                String data = DigitalTrans.byteArrHexToString(bodyByte); // 16进制数据域
                if (data.length() > 88) {
                    data = data.substring(0, 88);
                }
                int len = data.length() / 2;
                String dataLen = DigitalTrans.algorismToHEXString(7 + len) + "00"; // 数据长度
                mValue = "6809" + dataLen + "01" + fg + DigitalTrans.formatData(type) + "01" + h + m + wp + data;
                String value = DigitalTrans.getResultCommand(mValue);
                while (value.length() > 40) {
                    bluetoothLeService.writeRXCharacteristic(DigitalTrans.hex2byte(value.substring(0, 40)));
                    value = value.substring(40, value.length());
                    Thread.sleep(20);
                }
                bluetoothLeService.writeRXCharacteristic(DigitalTrans.hex2byte(value));
                return;

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else {  //删除编号 为 flag的 闹钟
            mValue = "6809020002" + fg;
        }


        String value = DigitalTrans.getResultCommand(mValue);
        bluetoothLeService.writeRXCharacteristic(DigitalTrans.hex2byte(value));

        //   Log.i(TAG, value + "  --- ");


    }

    public void notifyMessage(int type, String body) { // 通知消息
        String conf = "680b"; // 控制码
        try {
            byte[] bodyByte = body.getBytes("UTF-8");
            String data = DigitalTrans.byteArrHexToString(bodyByte); // 16进制数据域
            if (data.length() > 64) {
                data = data.substring(0, 64);
            }
            int len = data.length() / 2;
            String dataLen = DigitalTrans.algorismToHEXString(1 + len) + "00"; // 数据长度
            String tp = DigitalTrans.algorismToHEXString(type);  //数据域 类型
            String mValue = conf + dataLen + tp + data;
            String value = DigitalTrans.getResultCommand(mValue);

            if (value.length() > 40) {
                bluetoothLeService.writeRXCharacteristic(DigitalTrans.hex2byte(value.substring(0, 40)));
                value = value.substring(40, value.length());
                Thread.sleep(20);
            }

            bluetoothLeService.writeRXCharacteristic(DigitalTrans.hex2byte(value));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public void sendOffHookCommand() { // 挂断
        bluetoothLeService.writeRXCharacteristic(DigitalTrans.hex2byte("68010100016B16"));

    }

    public void sendCallInfo(String contactName, String contact) { // 来电信息
        Log.i(TAG, contact);
        String conf = "6801";
        String dataLen = "";
        String data = "";
        if (contact.equals("")) { //没有联系人
            dataLen = "1000";
            data = "00" + DigitalTrans.StringToAsciiString(contactName) + "00000000";
        } else {
            try {
                byte[] bodyByte = contact.getBytes("UTF-8");
                data = DigitalTrans.byteArrHexToString(bodyByte); // 16进制数据域 联系人部分数据
                if (data.length() > 64) {
                    data = data.substring(0, 64);
                }
                int len = data.length() / 2;
                data = "00" + DigitalTrans.StringToAsciiString(contactName) + "00000000" + data;
                dataLen = DigitalTrans.algorismToHEXString(16 + len) + "00"; // 数据长度

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        String mValue = conf + dataLen + data;

        String value = DigitalTrans.getResultCommand(mValue);
        //  String value = "6801160000313336353638393837343500000000E5BCA0E4B8893316";

        while (value.length() > 40) {
            String temp = value.substring(0, 40);
            bluetoothLeService.writeRXCharacteristic(DigitalTrans.hex2byte(temp));
            value = value.substring(40, value.length());
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
        bluetoothLeService.writeRXCharacteristic(DigitalTrans.hex2byte(value));


    }

    public void openFuncRemind(int type, boolean isOpen) { // 消息提醒设置 开关
        String head = "6805030000";
        String cmd = "";
        switch (type) {
            case GlobalValue.TYPE_MESSAGE_QQ:
                cmd = "0a";
                break;
            case GlobalValue.TYPE_MESSAGE_WECHAT:
                cmd = "09";
                break;
            case GlobalValue.TYPE_MESSAGE_PHONE:
                cmd = "03";
                break;
            case GlobalValue.TYPE_MESSAGE_SMS:
                cmd = "02";
                break;
            case GlobalValue.TYPE_MESSAGE_FACEBOOK:  // 添加facebook 提醒
                cmd = "0b";
                break;
            case GlobalValue.TYPE_MESSAGE_WHATSAPP:  // 添加whatapp 提醒
                cmd = "0e";
                break;
            default:
                return;
        }
        int flag = 0;
        if (isOpen) {
            flag = 1;
        } else {
            flag = 0;
        }
        String mValue = head + cmd + DigitalTrans.algorismToHEXString(flag);
        mValue = DigitalTrans.getResultCommand(mValue);
        bluetoothLeService.writeRXCharacteristic(DigitalTrans.hex2byte(mValue));
    }

    public void setLongSitRemind(int isOpen, int time, String startTime, String endTime) { //"0009 09:00 开始时间   0012 为18:00 结束时间"
        //  startTime = startTime.replace(":", "");
        //  endTime = endTime.replace(":", "");
        System.out.println("startTime: " + startTime);
        System.out.println("endTime: " + endTime);
        String[] starttimes = startTime.split(":");

        int starthour = Integer.parseInt(starttimes[0]);
        int startminitue = Integer.parseInt(starttimes[1]);

        String[] endtimes = endTime.split(":");
        int endhour = Integer.parseInt(endtimes[0]);
        int endminitues = Integer.parseInt(endtimes[1]);
        startTime = DigitalTrans.algorismToHEXString(startminitue) + DigitalTrans.algorismToHEXString(starthour);
        endTime = DigitalTrans.algorismToHEXString(endminitues) + DigitalTrans.algorismToHEXString(endhour);

        System.out.println("startTime: " + startTime);
        System.out.println("endTime: " + endTime);

        String conf = "681408000100" + DigitalTrans.algorismToHEXString(isOpen) + startTime + endTime +
                DigitalTrans.algorismToHEXString(time); //上午9点到下午6点
        String mValue = DigitalTrans.getResultCommand(conf);
        bluetoothLeService.writeRXCharacteristic(DigitalTrans.hex2byte(mValue));
    }

    public void asyncAllData(String head) {       // type
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DATE);
        int month = calendar.get(Calendar.MONTH) + 1;
        int year = calendar.get(Calendar.YEAR) - 2000;
        String dayS = DigitalTrans.algorismToHEXString(day);
        String monthS = DigitalTrans.algorismToHEXString(month);
        String yearS = DigitalTrans.algorismToHEXString(year);
        String conf = "68260400" + dayS + monthS + yearS + head;
        String mValue = DigitalTrans.getResultCommand(conf);
        System.out.println("asyncAllData: " + mValue);
        bluetoothLeService.writeRXCharacteristic(DigitalTrans.hex2byte(mValue));
    }

    public void answerData(String head, String time) {       // head 代表 包头， time 代表日期
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DATE);
        int month = calendar.get(Calendar.MONTH) + 1;
        int year = calendar.get(Calendar.YEAR) - 2000;
        String conf = "68260400" + time + head;
        String mValue = DigitalTrans.getResultCommand(conf);
        System.out.println("asyncAllData: " + mValue);
        bluetoothLeService.writeRXCharacteristic(DigitalTrans.hex2byte(mValue));
    }

    public void asyncHeartData(int serial) {       // type
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DATE);
        int month = calendar.get(Calendar.MONTH) + 1;
        int year = calendar.get(Calendar.YEAR) - 2000;
        String dayS = DigitalTrans.algorismToHEXString(day);
        String monthS = DigitalTrans.algorismToHEXString(month);
        String yearS = DigitalTrans.algorismToHEXString(year);
        String conf = "68260600" + dayS + monthS + yearS + "0308" + DigitalTrans.algorismToHEXString(serial);
        String mValue = DigitalTrans.getResultCommand(conf);
        System.out.println("asyncHeartData: " + mValue);
        bluetoothLeService.writeRXCharacteristic(DigitalTrans.hex2byte(mValue));
    }

    public void answerHeartData(String time, int serial) {
        String conf = "68260600" + time + "0308" + DigitalTrans.algorismToHEXString(serial);
        String mValue = DigitalTrans.getResultCommand(conf);
        System.out.println("asyncHeartData: " + mValue);
        bluetoothLeService.writeRXCharacteristic(DigitalTrans.hex2byte(mValue));
    }

    public void sendHexString(String data) {

        bluetoothLeService.writeRXCharacteristic(DigitalTrans.hex2byte(data));
    }

    public void answerFatigeData(String time) { // 时间
        String conf = "68250300" + time;
        String value = DigitalTrans.getResultCommand(conf);
        bluetoothLeService.writeRXCharacteristic(DigitalTrans.hex2byte(value));

    }

    public void setTakePhoto(String data) {
        String conf = "680d0100" + data;
        String value = DigitalTrans.getResultCommand(conf);
        bluetoothLeService.writeRXCharacteristic(DigitalTrans.hex2byte(value));
    }
}
