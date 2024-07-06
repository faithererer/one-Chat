package com.zjc.onechat.api;

import com.hjq.http.config.IRequestApi;
import com.zjc.onechat.entity.FriendRequest;

import java.util.List;

public class FriendRequestApi implements IRequestApi {
    @Override
    public String getApi() {
        return "api/friends/friendRequestList";
    }

    String userId;

    public String getUserId() {
        return userId;
    }

    public FriendRequestApi setUserId(String userId) {
        this.userId = userId;
        return this;
    }
    public static class Bean{
        List<FriendRequest> requestList;

        public List<FriendRequest> getRequestList() {
            return requestList;
        }

        public void setRequestList(List<FriendRequest> requestList) {
            this.requestList = requestList;
        }
    }
}
