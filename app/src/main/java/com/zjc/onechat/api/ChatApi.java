package com.zjc.onechat.api;

import com.hjq.http.config.IRequestApi;

import java.time.LocalDateTime;

public class ChatApi implements IRequestApi {
    @Override
    public String getApi() {
        return "api/chat/chatsByMe";
    }

    private Long userId;
    private Long friendId;

    public ChatApi setUserId(Long userId) {
        this.userId = userId;
        return this;
    }

    public ChatApi setFriendId(Long friendId) {
        this.friendId = friendId;
        return this;
    }

    public static class Bean {
        @Override
        public String toString() {
            return "Bean{" +
                    "id=" + id +
                    ", userId=" + userId +
                    ", friendId=" + friendId +
                    ", lastMessageTime=" + lastMessageTime +
                    '}';
        }

        private Long id;
        private Long userId;
        private Long friendId;
        private LocalDateTime lastMessageTime;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public Long getUserId() {
            return userId;
        }

        public void setUserId(Long userId) {
            this.userId = userId;
        }

        public Long getFriendId() {
            return friendId;
        }

        public void setFriendId(Long friendId) {
            this.friendId = friendId;
        }

        public LocalDateTime getLastMessageTime() {
            return lastMessageTime;
        }

        public void setLastMessageTime(LocalDateTime lastMessageTime) {
            this.lastMessageTime = lastMessageTime;
        }
    }
}
