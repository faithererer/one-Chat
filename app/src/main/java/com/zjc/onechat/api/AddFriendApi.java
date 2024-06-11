package com.zjc.onechat.api;

import com.hjq.http.config.IRequestApi;

public class AddFriendApi implements IRequestApi {
    @Override
    public String getApi() {
        return "api/friends/add";
    }

    private String userId;
    private String friendId;

    public String getUserId() {
        return userId;
    }

    public AddFriendApi setUserId(String userId) {
        this.userId = userId;
        return this;
    }

    public String getFriendId() {
        return friendId;
    }

    public AddFriendApi setFriendId(String friendId) {
        this.friendId = friendId;
        return this;
    }
}
