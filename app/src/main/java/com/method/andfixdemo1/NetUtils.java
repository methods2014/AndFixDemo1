package com.method.andfixdemo1;

import android.os.Environment;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

/**
 * Created by chen on 2016/3/12.
 */
public class NetUtils extends Thread{
    private static String TAG = "euler";
    private String urlStr;
    private CallBack mCallBack;
    private int type=0;//请求方式：Get 0, File 1
    public NetUtils(String urlStr, int type, CallBack callBack){
        this.urlStr = urlStr;
        this.mCallBack=callBack;
        this.type = type;
    }

    private void get(){
        try {
            URL url = new URL(urlStr);
            Log.i(TAG, urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            //conn.setConnectTimeout(3*1000);
            conn.setRequestMethod("GET");
            conn.setUseCaches(false);
            if (conn.getResponseCode() != 200) return;
            InputStream in = new BufferedInputStream(conn.getInputStream());
            Scanner scanner = new Scanner(in).useDelimiter("\\A");
            String result = scanner.hasNext() ? scanner.next() : "";
            mCallBack.invoke(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    private void getFile() {
        String path = "andfix";
        String fileName="fix.apatch";
        String SDCard = Environment.getExternalStorageDirectory() + "";
        String pathName = SDCard + "/" + path + "/" + fileName;//文件存储路径
        File file = new File(pathName);
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        try {
                /*
				 * 通过URL取得HttpURLConnection
				 * 要网络连接成功，需在AndroidMainfest.xml中进行权限配置
				 * <uses-permission android:name="android.permission.INTERNET" />
				 */
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setUseCaches(true);
            //取得inputStream，并将流中的信息写入SDCard

				/*
				 * 写前准备
				 * 1.在AndroidMainfest.xml中进行权限配置
				 * <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
				 * 取得写入SDCard的权限
				 * 2.取得SDCard的路径： Environment.getExternalStorageDirectory()
				 * 3.检查要保存的文件上是否已经存在
				 * 4.不存在，新建文件夹，新建文件
				 * 5.将input流中的信息写入SDCard
				 * 6.关闭流
				 */
            if (file.exists()) {
                Log.i(TAG, "exits");
                file.delete();
            }
            String dir = SDCard + "/" + path;
            new File(dir).mkdir();//新建文件夹
            file.createNewFile();//新建文件

            bis = new BufferedInputStream(conn.getInputStream());
            bos = new BufferedOutputStream(new FileOutputStream(file));

            int len = 2048;
            byte[] b = new byte[len];
            while ((len = bis.read(b)) != -1)
            {
                bos.write(b, 0, len);
            }
            bos.flush();
        } catch (Exception e) {
            Log.i(TAG,"exception 1");
            e.printStackTrace();
        } finally {
            try {
                if(null != bis) {
                    bis.close();
                }
                if(null != bos) {
                    bos.close();
                }
                Log.i(TAG,"success 1");
            } catch (IOException e) {
                Log.i(TAG,"fail 1");
                e.printStackTrace();
            }
        }
        mCallBack.invoke(file.getAbsolutePath());
    }

    @Override
    public void run() {
        switch (type){
            case 0:
                get();
                break;
            case 1:
                getFile();
                break;
        }
    }

    public interface CallBack{
        void invoke(String result);
    }
}
