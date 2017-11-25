package com.walnutin.hardsdkdemo.ProductNeed.Jinterface;

import android.bluetooth.BluetoothDevice;

public interface DeviceScanInterfacer {
    void LeScanCallback(BluetoothDevice device, int rssi, byte[] scanRecaord);
}
