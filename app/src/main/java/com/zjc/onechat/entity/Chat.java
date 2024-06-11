package com.zjc.onechat.entity;

public class Chat {
    private String name;
    private String message;
    private String time;
    private int unreadCount;
    // private String profileImageUrl; // If you have a URL for profile image

    public Chat(String name, String message, String time, int unreadCount) {
        this.name = name;
        this.message = message;
        this.time = time;
        this.unreadCount = unreadCount;
        // this.profileImageUrl = profileImageUrl;
    }

    public String getName() {
        return name;
    }

    public String getMessage() {
        return message;
    }

    public String getTime() {
        return time;
    }

    public int getUnreadCount() {
        return unreadCount;
    }

    // public String getProfileImageUrl() {
    //     return profileImageUrl;
    // }
}

