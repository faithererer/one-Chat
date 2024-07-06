package com.zjc.onechat.dao.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.stfalcon.chatkit.commons.models.IDialog;

@Entity(tableName = "chat")
public class Chat {
    public long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUser_id() {
        return user_id;
    }

    public void setUser_id(Long user_id) {
        this.user_id = user_id;
    }

    public long getFriend_id() {
        return friend_id;
    }

    public void setFriend_id(long friend_id) {
        this.friend_id = friend_id;
    }

    public Long getLast_message_time() {
        return last_message_time;
    }

    public void setLast_message_time(Long last_message_time) {
        this.last_message_time = last_message_time;
    }

    @PrimaryKey(autoGenerate = true)
    public Long id;
    public Long user_id;
    public Long friend_id;
    public Long last_message_time;

    @Override
    public String toString() {
        return "Chat{" +
                "id=" + id +
                ", user_id=" + user_id +
                ", friend_id=" + friend_id +
                ", last_message_time=" + last_message_time +
                '}';
    }
}
