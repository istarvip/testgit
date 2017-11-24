package com.walnutin.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.telephony.TelephonyManager;
import android.view.Window;
import android.widget.Toast;

import com.baidu.location.LocationClient;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapView;
import com.baidu.trace.LBSTraceClient;
import com.baidu.trace.LocationMode;
import com.baidu.trace.OnEntityListener;
import com.baidu.trace.OnStartTraceListener;
import com.baidu.trace.Trace;
import com.baidu.trace.TraceLocation;
import com.walnutin.hard.R;
import com.walnutin.util.TrackUploadUtil;

public class MapActivity extends FragmentActivity {
    /**
     * 轨迹服务
     */
    public static Trace trace = null;
    /**
     * entity标识
     */
    public static String entityName = null;
    /**
     * 鹰眼服务ID，开发者创建的鹰眼服务对应的服务ID
     */
    public static long serviceId = 115798;
    /**
     * 轨迹服务类型（0 : 不建立socket长连接， 1 : 建立socket长连接但不上传位置数据，2 : 建立socket长连接并上传位置数据）
     */
    private int traceType = 2;
    /**
     * 轨迹服务客户端
     */
    public static LBSTraceClient client = null;
    /**
     * Entity监听器
     */
    public static OnEntityListener entityListener = null;
    private TrackUploadUtil trackUploadUtil;
    public static MapView bmapView = null;
    public static BaiduMap mBaiduMap = null;
    public static Context mContext = null;
    public LocationClient mLocationClient = null;
    public static OnStartTraceListener startTraceListener = null;
    public MapActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_mapactivity);
        mContext = this;
        // 初始化轨迹服务客户端
        client = new LBSTraceClient(mContext);
        // 设置定位模式
        client.setLocationMode(LocationMode.High_Accuracy);
        // 初始化entity标识
        entityName = "myTrace";
        // 初始化轨迹服务
        trace = new Trace(getApplicationContext(), serviceId, entityName, traceType);
        // 初始化OnEntityListener
        initOnEntityListener();
        client.addEntity(serviceId, entityName, "name=test", entityListener);
        // 初始化组件
        initComponent();
        trackUploadUtil = new TrackUploadUtil();
    }

    private void initComponent() {
        // TODO Auto-generated method stub
        bmapView = (MapView) findViewById(R.id.bmapView);
        mBaiduMap = bmapView.getMap();
        bmapView.showZoomControls(false);

    }


    /**
     * 初始化OnEntityListener
     */
    private void initOnEntityListener() {

        entityListener = new OnEntityListener() {
            // 请求失败回调接口
            @Override
            public void onRequestFailedCallback(String arg0) {
                // TODO Auto-generated method stub
                System.out.println("entity请求失败回调接口消息 : " + arg0);
            }
            // 添加entity回调接口
            public void onAddEntityCallback(String arg0) {
                // TODO Auto-generated method stub
                Toast.makeText(MapActivity.this, "添加entity回调接口消息 : " + arg0, Toast.LENGTH_LONG).show();
            }
            // 查询entity列表回调接口
            @Override
            public void onQueryEntityListCallback(String message) {
                // TODO Auto-generated method stub
                System.out.println("entityList回调消息 : " + message);
            }
            @Override
            public void onReceiveLocation(TraceLocation location) {
                // TODO Auto-generated method stub
                if (trackUploadUtil != null) {
                    trackUploadUtil.showRealtimeTrack(location);
                    System.out.println("获取到实时位置:" + location.toString());

                }
            }

        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        trackUploadUtil.Start();
        trackUploadUtil.startRefreshThread(true);
        trackUploadUtil.addMarker();
        System.out.println("MapActivity  onStart....");
     //   mBaiduMap.setOnMapClickListener(null);
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        System.out.println("MapActivity  Pause....");
        super.onPause();
        trackUploadUtil.stopTrance();
        TrackUploadUtil.isInUploadFragment = false;
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        // 清空地图覆盖物
        System.out.println("MapActivity  ondestrou....");
        mBaiduMap.clear();
        client.onDestroy();
        // android.os.Process.killProcess(android.os.Process.myPid());
     //   client.stopTrace(trace,stopTraceListener);
  //      android.os.Process.killProcess(android.os.Process.myPid());
    }

    /**
     * 获取设备IMEI码
     *
     * @param context
     * @return
     */
    public static String getImei(Context context) {
        String mImei = "NULL";
        try {
            mImei = ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
        } catch (Exception e) {
            System.out.println("获取IMEI码失败");
            mImei = "NULL";
        }
        return mImei;
    }


//停止轨迹服务



}
