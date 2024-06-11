package com.zjc.onechat.entity;
public  class UserDTO{
    public String getToken() {
        return token;
    }

    public Long getId() {
        return id;
    }

    public String getPhone() {
        return phone;
    }

    public String getNickname() {
        return nickname;
    }

    public String getAvatar() {
        return avatar;
    }

    String token;
    private Long id;
    private String phone;
    private String nickname;
    private String avatar;
    public UserDTO setToken(String token) {
        this.token = token;
        return this;
    }

    public UserDTO setId(Long id) {
        this.id = id;
        return this;
    }

    public UserDTO setPhone(String phone) {
        this.phone = phone;
        return this;
    }

    public UserDTO setNickname(String nickname) {
        this.nickname = nickname;
        return this;
    }

    public UserDTO setAvatar(String avatar) {
        this.avatar = avatar;
        return this;
    }


}