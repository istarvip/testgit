package com.walnutin.Presenter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.walnutin.Jinterface.LinkDeviceChange;
import com.walnutin.Model.LinkDeviceImpl;
import com.walnutin.Model.LinkDeviceIntf;
import com.walnutin.ViewInf.DeviceLinkView;
import com.walnutin.activity.MyApplication;
import com.walnutin.entity.Device;
import com.walnutin.eventbus.CommonBlueMsg;

import org.greenrobot.eventbus.EventBus;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

/**
 * 作者：MrJiang on 2016/7/14 15:34
 */
public class LinkPresenter implements LinkDeviceChange {
    private LinkDeviceIntf linkDeviceIntf;
    private DeviceLinkView deviceLinkView;
    Context mContext;
    private BroadcastReceiver mStatusReceiver;


    public LinkPresenter(DeviceLinkView dlv, Context context) {
        deviceLinkView = dlv;
        linkDeviceIntf = new LinkDeviceImpl(MyApplication.getContext());
        mContext = context;
        initReceiver();
        linkDeviceIntf.setOnLinkBlueDeviceChange(this);
    }

    public boolean isSupportBle4_0() {
        return linkDeviceIntf.isSupportBle4_0();
    }

    public boolean isBleOpen() {
        return linkDeviceIntf.isBleOpened();
    }

    public void startScan() {
        linkDeviceIntf.startScan();
    }

    public void stopScan() {
        linkDeviceIntf.stopScan();
    }

    public void clearDeviceList() {
        linkDeviceIntf.clearScanAndLinkedList();
    }

    public List getScanDeviceList() {
        return linkDeviceIntf.getScanListDevice();
    }

    public List getLinkedDeviceList() {
        return linkDeviceIntf.getLinkedDevices();
    }

    public void removeDevice(Device device) {
        linkDeviceIntf.removeLinkedDevice(device);
    }

    public void addLinkedDevice(Device device) {
        linkDeviceIntf.addLinkedDevice(device);
    }

    public void saveLinkedDevice(){
        linkDeviceIntf.saveLinked();
    }

    public List getLocalLinkedDeviceList() {
        return linkDeviceIntf.getLocalLinkedList();
    }

    public void initReceiver() {
        if(mStatusReceiver!=null){
            return;
        }
        IntentFilter mFilter = new IntentFilter("ConnectedDevice");
        mStatusReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                boolean connection_status = intent.getBooleanExtra("connection_status", false);
                if (connection_status) {
                    String mDevice = intent.getStringExtra("deviceName");
                    String mDeviceAddr = intent.getStringExtra("deviceAddr");
                    deviceLinkView.connectedDeviceName(mDevice, mDeviceAddr);
                } else {
                    EventBus.getDefault().post(new CommonBlueMsg(false));
                }
            }
        };
        mContext.registerReceiver(mStatusReceiver, mFilter);
    }

    public void unRegisterBroad() {
        if (mStatusReceiver != null) {
            mContext.unregisterReceiver(mStatusReceiver);
        }
    }


    @Override
    public void onLinkedDeviceChange(List list) {
        deviceLinkView.updateLinkedListDevices(list);
    }

    @Override
    public void onScanDeviceChanged(List list) {
        deviceLinkView.updateScanListDevices(list);
    }
}
