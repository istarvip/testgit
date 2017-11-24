package com.walnutin.activity;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.walnutin.Jinterface.LinkDeviceChange;
import com.walnutin.eventbus.CommonBlueMsg;
import com.walnutin.hard.R;
import com.walnutin.util.LoadDataDialog;
import com.walnutin.util.MySharedPf;
import com.walnutin.view.SquareListView;
import com.yc.peddemo.sdk.BLEServiceOperate;
import com.yc.peddemo.sdk.DeviceScanInterfacer;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Set;

/**
 * Created by assa on 2016/5/26.
 */
public class LinkDetailActivity extends BaseActivity implements AdapterView.OnItemClickListener, View.OnClickListener {
    private ImageView blue_switch;

    private TextView bluet_finish;
    private TextView phone_type;
    private BluetoothAdapter bluetoothAdapter;
    private MySharedPf mySharedPf;
    private final long SCAN_PERIOD = 120 * 1000;
    private boolean mScanning;
    private Handler mHandler;

    private BroadcastReceiver mStatusReceiver;
    private LeDeviceListAdapter mLeDeviceListAdapter;
    private ConnectedListAdapter mConnectedListAdapter;

    private BLEServiceOperate mBLEServiceOperate;

    private DeviceScanInterfacer deviceScanInterfacer;
    private final int REQUEST_ENABLE_BT = 1;
    private SquareListView lv_device;
    private SquareListView lv_connected;
    private Set<BluetoothDevice> connectedDevices;
    //    private String status;
    private String mDevice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.watch_linkdetail);
        mHandler = new Handler();
        mySharedPf = MySharedPf.getInstance(this);
        mBLEServiceOperate = BLEServiceOperate
                .getInstance(getApplicationContext());// 用于BluetoothLeService实例化准备

        if (!mBLEServiceOperate.isSupportBle4_0()) {
            Toast.makeText(this, "设备不支持Bluetooth4.0", Toast.LENGTH_SHORT)
                    .show();
            finish();
            return;
        }
        EventBus.getDefault().register(this);
        deviceScanInterfacer = new DeviceScanOpen();
        mBLEServiceOperate.setDeviceScanListener(deviceScanInterfacer);
        openHandler.sendEmptyMessage(1);

        initView();
        initReceiver();
    }

    private void initView() {
        blue_switch = (ImageView) findViewById(R.id.blue_switch);
        blue_switch.setOnClickListener(this);
        phone_type = (TextView) findViewById(R.id.phone_type);
//        bluet_finish = (TextView) findViewById(R.id.watch_linkdetail_return);
//        bluet_finish.setOnClickListener(this);

        lv_device = (SquareListView) findViewById(R.id.lv_device);
        lv_connected = (SquareListView) findViewById(R.id.lv_connected);
        lv_device.setOnItemClickListener(this);
        // 获取对象
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        lv_connected.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String connected_device = mConnectedListAdapter.getDevice(position);
                showDialog();
                MyApplication.instance().startDeviceService();
            }
        });
        phone_type.setText("最近可被发现" + android.os.Build.MODEL);

        mLeDeviceListAdapter = new LeDeviceListAdapter(this);
        lv_device.setAdapter(mLeDeviceListAdapter);


        mConnectedListAdapter = new ConnectedListAdapter();
        lv_connected.setAdapter(mConnectedListAdapter);
    }


    private void initReceiver() {
        IntentFilter mFilter = new IntentFilter("ConnectedDevice");
        mStatusReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                disMissDialog();
                boolean connection_status = intent.getBooleanExtra("connection_status", false);
                if (connection_status) {
                    mDevice = intent.getStringExtra("deviceName");
//                status = intent.getStringExtra("connection_status");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            updateLinkedDevice();
                        }
                    });
                } else {
                    //     if(mConnectedListAdapter.mConnectedDv.contains(MySharedPf.getInstance(getApplicationContext()).getString("deviceName"))){
                    EventBus.getDefault().post(new CommonBlueMsg(false));
                    //       }
                }
            }
        };
        registerReceiver(mStatusReceiver, mFilter);
    }

    public boolean closeBluetooth = false;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.blue_switch:
                if (!closeBluetooth) {
                    scanLeDevice(false);
                    bluetoothAdapter.disable();
                    blue_switch.setBackgroundResource(R.drawable.closeblue);
                    lv_device.setVisibility(View.GONE);
                    lv_connected.setVisibility(View.GONE);
                    closeBluetooth = true;
                } else {

                    openHandler.sendEmptyMessage(1);
                }
                break;
