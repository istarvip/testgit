package com.walnutin.util;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Looper;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.view.LayoutInflater;
import android.view.View;

import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.CoordinateConverter;
import com.baidu.mapapi.utils.CoordinateConverter.CoordType;
import com.baidu.trace.OnStartTraceListener;
import com.baidu.trace.OnStopTraceListener;
import com.baidu.trace.TraceLocation;
import com.walnutin.activity.MapActivity;
import com.walnutin.service.MonitorService;
import com.walnutin.activity.PowerReceiver;
import com.walnutin.hard.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 轨迹追踪
 */
public class TrackUploadUtil {

    /**
     * 开启轨迹服务监听器
     */
    protected static OnStartTraceListener startTraceListener = null;
    /**
     * 停止轨迹服务监听器
     */
    protected static OnStopTraceListener stopTraceListener = null;
    /**
     * 采集周期（单位 : 秒）
     */
    private int gatherInterval = 2;
    /**
     * 打包周期（单位 : 秒）
     */
    private int packInterval = 4;
    /**
     * 图标
     */
    private static BitmapDescriptor realtimeBitmap;
    // 覆盖物
    protected static OverlayOptions overlay;
    // 路线覆盖物
    private static PolylineOptions polyline = null;
    private static List<LatLng> pointList = new ArrayList<LatLng>();
    protected boolean isTraceStart = false;
    private Intent serviceIntent = null;
    /**
     * 刷新地图线程(获取实时点)
     */
    protected RefreshThread refreshThread = null;
    protected static MapStatusUpdate msUpdate = null;
    private View view = null;
    private LayoutInflater mInflater = null;
    public static boolean isInUploadFragment = true;
    private static boolean isRegister = false;
    public static PowerManager pm = null;
    public static WakeLock wakeLock = null;
    private PowerReceiver powerReceiver = new PowerReceiver();

