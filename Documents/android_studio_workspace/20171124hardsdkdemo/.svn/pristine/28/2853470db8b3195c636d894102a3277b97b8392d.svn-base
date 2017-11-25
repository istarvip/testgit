package com.walnutin.hardsdkdemo.ProductNeed.entity;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class AutoUpdate {
    private final String TAG = AutoUpdate.class.getSimpleName();
    private int mLocalVersion = -1;
    public int mServerVersion = -1;
    private String mServerApkUrl;
    private String mVersionName;
    private String mDecription;
    private static final String mUrl = "http://2502.walnutin.com:8080/User/ver/hard_version.xml";

    public AutoUpdate(Context context) {
        try {
            mLocalVersion = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0).versionCode;
            getServerInfos();
        } catch (NameNotFoundException e) {
            //		XLog.e(TAG, e.toString());
        }
    }

    public boolean checkUpdateInfo() {
        if ((mServerVersion > 0) && (mLocalVersion < mServerVersion)) {
            return true;
        }
        return false;
    }

    public String getmDecription() {
        return mDecription;
    }

    public void setmDecription(String mDecription) {
        this.mDecription = mDecription;
    }

    public String getServerApkUrl() {
        return mServerApkUrl;
    }

    public String getVersionName() {
        return mVersionName;
    }

    public HttpURLConnection getHttpURLConnection(URL url) {
        try {
            HttpURLConnection connect = (HttpURLConnection) url
                    .openConnection();
            connect.setConnectTimeout(10000);
            connect.setReadTimeout(2000);
            connect.connect();
            return connect;

        } catch (MalformedURLException e) {
            //		XLog.e(TAG, e.toString());
        } catch (IOException e) {
            //		XLog.e(TAG, e.toString());
        }
        return null;
    }

    private void getServerInfos() {
        try {
            HttpURLConnection connect = getHttpURLConnection(new URL(mUrl));
            InputStream stream = connect.getInputStream();

            DocumentBuilderFactory factory = DocumentBuilderFactory
                    .newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(stream);
            mServerVersion = Integer.parseInt(getValByTagName(document,"versionCode"));
            mServerApkUrl = getValByTagName(document, "versionApk");
            mVersionName = getValByTagName(document,"versionName");
            mDecription = getValByTagName(document,"versionDescription");
//            Element root = document.getDocumentElement();
//            NodeList nodeList = root.getChildNodes();
//            mServerVersion = Integer.parseInt(root.getAttribute("versionCode"));
//            mServerApkUrl = root.getAttribute("versionApk");
//            mVersionName = root.getAttribute("versionName");
//            mDecription = root.getAttribute("versionDescription");
            stream.close();
        } catch (Exception e) {
//			XLog.e(TAG, e.toString());
        }
    }
    public static String getValByTagName(Document doc, String tagName) {
        NodeList list = doc.getElementsByTagName(tagName);
        if (list.getLength() > 0) {
            Node node = list.item(0);
            Node valNode = node.getFirstChild();
            if (valNode != null) {
                String val = valNode.getNodeValue();
                return val;
            }
        }
        return null;
    }
}
