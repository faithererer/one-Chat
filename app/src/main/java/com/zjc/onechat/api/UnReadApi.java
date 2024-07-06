package com.zjc.onechat.api;

import androidx.annotation.NonNull;
import androidx.room.Ignore;

import com.hjq.http.config.IRequestApi;
import com.zjc.onechat.dao.entity.Message;

public class UnReadApi implements IRequestApi {
    @NonNull
    public String getApi() {
        return "api/chat/unread_count";
    }

    Integer userId;
    Integer friendId;
    public Integer getUserId() {
        return userId;
    }

    public Integer getFriendId() {
        return friendId;
    }

    public UnReadApi setFriendId(Integer friendId) {
        this.friendId = friendId;
        return this;
    }

    public UnReadApi setUserId(Integer userId) {
        this.userId = userId;
        return this;
    }


    public static class Bean{
        Integer count;
        Message message;

        public Integer getCount() {
            return count;
        }

        public Message getMessage() {
            return message;
        }

        public void setMessage(Message message) {
            this.message = message;
        }

        public void setCount(Integer count) {
            this.count = count;
        }
    }
}
