package com.zjc.onechat.api;

import com.hjq.http.config.IRequestApi;

public class FriendListApi implements IRequestApi {
    @Override
    public String getApi() {
        return "api/friends/friendList";
    }

    private String id;


    public FriendListApi setId(String userId) {
        this.id = userId;
        return this;
    }


    public FriendListApi setFriendId(String friendId) {
        this.id = id;
        return this;
    }

    public class Bean{
        @Override
        public String toString() {
            return "Bean{" +
                    "nickName='" + nickName + '\'' +
                    ", avatar='" + avatar + '\'' +
                    ", id=" + id +
                    '}';
        }

        private String nickName;
        private String avatar;
        private int id;

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        private String phone;
        public String getNickName() {
            return nickName;
        }

        public void setNickName(String nickName) {
            this.nickName = nickName;
        }

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }
    }
}