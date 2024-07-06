package com.zjc.onechat.dao.entity;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

@Entity(tableName = "user")
public class User {

    public User(){

    }
    @Ignore
    public User(Long id, String phone, String nick_name, String avatar) {
        this.id = id;
        this.phone = phone;
        this.nick_name = nick_name;
        this.avatar = avatar;
     }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getNick_name() {
        return nick_name;
    }

    public void setNick_name(String nick_name) {
        this.nick_name = nick_name;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", phone='" + phone + '\'' +
                ", nick_name='" + nick_name + '\'' +
                ", avatar='" + avatar + '\'' +
                '}';
    }

    @PrimaryKey
    public long id;
    public String phone;
    @SerializedName("nickName")
    public String nick_name;
    public String avatar;
}
