package com.zjc.onechat;

import android.app.Application;
import android.content.SharedPreferences;

import androidx.room.Room;

import com.hjq.http.EasyConfig;
import com.hjq.toast.Toaster;
import com.zjc.onechat.config.RequestServer;
import com.zjc.onechat.dao.AppDatabase;
import com.zjc.onechat.dao.ChatDao;
import com.zjc.onechat.dao.FriendDao;
import com.zjc.onechat.dao.MessageDao;
import com.zjc.onechat.dao.UserDao;
import com.zjc.onechat.utils.RequestHandler;

import jp.wasabeef.blurry.BuildConfig;
import okhttp3.OkHttpClient;

public class ChatApp extends Application {
    private AppDatabase database;

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
            database = Room.databaseBuilder(this, AppDatabase.class, "oneChat").build();
    }


    public MessageDao getMessageDao() {
        return database.messageDao();
    }
    public UserDao getUserDao() {
        return database.userDao();
    }
    public ChatDao getChatDao() {
        return database.chatDao();
    }
    public FriendDao getFriendDao(){return database.friendDao();}

}
