package com.method.andfixdemo1;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.alipay.euler.andfix.patch.PatchManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, Constant {
    private static final String TAG = "euler";

    private PatchManager mPatchManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mPatchManager = new PatchManager(this);
        Button btn_showMsg = (Button) findViewById(R.id.btn_showMsg);
        Button btn_install = (Button) findViewById(R.id.btn_install);
        Button btn_uninstall = (Button) findViewById(R.id.btn_uninstall);
        btn_showMsg.setOnClickListener(this);
        btn_install.setOnClickListener(this);
        btn_uninstall.setOnClickListener(this);
    }

    private final MyHandler mHandler = new MyHandler(this);

    private static class MyHandler extends Handler {
        private final WeakReference<MainActivity> mActivity;

        public MyHandler(MainActivity activity) {
            mActivity = new WeakReference<MainActivity>(activity);
        }

        @Override
        public void dispatchMessage(Message msg) {
            MainActivity mainActivityctivity = mActivity.get();
            switch (msg.what) {
                case 100:
                    try {
                        String result = msg.getData().getString("result");
                        Version version = new Version();
                        JSONObject json = new JSONObject(result);
                        version.code = json.getInt("code");
                        version.version = json.getString("version");
                        version.fixUrl = json.getString("fixUrl");
                        Toast.makeText(mainActivityctivity, result, Toast.LENGTH_LONG).show();
                        mainActivityctivity.getFile(version.fixUrl);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case 200:
                    String path = msg.getData().getString("path");
                    Log.i(TAG, path);
                    Toast.makeText(mainActivityctivity, "install success!~", Toast.LENGTH_LONG).show();
                    File file = new File(path);
                    if (null != file && file.exists()) {
                        Log.i(TAG, "name:" + file.getName());
                        Log.i(TAG, "path:" + file.getAbsolutePath());
                        Log.i(TAG, "length:" + file.length());
                        Log.i(TAG, file.getName());
                    } else {
                        Log.i(TAG, "file is not exits!");
                    }
                    mainActivityctivity.addFix(path);
                    break;
            }
            super.dispatchMessage(msg);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_showMsg:
                Toast.makeText(this, getMessage(), Toast.LENGTH_LONG).show();
                break;
            case R.id.btn_install:
                try {
                    getVersion();
                } catch (Exception e) {
                    Log.e(TAG, "none fix file", e);
                }
                break;
            case R.id.btn_uninstall:
                mPatchManager.removeAllPatch();
                break;
        }
    }

    /**
     * 要修复的方法
     *
     * @return
     */
    private String getMessage() {
        //return "bug is over, fix succeed!";
        return "this is bug show ";
        //return "bug is over, second fix! ";
    }


    private void addFix(String path) {
        try {
            Log.i(TAG, "prepare addFix success!");
            mPatchManager.addPatch(path);
            Log.i(TAG, "addFix success!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void getVersion() {
        new NetUtils(VERSION_URL, 0, new NetUtils.CallBack() {
            @Override
            public void invoke(String result) {
                Message msg = new Message();
                msg.what = 100;
                Bundle bundle = new Bundle();
                bundle.putString("result", result);
                msg.setData(bundle);
                mHandler.sendMessage(msg);
            }
        }).start();
    }

    private void getFile(String filePath) {
        new NetUtils(filePath, 1, new NetUtils.CallBack() {
            @Override
            public void invoke(String path) {
                Message msg = new Message();
                msg.what = 200;
                Bundle bundle = new Bundle();
                bundle.putString("path", path);
                msg.setData(bundle);
                mHandler.sendMessage(msg);
            }
        }).start();
    }
}