package com.walnutin.hardsdkdemo.utils;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.DownloadManager;
import android.app.DownloadManager.Request;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.webkit.MimeTypeMap;

import com.walnutin.hardsdkdemo.ProductList.HardSdk;
import com.walnutin.hardsdkdemo.ProductNeed.entity.AutoUpdate;

import java.io.File;

public class UpdateModule {
    private final String TAG = UpdateModule.class.getSimpleName();
    private DownloadManager downloadManager;
    private String apkName;
    private String apkPath;
    private String downloadId;
    //private final String updateSPName = "update_shared_preferences_name";
    private Activity mActivity;
    private boolean isShowToast = false;

    private static UpdateModule mUpdateAction;
    private AutoUpdate autoUpdate;
    private PreferenceSettings preferenceSettings;

    public static UpdateModule getInstance() {
        if (mUpdateAction == null) {
            mUpdateAction = new UpdateModule();
        }

        return mUpdateAction;
    }

    boolean haveNewVersion = false;

    public boolean checkNewVersion(final Activity activity, final boolean ignore) {
        preferenceSettings = PreferenceSettings.getInstance(activity);
        mActivity = activity;
        //isShowToast = isShow;
        downloadManager = (DownloadManager) mActivity
                .getSystemService(Activity.DOWNLOAD_SERVICE);

        new AsyncTask<String, Integer, String>() {

            protected void onPreExecute() {

            }

            protected String doInBackground(String... params) {
                autoUpdate = new AutoUpdate(mActivity);
                return null;
            }

            protected void onPostExecute(String result) {
                apkName = "hard_v" + autoUpdate.mServerVersion + ".apk";
                apkPath = Environment.getExternalStorageDirectory().getPath()
                        + "/Download/" + apkName;
                downloadId = apkName;
                long ignoreVersion = -1;
                if (ignore == true) {
                    ignoreVersion = preferenceSettings.getLong("ignoreVersion", 0);

                }
                if (autoUpdate.checkUpdateInfo() && autoUpdate.mServerVersion != ignoreVersion) {
                    haveNewVersion = true;
                    // 注册广播
                    if (downloadBroadcastReceiver != null) {
                        HardSdk.getInstance().getContext().registerReceiver(downloadBroadcastReceiver,
                                new IntentFilter(
                                        DownloadManager.ACTION_DOWNLOAD_COMPLETE));
                    }
                    if (preferenceSettings.getLong(downloadId, -1) == -1) {
                        //     preferenceSettings.setBoolean(Config.UPDATE_SOFTWARE_FLAG, false);
                        Builder builder = new Builder(mActivity);
                        builder.setCancelable(true);
                        builder.setTitle("有新版本");
                        builder.setMessage("当前版本"
                                + OtherUtil.getAppVersionName(mActivity)
                                + "最新版本" + autoUpdate.getVersionName() + "\n " +
                                autoUpdate.getmDecription());
                        builder.setPositiveButton("现在更新",
                                new DialogInterface.OnClickListener() {

                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        String url = autoUpdate
                                                .getServerApkUrl();
                                        Uri resource = Uri.parse(url);
                                        Request request = new Request(
                                                resource);
                                        request.setAllowedNetworkTypes(Request.NETWORK_MOBILE
                                                | Request.NETWORK_WIFI);
                                        request.setAllowedOverRoaming(false);
                                        MimeTypeMap mimeTypeMap = MimeTypeMap
                                                .getSingleton();
                                        String mimeString = mimeTypeMap
                                                .getMimeTypeFromExtension(MimeTypeMap
                                                        .getFileExtensionFromUrl(url));
                                        request.setMimeType(mimeString);
                                        request.setNotificationVisibility(Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                                        request.setVisibleInDownloadsUi(true);

                                        // sdcard的目录下的download文件夹
                                        request.setDestinationInExternalPublicDir(
                                                "/Download/", apkName);
                                        request.setTitle("更新版本");

                                        try {
                                            long id = downloadManager
                                                    .enqueue(request);
                                            preferenceSettings.setLong(downloadId, id);
                                        } catch (Exception e) {
                                            //	ToastUtil.showToast("请开启下载管理器");
                                        }
                                    }
                                });
                        builder.setNegativeButton("忽略版本",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        if (downloadBroadcastReceiver != null) {
                                            try {
                                                preferenceSettings.setLong("ignoreVersion", autoUpdate.mServerVersion);
                                                HardSdk.getInstance().getContext()
                                                        .unregisterReceiver(downloadBroadcastReceiver);
                                            } catch (IllegalArgumentException e) {
                                                //XLog.e(TAG, e.toString());
                                            }
                                        }
                                    }
                                });
                        builder.create().show();

                    } else {
                        queryDownloadStatus();// 下载已经开始，检查状态
                    }

                } else {

                    haveNewVersion = false;
                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        return haveNewVersion;
    }

    private void queryDownloadStatus() {
        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterById(preferenceSettings.getLong(downloadId, 0));
        Cursor cursor = downloadManager.query(query);
        if (cursor.moveToFirst()) {
            int status = cursor.getInt(cursor
                    .getColumnIndex(DownloadManager.COLUMN_STATUS));
            switch (status) {
                case DownloadManager.STATUS_PAUSED:
                    //	XLog.i(TAG, "暂停下载");
                case DownloadManager.STATUS_PENDING:
                    //	XLog.i(TAG, "等待下载");
                case DownloadManager.STATUS_RUNNING:
                    //	XLog.i(TAG, "正在下载");
                    break;
                case DownloadManager.STATUS_SUCCESSFUL:
                    //	XLog.i(TAG, "下载完成");
                    if (downloadBroadcastReceiver != null) {
                        HardSdk.getInstance().getContext().unregisterReceiver(downloadBroadcastReceiver);
                    }
                    installApk();
                    break;
                case DownloadManager.STATUS_FAILED:
                    //	XLog.e(TAG, "下载失败");
                    downloadManager.remove(preferenceSettings.getLong(downloadId, 0));
                    //	SPUtil.clearPreference(updateSPName);
                    if (downloadBroadcastReceiver != null) {
                        HardSdk.getInstance().getContext().unregisterReceiver(downloadBroadcastReceiver);
                    }
                    break;
                default:
                    if (downloadBroadcastReceiver != null) {
                        HardSdk.getInstance().getContext().unregisterReceiver(downloadBroadcastReceiver);
                    }
                    break;
            }
        }
        cursor.close();
    }

    private BroadcastReceiver downloadBroadcastReceiver = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {
            long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0);
            //	XLog.i(TAG, "接收到下载完成的ID是：" + id);
            queryDownloadStatus();
        }
    };

    private void installApk() {
        File apkfile = new File(apkPath);
        if (!apkfile.exists()) {
            downloadManager.remove(preferenceSettings.getLong(downloadId, 0));
            preferenceSettings.setLong(downloadId, -1);
            //SPUtil.clearPreference(updateSPName);
            return;
        }
        preferenceSettings.setLong(downloadId, -1);
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.setDataAndType(Uri.parse("file://" + apkfile.toString()),
                "application/vnd.android.package-archive");
        mActivity.startActivity(i);
    }
}
