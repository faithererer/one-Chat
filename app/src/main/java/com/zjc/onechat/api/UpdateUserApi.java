package com.zjc.onechat.api;

import androidx.annotation.NonNull;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;
import com.hjq.http.config.IRequestApi;
import com.zjc.onechat.dao.entity.User;

public class UpdateUserApi implements IRequestApi{
    @NonNull
    public String getApi() {
        return "api/user/update";
    }
    @PrimaryKey
    public long id;
    public String nickName;
    public String avatar;

    public long getId() {
        return id;
    }

    public UpdateUserApi setId(long id) {
        this.id = id;
        return this;
    }

    public UpdateUserApi setAvatar(String avatar) {
        this.avatar = avatar;
        return this;
    }

    public String getAvatar() {
        return avatar;
    }

    public String getNick_name() {
        return nickName;
    }

    public UpdateUserApi setNick_name(String nick_name) {
        this.nickName = nick_name;
        return this;
    }
}
