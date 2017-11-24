package com.walnutin.Model;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.View;
import android.widget.Toast;

import com.walnutin.Jinterface.LinkDeviceChange;
import com.walnutin.activity.MyApplication;
import com.walnutin.entity.Device;
import com.walnutin.entity.HeartRateModel;
import com.walnutin.util.Conversion;
import com.walnutin.util.MySharedPf;
import com.yc.peddemo.sdk.BLEServiceOperate;
import com.yc.peddemo.sdk.DeviceScanInterfacer;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * 作者：MrJiang on 2016/7/13 16:49
 */
public class LinkDeviceImpl implements LinkDeviceIntf {
    private List<BluetoothDevice> mLeDevices;
    private MySharedPf mySharedPf;
    private Context mContext;
    private BLEServiceOperate mBLEServiceOperate;
    private DeviceScanInterfacer deviceScanInterfacer;
    public boolean closeBluetooth = false;
    private BluetoothAdapter bluetoothAdapter;
    LinkDeviceChange linkBlueDevice;
    List<Device> linkedDeviceList;


    public LinkDeviceImpl(Context context) {
        mContext = context;
        mySharedPf = MySharedPf.getInstance(context);
        mBLEServiceOperate = BLEServiceOperate.getInstance(context);// 用于BluetoothLeService实例化准备
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        deviceScanInterfacer = new DeviceScanOpen();
        mBLEServiceOperate.setDeviceScanListener(deviceScanInterfacer);
        mLeDevices = new ArrayList<>();
    }

    public boolean isSupportBle4_0() {
        if (mBLEServiceOperate.isSupportBle4_0()) {
            return true;
        }
        return false;
    }

    public void addLinkedDevice(Device device) {
        for (Device device1 : linkedDeviceList) {
            if (device1.getDeviceName().equals(device.getDeviceName()) && device1.getDeviceAddr().equals(device.getDeviceAddr())) {
                return;
            }
        }
        linkedDeviceList.add(0, device);   //添加至队首

        setLinkedDevice(linkedDeviceList);
    }

    public void removeLinkedDeviceByName(String deviceName) {
        for (Device device : linkedDeviceList) {
            if (device.getDeviceName().equals(deviceName)) {
                linkedDeviceList.remove(device);
                return;
            }
        }
    }

    public void removeLinkedDeviceByDeviceAddr(String deviceAddr) {
        for (Device device : linkedDeviceList) {
            if (device.getDeviceAddr().equals(deviceAddr)) {
                linkedDeviceList.remove(device);
                return;
            }
        }
    }

    @Override
    public void removeLinkedDevice(Device device) {
        for (Device device2 : linkedDeviceList) {
            if (device2.getDeviceAddr().equals(device.getDeviceAddr()) && device2.getDeviceName().equals(device.getDeviceName())) {
                linkedDeviceList.remove(device2);
                setLinkedDevice(linkedDeviceList);
                return;
            }
        }
    }

    public List getLocalLinkedList() {
        linkedDeviceList = (List<Device>) Conversion.stringToList(mySharedPf.getString("devLinkedList", null));
        if (linkedDeviceList == null) {
            linkedDeviceList = new ArrayList<>();
        }
        setLinkedDevice(linkedDeviceList);
        return linkedDeviceList;
    }

    public void saveLinked() {
        mySharedPf.setString("devLinkedList", Conversion.listToString(linkedDeviceList));
    }

    @Override
    public boolean isBleOpened() {
        return mBLEServiceOperate.isBleEnabled();
    }

    @Override
    public void startScan() {
        //获取已配对过的蓝牙设备
        scanLeDevice(true);
    }

    private void scanLeDevice(final boolean enable) {
        if (enable) {
            mBLEServiceOperate.startLeScan();
        } else {
            mBLEServiceOperate.stopLeScan();
        }
    }

    @Override
    public void stopScan() {
        mBLEServiceOperate.stopLeScan();
    }

    @Override
    public List getLinkedDevices() {
        return linkedDeviceList;
    }


    public void setLinkedDevice(List l) {
        //   linkedDeviceList = l;
        if (linkBlueDevice != null) {
            linkBlueDevice.onLinkedDeviceChange(l);
        }
    }

    @Override
    public List getScanListDevice() {
        return mLeDevices;
    }

    @Override
    public void setScanListDevice(List listDevice) {
        mLeDevices = listDevice;
        if (linkBlueDevice != null) {
            linkBlueDevice.onScanDeviceChanged(mLeDevices);
        }
    }


    private class DeviceScanOpen implements DeviceScanInterfacer {

        @Override
        public void LeScanCallback(final BluetoothDevice device, int i) {
            boolean hasDevice = false;
            for (BluetoothDevice bluetoothDevice : mLeDevices) {
                if (bluetoothDevice.getAddress() != null && bluetoothDevice.getName() != null) {
                    if (bluetoothDevice.getAddress().equals(device.getAddress()) && bluetoothDevice.getName().equals(device.getName())) {
                        hasDevice = true;
                        return;
                    }
                }
            }
            if (!hasDevice) {
                mLeDevices.add(device);
                if (linkBlueDevice != null) {
                    linkBlueDevice.onScanDeviceChanged(mLeDevices);
                }
            }

        }
    }

    @Override
    public void clearScanAndLinkedList() {
        mLeDevices.clear();
        setScanListDevice(mLeDevices);
    }

    @Override
    public void setOnLinkBlueDeviceChange(LinkDeviceChange linkBlueDevices) {
        linkBlueDevice = linkBlueDevices;
    }
}
