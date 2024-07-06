package com.zjc.onechat.api;

import com.hjq.http.config.IRequestApi;

import kotlin.UInt;

public class UserInfo implements IRequestApi {
    @Override
    public String getApi() {
        return "api/user/"+getUserId();
    }

    private Long userId;
    private String phone;
    private String nickName;
    private String avatar;

    public UserInfo setUserId(Long userId) {
        this.userId = userId;
        return this;
    }

    public Long getUserId() {
        return userId;
    }

    public static class Bean {
        @Override
        public String toString() {
            return "Bean{" +
                    "id=" + id +
                    ", nickName='" + nickName + '\'' +
                    ", avatar='" + avatar + '\'' +
                    '}';
        }

        private Long id;
//        private String phone;
        private String nickName;
        private String avatar;
        private String phone;

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

//        public String getPhone() {
//            return phone;
//        }
//
//        public void setPhone(String phone) {
//            this.phone = phone;
//        }

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
    }
}
