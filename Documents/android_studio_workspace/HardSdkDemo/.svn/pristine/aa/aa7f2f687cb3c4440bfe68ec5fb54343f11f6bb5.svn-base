package com.walnutin.hardsdkdemo.ProductList.rtk;

import android.os.Handler;
import android.util.Log;

import com.walnutin.hardsdkdemo.ProductList.BluetoothLeService;
import com.walnutin.hardsdkdemo.ProductList.HardSdk;
import com.walnutin.hardsdkdemo.utils.DigitalTrans;
import com.walnutin.hardsdkdemo.utils.GlobalValue;
import com.walnutin.hardsdkdemo.utils.MySharedPf;
import com.walnutin.hardsdkdemo.utils.TimeUtil;

import java.io.UnsupportedEncodingException;
import java.util.Calendar;

/**
 * Created by chenliu on 2017/1/13.
 */

public class WriteCommand {
    final String TAG = WriteCommand.class.getSimpleName();
    private final BluetoothLeService bluetoothLeService;
    private Handler mHandler = new Handler() {

    };


    public WriteCommand(BluetoothLeService bluetoothLeService) {
        Log.d(TAG, " RtkSdk 4 WriteCommand: bluetoothLeService:" + bluetoothLeService);
        this.bluetoothLeService = bluetoothLeService;
    }


    public void sendRateTestCommand(int status) {
        Log.d(TAG, "sendRateTestCommand: status:" + status);

        if (status == GlobalValue.RATE_START) {
            bluetoothLeService.writeRXCharacteristic(DigitalTrans.hex2byte("320b"));
//            bluetoothLeService.writeRXCharacteristic(DigitalTrans.hex2byte("4916"));

        } else if (status == GlobalValue.RATE_STOP) {
            bluetoothLeService.writeRXCharacteristic(DigitalTrans.hex2byte("3216"));
        }

    }

    public void setAlarmClock(int flag, byte weekPeroid, int hour, int minitue, boolean isOpen) {
        //？关闹钟？ -1?
        if (isOpen) {
            bluetoothLeService.writeRXCharacteristic(DigitalTrans.hex2byte("0C" + DigitalTrans.algorismToHEXString(hour) + DigitalTrans.algorismToHEXString(minitue)));
        } else {
            bluetoothLeService.writeRXCharacteristic(DigitalTrans.hex2byte("0C" + DigitalTrans.algorismToHEXString(-1) + DigitalTrans.algorismToHEXString(-1)));
        }
    }


    public void syncTime() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        String hexYear = DigitalTrans.algorismToHEXString(year);
//        int highYear = year / 100;
//        int lowYear = year - (highYear * 100);
        //    Log.d(TAG, "syncTime: highYear:" + highYear + " lowyear:" + lowYear);
        //      String highYearHexString = DigitalTrans.algorismToHEXString(highYear);
        //     String lowYearHexString = DigitalTrans.algorismToHEXString(lowYear);
        String monthHexString = DigitalTrans.algorismToHEXString(month);
        String dayHexString = DigitalTrans.algorismToHEXString(day);
        bluetoothLeService.writeRXCharacteristic(DigitalTrans.hex2byte("08" + hexYear + monthHexString + dayHexString)); //年份 暂定位07e1
//        try {
//            Thread.sleep(300);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

