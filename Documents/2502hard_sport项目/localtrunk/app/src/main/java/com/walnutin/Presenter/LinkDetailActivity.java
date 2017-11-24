package com.walnutin.Presenter;

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

import com.walnutin.ViewInf.DeviceLinkView;
import com.walnutin.activity.BaseActivity;
import com.walnutin.activity.MyApplication;
import com.walnutin.activity.NeglectDeviceActivity;
import com.walnutin.entity.Device;
import com.walnutin.eventbus.CommonBlueMsg;
import com.walnutin.hard.R;
import com.walnutin.util.LoadDataDialog;
import com.walnutin.util.MySharedPf;
import com.walnutin.view.SquareListView;
import com.walnutin.view.TopTitleLableView;


import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by assa on 2016/5/26.
 */
public class LinkDetailActivity extends BaseActivity implements AdapterView.OnItemClickListener, View.OnClickListener, DeviceLinkView {
    private ImageView blue_switch;
    private TextView bluet_finish;
    private TextView phone_type;
    private BluetoothAdapter bluetoothAdapter;
    private MySharedPf mySharedPf;
    private final long SCAN_PERIOD = 120 * 1000;
    private boolean mScanning;
    private Handler mHandler;
    private LeDeviceListAdapter mLeDeviceListAdapter;
    private ConnectedListAdapter mConnectedListAdapter;
    private final int REQUEST_ENABLE_BT = 1;
    private SquareListView lv_device;
    private SquareListView lv_connected;
    public List<Device> mConnectedDv = new ArrayList<>();
    private List<BluetoothDevice> mLeDevices;
    boolean connectState = false;
    LinkPresenter linkPresenter;
    String connectName = "un link";
    String connectAddr = "un link";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.watch_linkdetail);
        mHandler = new Handler();
        mySharedPf = MySharedPf.getInstance(this);
        linkPresenter = new LinkPresenter(this, this);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!linkPresenter.isSupportBle4_0()) {
            Toast.makeText(this, "设备不支持Bluetooth4.0", Toast.LENGTH_SHORT)
                    .show();
            finish();
            return;
        }
        EventBus.getDefault().register(this);
        openHandler.sendEmptyMessage(1);
        initView();
        linkPresenter.initReceiver();
        topTitleLableView.setOnBackListener(new TopTitleLableView.OnBackListener() {
            @Override
            public void onClick() {
                linkPresenter.stopScan();
                finish();
            }
        });
    }

    private void initView() {
        blue_switch = (ImageView) findViewById(R.id.blue_switch);
        blue_switch.setOnClickListener(this);
        phone_type = (TextView) findViewById(R.id.phone_type);
        lv_device = (SquareListView) findViewById(R.id.lv_device);
        lv_connected = (SquareListView) findViewById(R.id.lv_connected);
        lv_device.setOnItemClickListener(this);
        lv_connected.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Device connected_device = mConnectedListAdapter.getDevice(position);
                mySharedPf.setString("device_name", connected_device.getDeviceName());
                mySharedPf.setString("device_address", connected_device.getDeviceAddr());
                MyApplication.instance().startDeviceService();
            }
        });
        phone_type.setText("最近可被发现" + android.os.Build.MODEL);
        mLeDeviceListAdapter = new LeDeviceListAdapter(this);
        lv_device.setAdapter(mLeDeviceListAdapter);
        mConnectedListAdapter = new ConnectedListAdapter();
        lv_connected.setAdapter(mConnectedListAdapter);

        linkPresenter.getLocalLinkedDeviceList();  //加载本地存储已连接的设备


    }

    // 检测连接状态
    public void checkLinkState() {
        if (!MyApplication.isDevConnected) {       //尝试从已连接最近的列表中连接
            for (Device s : mConnectedDv) {
                if (s.getDeviceName().equals(MySharedPf.getInstance(this).getString("device_name"))) {
                    showDialog();
                    MyApplication.instance().startDeviceService();
                }
            }
        } else {
            connectName = MySharedPf.getInstance(this).getString("device_name");
            connectAddr = MySharedPf.getInstance(this).getString("device_address");
            mConnectedListAdapter.notifyDataSetChanged();
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
        if (loadDataDialog != null && loadDataDialog.isShowing()) {
            loadDataDialog.dismiss();
        }
    }


    public boolean closeBluetooth = false;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.blue_switch:
                if (!closeBluetooth) {
                    linkPresenter.stopScan();
                    bluetoothAdapter.disable();
                    blue_switch.setBackgroundResource(R.drawable.closeblue);
                    lv_device.setVisibility(View.GONE);
                    lv_connected.setVisibility(View.GONE);
                    closeBluetooth = true;
                } else {
                    openHandler.sendEmptyMessage(1);
                }
                break;

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
            linkPresenter.stopScan();
            mScanning = false;
        }
        if (!MyApplication.isDevConnected()) {
            mySharedPf.setString("device_name", device.getName());
            mySharedPf.setString("device_address", device.getAddress());
            showDialog();
            MyApplication.instance().startDeviceService();
            return;
        }
        if (device.getName().equals(connectName) && device.getAddress().equals(connectAddr)) {
            return;
        }
        mySharedPf.setString("device_name", device.getName());
        mySharedPf.setString("device_address", device.getAddress());
        showDialog();
        MyApplication.instance().startDeviceService();

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Subscribe
    public void onResultBlueState(CommonBlueMsg msg) {       // 连接 断开
        if (msg.neglect == true) {      //忽略设备
            linkPresenter.removeDevice(new Device(connectName, connectAddr));
        }
        disMissDialog();
        MyApplication.instance().stopDeviceService();
        MyApplication.isDevConnected = false;
        connectName = "un link";
        scanDevice();

    }

    @Override
    protected void onPause() {
        super.onPause();
        linkPresenter.saveLinkedDevice();
    }

    private void scanDevice() {
        if (!closeBluetooth) {
            lv_connected.setVisibility(View.VISIBLE);
            lv_device.setVisibility(View.VISIBLE);
        } else {
            return;
        }

        checkLinkState();
        linkPresenter.clearDeviceList();
        linkPresenter.startScan();

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mScanning = false;
                linkPresenter.stopScan();
            }
        }, SCAN_PERIOD);


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
        scanDevice();

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        linkPresenter.unRegisterBroad();
        mHandler.removeCallbacksAndMessages(null);
        bluetoothAdapter.cancelDiscovery();
        linkPresenter.stopScan();
        EventBus.getDefault().unregister(this);
        System.out.println("LinkDetail onDestory");
    }


    @Override
    public List<BluetoothDevice> getScanListDevices() {
        return null;
    }

    @Override
    public List<BluetoothDevice> getLinkedListDevices() {
        return null;
    }

    @Override
    public void updateScanListDevices(List list) {
        //    mLeDevices = list;
        mLeDeviceListAdapter.setLeDeviceList(list);

    }

    @Override
    public void updateLinkedListDevices(List list) {
        //   mConnectedDv = list;
        mConnectedListAdapter.setConnectDeviceList(list);

    }

    @Override
    public void updateLinkState(boolean state) {
        connectState = state;
    }

    @Override
    public void switchBlueBottomState(boolean state) {

    }

    @Override
    public void connectedDeviceName(String deviceName, String addr) {
        connectName = deviceName;
        connectAddr = addr;
        linkPresenter.addLinkedDevice(new Device(connectName, connectAddr));
        mConnectedListAdapter.notifyDataSetChanged();
        disMissDialog();
    }


    public class LeDeviceListAdapter extends BaseAdapter {
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

        public void setLeDeviceList(List l) {
            mLeDevices = (ArrayList<BluetoothDevice>) l;
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

        class ViewHolder {
            TextView deviceName;
        }
    }


    //已连接adapter
    public class ConnectedListAdapter extends BaseAdapter {
        private LayoutInflater cInflator;

        public ConnectedListAdapter() {
            super();
            mConnectedDv = new ArrayList<Device>();
            cInflator = LinkDetailActivity.this.getLayoutInflater();
        }

        public void addDevice(Device device) {
            if (!mConnectedDv.contains(device)) {
            } else {
                mConnectedDv.remove(device);
            }
            mConnectedDv.add(0, device);
            notifyDataSetChanged();
        }

        public void setConnectDeviceList(List l) {
            mConnectedDv = (ArrayList<Device>) l;
            notifyDataSetChanged();
        }

        public Device getDevice(int position) {
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
            Device ConnectedDevice = mConnectedDv.get(position);
            aViewHolder.connectedDevice.setText(ConnectedDevice.getDeviceName());

            if (ConnectedDevice.getDeviceName().equals(connectName) && ConnectedDevice.getDeviceAddr().equals(connectAddr)) {
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


    // @Override
    public void LeScanCallback(final BluetoothDevice device, final int rssi) {
        // TODO Auto-generated method stub
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                System.out.println("LinkDetail device: " + " -- " + LinkDetailActivity.this + " -- " + device.getName());
                mLeDeviceListAdapter.addDevice(device);
            }
        });
    }


    private final Handler openHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    if (!linkPresenter.isBleOpen()) {
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




