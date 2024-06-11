package com.zjc.onechat.api;

import com.hjq.http.config.IRequestApi;

public class SearchFriendApi implements IRequestApi {
    @Override
    public String getApi() {
        return "api/friends/search";
    }

    private String userId;
    private String friendId;

    public String getUserId() {
        return userId;
    }

    public SearchFriendApi setUserId(String userId) {
        this.userId = userId;
        return this;
    }

    public String getFriendId() {
        return friendId;
    }

    public SearchFriendApi setFriendId(String friendId) {
        this.friendId = friendId;
        return this;
    }
}
