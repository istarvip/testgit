package com.walnutin.util;

import java.io.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Created by Jiangang on 2015/5/21.
 */
public class GzipUtils {
    public static Object unCompress(byte[] restoreCheckResult) {
        Object object_ = null;
        try {
            //建立字节数组输入流 
            ByteArrayInputStream i = new ByteArrayInputStream(restoreCheckResult);
            //建立gzip解压输入流 
            GZIPInputStream gzin = new GZIPInputStream(i);
            //建立对象序列化输入流 
            ObjectInputStream in = new ObjectInputStream(gzin);
            //按制定类型还原对象 
            object_ = (Object) in.readObject();
       /*     i.close();
            gzin.close();
            in.close();*/
            in.close();
            gzin.close();
            i.close();
        } catch (ClassNotFoundException e) {
            System.out.println(e);
        } catch (IOException e) {
            System.out.println(e);
        }
        return (object_);
    }

    public static byte[] doCompress(Object checkResultData) {
        byte[] data_ = null;
        try {
            ByteArrayOutputStream o = new ByteArrayOutputStream();
            //建立gzip压缩输出流
            GZIPOutputStream gzout = new GZIPOutputStream(o);
            //建立对象序列化输出流
            ObjectOutputStream out = new ObjectOutputStream(gzout);
            out.writeObject(checkResultData);
            out.flush();
            out.close();
            gzout.close();
            //返回压缩字节流
            data_ = o.toByteArray();
            o.close();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }
        return data_;
    }
}
