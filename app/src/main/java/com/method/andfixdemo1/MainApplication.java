/*
 * 
 * Copyright (c) 2015, alipay.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.method.andfixdemo1;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.alipay.euler.andfix.patch.PatchManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * sample application
 *
 * @author sanping.li@alipay.com
 */
public class MainApplication extends Application {
    private static final String TAG = "euler";

    private static final String APATCH_PATH = "/out.apatch";
    /**
     * patch manager
     */
    private PatchManager mPatchManager;

    @Override
    public void onCreate() {
        super.onCreate();
        // initialize
        initAndFix();
    }

    private void initAndFix() {
        getVersion();
    }


    private void initPatchManager(String version, String path) {
        final String SP_NAME = "_andfix_"; //PatchManager中的属性
        final String SP_VERSION = "version"; //PatchManager中的属性
        SharedPreferences sp = this.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        String ver = sp.getString(SP_VERSION, null);
        boolean downLoad = ver == null || !ver.equalsIgnoreCase(version);
        mPatchManager = new PatchManager(this);
        mPatchManager.init(version);
        Log.d(TAG, "inited.version:" + version);
        if (downLoad) {
            getFile(path);
        } else {
            mPatchManager.loadPatch();
        }
    }

    private void getVersion() {
        new NetUtils(Constant.VERSION_URL, 0, new NetUtils.CallBack() {
            @Override
            public void invoke(String result) {
                try {
                    Version version = new Version();
                    JSONObject json = new JSONObject(result);
                    version.code = json.getInt("code");
                    version.version = json.getString("version");
                    version.fixUrl = json.getString("fixUrl");
                    Log.i(TAG, "result:" + result);
                    initPatchManager(version.version, version.fixUrl);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void getFile(String filePath) {
        new NetUtils(filePath, 1, new NetUtils.CallBack() {
            @Override
            public void invoke(String path) {
                try {
                    mPatchManager.addPatch(path);
                    mPatchManager.loadPatch();
                    Log.d(TAG, "apatch loaded.");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
