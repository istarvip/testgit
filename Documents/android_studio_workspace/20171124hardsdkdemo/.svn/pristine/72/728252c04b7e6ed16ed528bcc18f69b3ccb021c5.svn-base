package com.walnutin.hardsdkdemo;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.walnutin.hardsdkdemo.ProductList.HardSdk;
import com.walnutin.hardsdkdemo.ProductNeed.Jinterface.IHardScanCallback;
import com.walnutin.hardsdkdemo.ProductNeed.Jinterface.IHardSdkCallback;
import com.walnutin.hardsdkdemo.ProductNeed.entity.Device;
import com.walnutin.hardsdkdemo.utils.GlobalValue;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chenliu on 2017/4/14.
 */

public class SearchDeviceActivity extends Activity implements IHardScanCallback, IHardSdkCallback {

    private RecyclerView mRecyclerView;
    private MyAdapter mMyAdapter;
    private final String TAG = SearchDeviceActivity.class.getSimpleName();
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        initView();
        initEvent();
    }


    private void initView() {
        mRecyclerView = (RecyclerView) findViewById(R.id.search_result);
        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);


    }

    private void initEvent() {
        mMyAdapter = new MyAdapter();
        mRecyclerView.setAdapter(mMyAdapter);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        HardSdk.getInstance().setHardScanCallback(this);
        HardSdk.getInstance().setHardSdkCallback(this);
        if (!HardSdk.getInstance().isSupportBle4_0()) {
            Toast.makeText(HardSdk.getInstance().getContext(), "当前设备不支持ble4.0", Toast.LENGTH_LONG).show();
            finish();
        }
        if (!HardSdk.getInstance().isBleEnabled()) {
            Toast.makeText(HardSdk.getInstance().getContext(), "请打开蓝牙后再进行搜索", Toast.LENGTH_LONG).show();
            finish();
        }
        HardSdk.getInstance().startScan();
    }


    @Override
    public void onFindDevice(BluetoothDevice device, int rssi, String factoryNameByUUID, byte[] scanRecord) {
        Log.d(TAG, "onFindDevice: device:" + device.getName() + " " + device.getAddress());
        mMyAdapter.addDevice(new Device(factoryNameByUUID, device.getName(), device.getAddress()));
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        HardSdk.getInstance().stopScan();
        HardSdk.getInstance().removeHardScanCallback(this);
    }

    @Override
    public void onCallbackResult(int flag, boolean state, Object obj) {
        if (flag == GlobalValue.CONNECTED_MSG) {
            Log.d(TAG, "onCallbackResult: 连接成功");
            Toast.makeText(this, "连接成功", Toast.LENGTH_LONG).show();
            mProgressBar.setVisibility(View.GONE);
            finish();
        } else if (flag == GlobalValue.DISCONNECT_MSG) {
            Log.d(TAG, "onCallbackResult: 连接失败");
            Toast.makeText(this, "连接断开", Toast.LENGTH_LONG).show();
            mProgressBar.setVisibility(View.GONE);
            HardSdk.getInstance().startScan();
        } else if (flag == GlobalValue.CONNECT_TIME_OUT_MSG) {
            Log.d(TAG, "onCallbackResult: 连接超时");
            Toast.makeText(this, "连接超时", Toast.LENGTH_LONG).show();
            mProgressBar.setVisibility(View.GONE);
            HardSdk.getInstance().startScan();
        }
    }

    @Override
    public void onStepChanged(int step, float distance, int calories, boolean finish_status) {

    }

    @Override
    public void onSleepChanged(int lightTime, int deepTime, int sleepAllTime, int[] sleepStatusArray, int[] timePointArray, int[] duraionTimeArray) {

    }

    @Override
    public void onHeartRateChanged(int rate, int status) {

    }

    @Override
    public void onBloodPressureChanged(int hightPressure, int lowPressure, int status) {

    }

    private class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> implements View.OnClickListener {
        private List<Device> deviceInfoList = new ArrayList<>();

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View layout = LayoutInflater.from(SearchDeviceActivity.this).inflate(R.layout.item_recyclerview, parent, false);
            layout.setOnClickListener(this);
            MyViewHolder myViewHolder = new MyViewHolder(layout);
            return myViewHolder;
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            if (holder.deviceText.getTag(R.id.tag_listener) == null) {
                holder.deviceText.setOnClickListener(this);
                holder.deviceText.setTag(R.id.tag_listener, true);
            }
            holder.deviceText.setTag(R.id.tag_position, position);
            Log.d(TAG, "onBindViewHolder: deviceInfoList:" + deviceInfoList.size() + "  positon:" + position + "  deviceInfoList.get(position)" + deviceInfoList.get(position));
            holder.deviceText.setText(deviceInfoList.get(position).getDeviceName() + " " + deviceInfoList.get(position).getDeviceAddr());
        }

        @Override
        public int getItemCount() {
            return deviceInfoList.size();
        }

        @Override
        public void onClick(View view) {
            if (view != null) {
                Object tag = view.getTag(R.id.tag_position);
                if (tag != null) {
                    int positon = (int) tag;
                    Device device = deviceInfoList.get(positon);
                    HardSdk.getInstance().stopScan();
                    HardSdk.getInstance().connectBracelet(device.getFactoryName(), device.getDeviceName(), device.getDeviceAddr());
                    mMyAdapter.clearData();
                    mProgressBar.setVisibility(View.VISIBLE);
                }
            }
        }


        public class MyViewHolder extends RecyclerView.ViewHolder {

            public TextView deviceText;

            public MyViewHolder(View itemView) {
                super(itemView);
                initHolderView(itemView);
            }

            private void initHolderView(View itemView) {
                deviceText = (TextView) itemView.findViewById(R.id.item_recyclerview);
            }
        }

        public void addDevice(com.walnutin.hardsdkdemo.ProductNeed.entity.Device deviceInfo) {

            for (Device info : deviceInfoList) {
                if (info.getDeviceAddr().equals(deviceInfo.getDeviceAddr())) {
                    return;
                }
            }
            deviceInfoList.add(deviceInfo);
            notifyDataSetChanged();
        }


        public void clearData() {
            deviceInfoList.clear();
            notifyDataSetChanged();
        }
    }
}
