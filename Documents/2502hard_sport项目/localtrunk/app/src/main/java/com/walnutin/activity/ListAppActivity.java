package com.walnutin.activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.walnutin.manager.NoticeInfoManager;
import com.walnutin.adapter.AppListAdapter;
import com.walnutin.entity.NoticeDevice;
import com.walnutin.hard.R;
import com.walnutin.util.Conversion;
import com.walnutin.util.LoadDataDialog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by assa on 2016/5/24.
 */
public class ListAppActivity extends BaseActivity {

    ListView listSoftView;
    List<NoticeDevice> noticeInfoList = new ArrayList<>();
    AppListAdapter appListAdapter;
    NoticeInfoManager noticeInfoManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listsoft);
        listSoftView = (ListView) findViewById(R.id.appListView);
        appListAdapter = new AppListAdapter(this);
        listSoftView.setAdapter(appListAdapter);
        noticeInfoManager = NoticeInfoManager.getInstance(this.getApplicationContext());

        initData();



        listSoftView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                NoticeDevice noticeDevice = noticeInfoList.get(position);
//                Intent intent = new Intent(ListAppActivity.this, PushAppListActivity.class);
//                intent.putExtra("noticeDevice", noticeDevice);
//                ListAppActivity.this.setResult(1, intent);
                noticeInfoManager.addoftRemind(noticeDevice);
                ListAppActivity.this.finish();
            }
        });
    }

    private void initData() {
    //    queryAppInfo();
        new LoadApp().execute();
    }

    LoadDataDialog loadDataDialog;

    class  LoadApp extends AsyncTask{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loadDataDialog = new LoadDataDialog(ListAppActivity.this,"loadDialog");
            loadDataDialog.setCanceledOnTouchOutside(false);
            loadDataDialog.show();
        }

        @Override
        protected Object doInBackground(Object[] params) {
            queryAppInfo();
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            loadDataDialog.cancel();
            appListAdapter.setIconList(noticeInfoList);
        }
    }

    public void queryAppInfo() {
        PackageManager pm = this.getPackageManager(); // 获得PackageManager对象
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        // 通过查询，获得所有ResolveInfo对象.
        List<ResolveInfo> resolveInfos = pm
                .queryIntentActivities(mainIntent, PackageManager.MATCH_ALL);
        // 调用系统排序 ， 根据name排序
        // 该排序很重要，否则只能显示系统应用，而不能列出第三方应用程序
        Collections.sort(resolveInfos, new ResolveInfo.DisplayNameComparator(pm));
        if (noticeInfoList != null) {
            noticeInfoList.clear();
            for (ResolveInfo reInfo : resolveInfos) {
                String activityName = reInfo.activityInfo.name; // 获得该应用程序的启动Activity的name
                String pkgName = reInfo.activityInfo.packageName; // 获得应用程序的包名
                String appLabel = (String) reInfo.loadLabel(pm); // 获得应用程序的Label
                Drawable icon = reInfo.loadIcon(pm); // 获得应用程序图标
             //   System.out.println("appLabel: "+appLabel+" activityName:"+reInfo.activityInfo.name+" pkgName: "+reInfo.activityInfo.packageName);
                // 为应用程序的启动Activity 准备Intent
//                Intent launchIntent = new Intent();
//                launchIntent.setComponent(new ComponentName(pkgName,
//                        activityName));
                // 创建一个AppInfo对象，并赋值
                NoticeDevice appInfo = new NoticeDevice();
                appInfo.appAvator = Conversion.drawableToByte(icon);
                appInfo.appName = appLabel;
                appInfo.packageInfo = pkgName;
//
//                appInfo.setAppLabel(appLabel);
//                appInfo.setPkgName(pkgName);
//                appInfo.setAppIcon(icon);
//                appInfo.setIntent(launchIntent);
                noticeInfoList.add(appInfo); // 添加至列表中
//                System.out.println(appLabel + " activityName---" + activityName
//                        + " pkgName---" + pkgName);
            }
        }
    }

    Bitmap drawableToBitmap(Drawable drawable) {

        Bitmap bitmap = Bitmap
                .createBitmap(
                        drawable.getIntrinsicWidth(),
                        drawable.getIntrinsicHeight(),
                        drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                                : Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        //canvas.setBitmap(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return bitmap;
    }
}