        long timeMillis = System.currentTimeMillis();
        int hour = TimeUtil.hourFromTimeMillis(timeMillis);
        int minitue = TimeUtil.minituesFromTimeMillis(timeMillis);
        int second = 0;
        Log.d(TAG, "syncTime: hour:" + hour + " minitue:" + minitue + " second:" + second);
        String hourHexString = DigitalTrans.algorismToHEXString(hour);
        String minitueHexString = DigitalTrans.algorismToHEXString(minitue);
        String secondHexString = DigitalTrans.algorismToHEXString(second);
        bluetoothLeService.writeRXCharacteristic(DigitalTrans.hex2byte("09" + hourHexString + minitueHexString + secondHexString));
//        try {
//            Thread.sleep(300);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
    }

    public void setHeight(int height) {
        bluetoothLeService.writeRXCharacteristic(DigitalTrans.hex2byte("04" + DigitalTrans.algorismToHEXString(height)));
    }

    public void setWeight(int weight) {
        bluetoothLeService.writeRXCharacteristic(DigitalTrans.hex2byte("05" + DigitalTrans.algorismToHEXString(weight)));
    }

    public void setAge(int age) {
        bluetoothLeService.writeRXCharacteristic(DigitalTrans.hex2byte("2c" + DigitalTrans.algorismToHEXString(age)));
    }

    public void setSex(String sexfrom) {
        bluetoothLeService.writeRXCharacteristic(DigitalTrans.hex2byte("2d" + ("男".equals(sexfrom) ? 0 : 1)));
    }

    public void queryDeviceVersion() {
        bluetoothLeService.writeRXCharacteristic(DigitalTrans.hex2byte("17"));
    }


    public void sendQQWeChatTypeCommand(int type, String body) {
        //   Log.i("sendQCommand:",type+"");
        Log.d(TAG, "onCharacteristicWrite:  type:" + type + " body:" + body );
        try {
            Thread.sleep(100);

            byte[] bodyByte;
            if (MySharedPf.getInstance(HardSdk.getInstance().getContext()).getZikuType().equals("00")) {
                bodyByte = body.getBytes("GB2312");
                //  bodyByte = body.getBytes("unicode");
            } else {
                bodyByte = body.getBytes("unicode");
            }
            String data = DigitalTrans.byteArrHexToString(bodyByte); // 16进制数据域
            int len = data.length() / 2;  // 字节数
            String cmd = "43";
            int sumPackage = len / 17 + 1; // 总包数
            int startSoke = 1;
            String content = "";
            while (startSoke <= sumPackage) {
                if (data.length() > 34 * (startSoke)) {
                    content = data.substring(34 * (startSoke - 1), 34 * (startSoke));
                } else {
                    content = data.substring(34 * (startSoke - 1), data.length());
                    while (content.length() < 34) {
                        content += "00";
                    }
                }

                String value = cmd + DigitalTrans.algorismToHEXString(sumPackage) + DigitalTrans.algorismToHEXString(startSoke) + content;
                startSoke++;
                System.out.println("onCharacteristicWrite:  value:" +value+" hex:"+ DigitalTrans.hex2byte(value));
                bluetoothLeService.writeRXCharacteristic(DigitalTrans.hex2byte(value));
            }

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void sendCallInfo(String number, String contact) {
        bluetoothLeService.writeRXCharacteristic(DigitalTrans.hex2byte("06" + "aa"));
    }


    public void sendOffHookCommand() {
        bluetoothLeService.writeRXCharacteristic(DigitalTrans.hex2byte("06" + "ff"));
    }

    public void sendSmsInfo(String number, String contact, String content) {
        bluetoothLeService.writeRXCharacteristic(DigitalTrans.hex2byte("07" + "aa"));
    }

    public void resetBracelet() {
        bluetoothLeService.writeRXCharacteristic(DigitalTrans.hex2byte("B6" + "5A"));
    }

    public void sendSedentaryRemindCommand(int isOpen, int interval, int startHour, int startMinute, int endHour, int endMinute) {

        String intervalResult = "01";
        switch (interval) {
            case 45:
                intervalResult = "01";
                break;
            case 60:
                intervalResult = "02";
                break;
            case 120:
                intervalResult = "03";
                break;
            case 180:
                intervalResult = "04";
                break;
            case 240:
                intervalResult = "05";
                break;
            default:
                intervalResult = "01";
                break;
        }

        String startHourHex = DigitalTrans.algorismToHEXString(startHour);
        String startMinuteHex = DigitalTrans.algorismToHEXString(startMinute);

        String endHourHex = DigitalTrans.algorismToHEXString(endHour);
        String endMinuteHex = DigitalTrans.algorismToHEXString(endMinute);
        if (isOpen == 1) {
            bluetoothLeService.writeRXCharacteristic(DigitalTrans.hex2byte("1E" + intervalResult + startHourHex + startMinuteHex + endHourHex + endMinuteHex));
        } else {
            bluetoothLeService.writeRXCharacteristic(DigitalTrans.hex2byte("1E" + "00" + startHourHex + startMinuteHex + endHourHex + endMinuteHex));
        }
    }

    public void sendHexString(String hex) {
        bluetoothLeService.writeRXCharacteristic(DigitalTrans.hex2byte(hex));
    }

    public void setScreenOnTime(int screenOnTime) {
        if (screenOnTime < 0 || screenOnTime > 1000) {
            screenOnTime = 10;
        }
        String screenOnTimeResult = DigitalTrans.algorismToHEXString(screenOnTime);
        bluetoothLeService.writeRXCharacteristic(DigitalTrans.hex2byte("0B" + screenOnTimeResult));
    }

    public void setMetric(boolean isInch) {
        if (isInch == false) {
            bluetoothLeService.writeRXCharacteristic(DigitalTrans.hex2byte("48" + "00"));
        } else {
            bluetoothLeService.writeRXCharacteristic(DigitalTrans.hex2byte("48" + "01"));
        }
    }


    public void setAutoHeartRate(boolean isAuto) {
        if (isAuto) {
            bluetoothLeService.writeRXCharacteristic(DigitalTrans.hex2byte("35" + "0A"));
        } else {
            bluetoothLeService.writeRXCharacteristic(DigitalTrans.hex2byte("35" + "FF"));
        }
    }

    public void changePalming(boolean palming) {
        if (palming) {
            bluetoothLeService.writeRXCharacteristic(DigitalTrans.hex2byte("3A" + "01"));
        } else {
            bluetoothLeService.writeRXCharacteristic(DigitalTrans.hex2byte("3A" + "00"));
        }
    }


}
