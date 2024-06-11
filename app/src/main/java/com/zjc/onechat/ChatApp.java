package com.zjc.onechat;

import android.app.Application;
import android.content.SharedPreferences;

import com.hjq.http.EasyConfig;
import com.hjq.http.request.HttpRequest;
import com.hjq.toast.Toaster;
import com.zjc.onechat.config.RequestServer;

import jp.wasabeef.blurry.BuildConfig;
import okhttp3.OkHttpClient;

public class ChatApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // 初始化 Toast 框架
        Toaster.init(this);
        SharedPreferences sharedPreferences = getSharedPreferences("authorization",MODE_PRIVATE);

        OkHttpClient okHttpClient = new OkHttpClient.Builder().build();



        EasyConfig.with(okHttpClient)
                // 是否打印日志
                .setLogEnabled(BuildConfig.DEBUG)
                .setServer(new RequestServer())
                .setHandler(new RequestHandler(this))
                .addHeader("authorization", sharedPreferences.getString("authorization",""))
                .into();
    }
}