//            case R.id.watch_linkdetail_return:
//                scanLeDevice(false);
//                finish();
//
//                break;
        }
    }

    LoadDataDialog loadDataDialog;

    void showDialog() {
        if (loadDataDialog == null || !loadDataDialog.isShowing()) {
            loadDataDialog = new LoadDataDialog(this, "link");
            loadDataDialog.show();
            loadDataDialog.setCanceledOnTouchOutside(false);
        }
    }

    void disMissDialog() {
        if (loadDataDialog.isShowing()) {
            loadDataDialog.dismiss();
        }
    }

    boolean isConnected = false;
    Intent intent;

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        final BluetoothDevice device = mLeDeviceListAdapter.getDevice(position);
        if (device == null)
            return;
        if (mScanning) {
            mBLEServiceOperate.stopLeScan();
            mScanning = false;
        }

        if (!MyApplication.isDevConnected()) {
            mySharedPf.setString("device_name", device.getName());
            mySharedPf.setString("device_address", device.getAddress());
            System.out.println("device.getName()==" + device.getName());
            showDialog();
            MyApplication.instance().startDeviceService();
            return;
        }
        // if (!MyApplication.isDevConnected()) {
        mySharedPf.setString("device_name", device.getName());
        mySharedPf.setString("device_address", device.getAddress());
        showDialog();
        MyApplication.instance().startDeviceService();
        //  }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Subscribe
    public void onResultBlueState(CommonBlueMsg msg) {
        MyApplication.instance().stopDeviceService();
        MyApplication.isDevConnected = false;
        scanDevice();
    }

    private void scanDevice() {
        if (!closeBluetooth) {
            lv_connected.setVisibility(View.VISIBLE);
            lv_device.setVisibility(View.VISIBLE);
        }
        // 开始搜索
        mLeDeviceListAdapter.clear();
        mConnectedListAdapter.clear();
        //获取已配对过的蓝牙设备
        connectedDevices = bluetoothAdapter.getBondedDevices();
        if (connectedDevices.size() > 0) {
            for (BluetoothDevice s : connectedDevices) {
                mConnectedListAdapter.addDevice(s.getName());
            }
            if (mConnectedListAdapter.mConnectedDv.contains(MySharedPf.getInstance(this).getString("device_name"))
                    && MyApplication.isDevConnected == false) {
                mDevice = "not linked";
                showDialog();
                MyApplication.instance().startDeviceService();
            } else if (MyApplication.isDevConnected) {
                mDevice = MySharedPf.getInstance(this).getString("device_name");
                if (!mConnectedListAdapter.mConnectedDv.contains(MySharedPf.getInstance(this).getString("device_name"))) {
                    updateLinkedDevice();
                    //        return;
                } else {
                    mConnectedListAdapter.notifyDataSetChanged();
                    //   scanLeDevice(false);
                    //   return;
                }
            }
        }
        scanLeDevice(true);
    }

    public void updateLinkedDevice() {

        mConnectedListAdapter.addDevice(mDevice);
        // scanLeDevice(false);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ENABLE_BT
                && resultCode == Activity.RESULT_CANCELED) {
            finish();
            return;
        }

        blue_switch.setBackgroundResource(R.drawable.openblue);
        closeBluetooth = false;
        //     bluetoothAdapter.enable();
        scanDevice();

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        unregisterReceiver(mStatusReceiver);
        mLeDeviceListAdapter.clear();
        //    mBLEServiceOperate.unBindService();// unBindService
        mHandler.removeCallbacksAndMessages(null);
        bluetoothAdapter.cancelDiscovery();
        scanLeDevice(false);
        EventBus.getDefault().unregister(this);
        deviceScanInterfacer = null;
        System.out.println("LinkDetail onDestory");
    }

    private void scanLeDevice(final boolean enable) {
        if (enable) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScanning = false;
                    mBLEServiceOperate.stopLeScan();
                }
            }, SCAN_PERIOD);

            mScanning = true;
            mBLEServiceOperate.startLeScan();
        } else {
            mScanning = false;
            mBLEServiceOperate.stopLeScan();
        }
    }


    public class LeDeviceListAdapter extends BaseAdapter {
        private ArrayList<BluetoothDevice> mLeDevices;
        private LayoutInflater mInflator;


        public LeDeviceListAdapter(Context context) {
            super();
            mLeDevices = new ArrayList<BluetoothDevice>();
            this.mInflator = LayoutInflater.from(context);
        }


        public void addDevice(BluetoothDevice device) {
            if (!mLeDevices.contains(device)) {
                mLeDevices.add(device);
                notifyDataSetChanged();
            }
        }

        public BluetoothDevice getDevice(int position) {
            return mLeDevices.get(position);
        }

        public void clear() {
            mLeDevices.clear();
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return mLeDevices.size();
        }

        @Override
        public Object getItem(int position) {
            return mLeDevices.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            // General ListView optimization code.
            if (convertView == null) {
                convertView = mInflator.inflate(R.layout.watch_linklistitem, null);
                viewHolder = new ViewHolder();
                viewHolder.deviceName = (TextView) convertView
                        .findViewById(R.id.tv_device);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            BluetoothDevice device = mLeDevices.get(position);
            final String deviceName = device.getName();
            if (deviceName != null && deviceName.length() > 0) {
                viewHolder.deviceName.setText(deviceName);
            } else {
                viewHolder.deviceName.setText("未知设备");
            }
            return convertView;
        }

    }

    static class ViewHolder {
        TextView deviceName;
    }

    //已连接adapter
    public class ConnectedListAdapter extends BaseAdapter {
        public ArrayList<String> mConnectedDv;
        private LayoutInflater cInflator;


        public ConnectedListAdapter() {
            super();
            mConnectedDv = new ArrayList<String>();
            cInflator = LinkDetailActivity.this.getLayoutInflater();
        }


        public void addDevice(String device) {
            if (!mConnectedDv.contains(device)) {
            } else {
                mConnectedDv.remove(device);
            }
            mConnectedDv.add(0, device);
            notifyDataSetChanged();
        }

        public String getDevice(int position) {
            return mConnectedDv.get(position);
        }

        public void clear() {
            mConnectedDv.clear();
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return mConnectedDv.size();
        }

        @Override
        public Object getItem(int position) {
            return mConnectedDv.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            AViewHolder aViewHolder = null;
            // General ListView optimization code.
            if (convertView == null) {
                convertView = cInflator.inflate(R.layout.watch_connectedlistitem, null);
                aViewHolder = new AViewHolder();
                aViewHolder.connectedDevice = (TextView) convertView
                        .findViewById(R.id.connected_device);
                aViewHolder.tv_connected = (TextView) convertView.findViewById(R.id.tv_connected);
                aViewHolder.lv_warn = (ImageView) convertView.findViewById(R.id.lv_warn);
                aViewHolder.tv_connected = (TextView) convertView.findViewById(R.id.tv_connected);
                aViewHolder.lv_warn = (ImageView) convertView.findViewById(R.id.lv_warn);
                convertView.setTag(aViewHolder);
            } else {
                aViewHolder = (AViewHolder) convertView.getTag();
            }
            String ConnectedDevice = mConnectedDv.get(position);
            aViewHolder.connectedDevice.setText(ConnectedDevice);

            if (ConnectedDevice.equals(mDevice)) {
                aViewHolder.tv_connected.setVisibility(View.VISIBLE);
                aViewHolder.lv_warn.setVisibility(View.VISIBLE);
                aViewHolder.lv_warn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(LinkDetailActivity.this, NeglectDeviceActivity.class);
                        intent.putExtra("deviceaddr", MySharedPf.getInstance(getApplicationContext()).getString("device_address"));
                        startActivity(intent);
                    }
                });
            } else {
                aViewHolder.tv_connected.setVisibility(View.GONE);
                aViewHolder.lv_warn.setVisibility(View.GONE);
            }

            return convertView;
        }

        class AViewHolder {
            public TextView connectedDevice;
            public TextView tv_connected;
            public ImageView lv_warn;
        }

    }


    private class DeviceScanOpen implements DeviceScanInterfacer {

        @Override
        public void LeScanCallback(final BluetoothDevice device, int i) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    System.out.println("LinkDetail device: " + " -- " + LinkDetailActivity.this + " -- " + device.getName());
                    mLeDeviceListAdapter.addDevice(device);
                }
            });
        }
    }

    private final Handler openHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    if (!mBLEServiceOperate.isBleEnabled()) {
                        Intent enableBtIntent = new Intent(
                                BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                    } else {
                        blue_switch.setBackgroundResource(R.drawable.openblue);
                        scanDevice();
                    }
                    break;
            }
        }
    };


}




