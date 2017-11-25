package com.walnutin.hardsdkdemo.ProductList.wyp;


import com.walnutin.hardsdkdemo.ProductList.BluetoothLeService;
import com.walnutin.hardsdkdemo.utils.DigitalTrans;
import com.walnutin.hardsdkdemo.utils.GlobalValue;

/**
 * Created by chenliu on 2017/1/13.
 */

public class WriteCommand {

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

    public void sendRateTestCommand(int status) {

        if (status == GlobalValue.RATE_START) {
            bluetoothLeService.writeRXCharacteristic(DigitalTrans.hex2byte("d001"));
        } else if (status == GlobalValue.RATE_STOP) {
            bluetoothLeService.writeRXCharacteristic(DigitalTrans.hex2byte("d000"));
        }
    }


}