    public void Start() {
        // TODO Auto-generated method stub
        System.out.println(" start...........track");
        startTrance();
        // 初始化
        startTrace();
        // 初始化监听器
        initListener();
        // 设置采集周期
        setInterval();
        // 设置http请求协议类型
        setRequestType();
    }
    /**
     * 初始化
     */
    void startTrance() {
        //startTrace();
        if (!isRegister) {
            if (null == pm) {
                pm = (PowerManager) MapActivity.mContext.getSystemService(Context.POWER_SERVICE);
            }
            if (null == wakeLock) {
                wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "track upload");
            }
            IntentFilter filter = new IntentFilter();
            filter.addAction(Intent.ACTION_SCREEN_OFF);
            filter.addAction(Intent.ACTION_SCREEN_ON);
            MapActivity.mContext.registerReceiver(powerReceiver, filter);
            isRegister = true;
        }
    }

    public  void stopTrance() {
        stopTrace();
        if (isRegister) {
            try {
                MapActivity.mContext.unregisterReceiver(powerReceiver);
                isRegister = false;
            } catch (Exception e) {
                // TODO: handle
            }
        }
    }

    public void startMonitorService() {
        serviceIntent = new Intent(MapActivity.mContext,
                MonitorService.class);
        MapActivity.mContext.startService(serviceIntent);
    }

    /**
     * 初始化监听器
     */
    private void initListener() {
        // 初始化开启轨迹服务监听器
        initOnStartTraceListener();
        // 初始化停止轨迹服务监听器
        initOnStopTraceListener();
    }

    /**
     * 开启轨迹服务
     */
    private void startTrace() {
        // 通过轨迹服务客户端client开启轨迹服务
        MapActivity.client.startTrace(MapActivity.trace, startTraceListener);

        if (!MonitorService.isRunning) {
            // 开启监听service
            MonitorService.isCheck = true;
            MonitorService.isRunning = true;
            startMonitorService();
        }
    }

    /**
     * 停止轨迹服务
     */
    private void stopTrace() {
        // 通过轨迹服务客户端client停止轨迹服务
        MapActivity.client.stopTrace(MapActivity.trace, stopTraceListener);
        // 停止监听service
        MonitorService.isCheck = false;
        MonitorService.isRunning = false;
        MapActivity.mContext.stopService(serviceIntent);
    }

    /**
     * 设置采集周期和打包周期
     */
    private void setInterval() {
        MapActivity.client.setInterval(gatherInterval, packInterval);
    }

    /**
     * 设置请求协议
     */
    protected static void setRequestType() {
        int type = 0;
        MapActivity.client.setProtocolType(type);
    }

    /**
     * 查询实时轨迹
     */
    private void queryRealtimeLoc() {
        MapActivity.client.queryRealtimeLoc(MapActivity.serviceId, MapActivity.entityListener);
    }

    /**
     * 查询entityList
     */
    @SuppressWarnings("unused")
    private void queryEntityList() {
        // // entity标识列表（多个entityName，以英文逗号"," 分割）
        String entityNames = MapActivity.entityName;
        // 属性名称（格式为 : "key1=value1,key2=value2,....."）
        String columnKey = "key1=value1,key2=value2";
        // 返回结果的类型（0 : 返回全部结果，1 : 只返回entityName的列表）
        int returnType = 0;
        // 活跃时间（指定该字段时，返回从该时间点之后仍有位置变动的entity的实时点集合）
        // int activeTime = (int) (System.currentTimeMillis() / 1000 - 30);
        int activeTime = 0;
        // 分页大小
        int pageSize = 10;
        // 分页索引
        int pageIndex = 1;
        MapActivity.client.queryEntityList(MapActivity.serviceId, entityNames, columnKey, returnType, activeTime,
                pageSize,
                pageIndex, MapActivity.entityListener);
    }

    /**
     * 初始化OnStartTraceListener
     */
    private void initOnStartTraceListener() {
        // 初始化startTraceListener
        startTraceListener = new OnStartTraceListener() {
            // 开启轨迹服务回调接口（arg0 : 消息编码，arg1 : 消息内容，详情查看类参考）
            public void onTraceCallback(int arg0, String arg1) {
                // TODO Auto-generated method stub
                //showMessage("开启轨迹服务回调接口消息 [消息编码 : " + arg0 + "，消息内容 : " + arg1 + "]", Integer.valueOf(arg0));
                if (0 == arg0 || 10006 == arg0 || 10008 == arg0 || 10009 == arg0) {
                    isTraceStart = true;
                    System.out.println(" onTraceCallback"+arg0+" -- "+arg1);
                    // startRefreshThread(true);
                }
            }

            @Override
            public void onTracePushCallback(byte arg0, String arg1) {
                // TODO Auto-generated method stub
                System.out.println("onTracePushCallback "+arg0+" -- "+arg1);
            }

        };
    }

    /**
     * 初始化OnStopTraceListener
     */
    private void initOnStopTraceListener() {
        // 初始化stopTraceListener
        stopTraceListener = new OnStopTraceListener() {

            // 轨迹服务停止成功
            public void onStopTraceSuccess() {
                // TODO Auto-generated method stub
                //    showMessage("停止轨迹服务成功", Integer.valueOf(1));
                isTraceStart = false;
                startRefreshThread(false);
            }

            // 轨迹服务停止失败（arg0 : 错误编码，arg1 : 消息内容，详情查看类参考）
            public void onStopTraceFailed(int arg0, String arg1) {
                // TODO Auto-generated method stub
                //   showMessage("停止轨迹服务接口消息 [错误编码 : " + arg0 + "，消息内容 : " + arg1 + "]", null);
                startRefreshThread(false);
            }
        };
    }

    protected class RefreshThread extends Thread {

        protected boolean refresh = true;

        @Override
        public void run() {
            // TODO Auto-generated method stub
            Looper.prepare();
            while (refresh) {
                // 查询实时位置
                queryRealtimeLoc();
                System.out.println("refresh:");
                try {
                    Thread.sleep(gatherInterval * 1000);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    System.out.println("线程休眠失败");
                }
            }
            Looper.loop();
        }
    }

    /**
     * 显示实时轨迹
     */
    public void showRealtimeTrack(TraceLocation location) {

        if (null == refreshThread || !refreshThread.refresh) {
            return;
        }

        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        if (Math.abs(latitude - 0.0) < 0.000001 && Math.abs(longitude - 0.0) < 0.000001) {
//            showMessage("当前查询无轨迹点",
//                    null);
        } else {
            LatLng latLng = new LatLng(latitude, longitude);
            if (1 == location.getCoordType()) {
                LatLng sourceLatLng = latLng;
                CoordinateConverter converter = new
                        CoordinateConverter();
                converter.from(CoordType.GPS);
                converter.coord(sourceLatLng);
                latLng =
                        converter.convert();
            }
            pointList.add(latLng);
            if (isInUploadFragment) {
                // 绘制实时点
                drawRealtimePoint(latLng);
            }

        }

    }

    /**
     * 绘制实时点
     */
    private void drawRealtimePoint(LatLng point) {
        MapActivity.mBaiduMap.clear();
        MapStatus mMapStatus = new MapStatus.Builder().target(point).zoom(19).build();
        msUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
        realtimeBitmap = BitmapDescriptorFactory
                .fromResource(R.drawable.icon_gcoding);
        overlay = new MarkerOptions().position(point)
                .icon(realtimeBitmap).zIndex(9).draggable(true);
        if (pointList.size() >= 2 && pointList.size() <= 10000) {
            System.out.println("pointListSize:"+pointList.size());
            // 添加路线（轨迹）
            polyline = new PolylineOptions().width(6).dottedLine(true)
                    .color(Color.BLUE).points(pointList);
        }

        addMarker();

    }

    /**
     * 添加地图覆盖物
     */
    public static void addMarker() {
      //  Toast.makeText(MyApplication.getContext(), "addMark", Toast.LENGTH_SHORT).show();
        if (null != msUpdate) {
            MapActivity.mBaiduMap.setMapStatus(msUpdate);
        }
        // 路线覆盖物
        if (null != polyline) {
            MapActivity.mBaiduMap.addOverlay(polyline);
        }
        // 实时点覆盖物
        if (null != overlay) {
            MapActivity.mBaiduMap.addOverlay(overlay);
        }
    }

    public void startRefreshThread(boolean isStart) {
        if (null == refreshThread) {
            refreshThread = new RefreshThread();
        }
        refreshThread.refresh = isStart;
        if (isStart) {
            if (!refreshThread.isAlive()) {
                refreshThread.start();
            }
        } else {
            refreshThread = null;
        }
    }

}
