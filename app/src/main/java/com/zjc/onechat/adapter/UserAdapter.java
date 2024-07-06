package com.zjc.onechat.adapter;

import android.util.Log;

import androidx.annotation.NonNull;

import com.stfalcon.chatkit.commons.models.IUser;
import com.zjc.onechat.dao.entity.User;

public class UserAdapter implements IUser {
    private User user;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public UserAdapter(User user) {
        this.user = user;
    }

    @Override
    public String getId() {
        return String.valueOf(user.getId());
    }

    @Override
    public String getName() {
        return user.getNick_name();
    }

    @Override
    public String getAvatar() {
        Log.d("头像"+user.getNick_name(),"a");
        return user.getAvatar();
    }

    @NonNull
    @Override
    public String toString() {
        return "UserAdapter{" +
                "user=" + user +
                '}';
    }
}
