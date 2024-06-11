package com.zjc.onechat.entity;

public class Friend {
    private String nickName;
    private String avatar; // 使用URL而不是资源ID
    private Integer id;

    public Friend(String nickName, String avatar, int id) {
        this.nickName = nickName;
        this.avatar = avatar;
        this.id = id;
    }

    public String getNickName() {
        return nickName;
    }

    public String getAvatar() {
        return avatar;
    }

    public Integer getId() {
        return id;
    }

    public void setName(String name) {
        this.nickName = nickName;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatar = avatar;
    }

    public void setId(int id) {
        this.id = id;
    }
}