package com.walnutin.Model;

import android.bluetooth.BluetoothDevice;

import com.walnutin.Jinterface.LinkDeviceChange;
import com.walnutin.entity.Device;
import com.walnutin.entity.UserBean;

import java.util.List;
import java.util.Observable;

import cn.sharesdk.framework.Platform;

/**
 * 作者：MrJiang on 2016/7/13 16:49
 */
public interface LinkDeviceIntf {
    void startScan();

    void stopScan();

    List getLinkedDevices();

    List getScanListDevice();

    void addLinkedDevice(Device device);

    void removeLinkedDeviceByName(String deviceName);

    void removeLinkedDeviceByDeviceAddr(String deviceAddr);

    void removeLinkedDevice(Device device);

    List getLocalLinkedList();

    void saveLinked();

    void setScanListDevice(List listDevice);

    boolean isSupportBle4_0();

    boolean isBleOpened();

    void clearScanAndLinkedList();

    void setOnLinkBlueDeviceChange(LinkDeviceChange linkBlueDevice);

}
