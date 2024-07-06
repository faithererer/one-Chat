package com.zjc.onechat.entity;

public class FriendRequest {

    private Long id;
    private Long userId;
    private Long friendId;
    private String avatar;
    private String nickName;
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        id = id;
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

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }


}
