package com.zjc.onechat.entity;

public class FriendRequest {
    private Long userId;
    private Long friendId;
    private String avatar;
    private String nickName;

    public FriendRequest(Long userId, Long friendId, String avatar, String nickName) {
        this.userId = userId;
        this.friendId = friendId;
        this.avatar = avatar;
        this.nickName = nickName;
    }

    public Long getUserId() {
        return userId;
    }

    public Long getFriendId() {
        return friendId;
    }

    public String getAvatar() {
        return avatar;
    }

    public String getNickName() {
        return nickName;
    }
}
