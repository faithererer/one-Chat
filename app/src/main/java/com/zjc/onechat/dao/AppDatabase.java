package com.zjc.onechat.dao;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.zjc.onechat.dao.entity.Chat;
import com.zjc.onechat.dao.entity.Friend;
import com.zjc.onechat.dao.entity.FriendRequest;
import com.zjc.onechat.dao.entity.Message;
import com.zjc.onechat.dao.entity.User;

@Database(entities = {User.class, Chat.class, Friend.class, FriendRequest.class, Message.class}, version = 1
,exportSchema=false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract UserDao userDao();
    public abstract ChatDao chatDao();
    public abstract FriendDao friendDao();
    public abstract FriendRequestDao friendRequestDao();
    public abstract MessageDao messageDao();

 }
