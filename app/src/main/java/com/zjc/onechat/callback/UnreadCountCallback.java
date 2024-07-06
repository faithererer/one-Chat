package com.zjc.onechat.callback;

import com.zjc.onechat.api.UnReadApi;

public interface UnreadCountCallback {
    void onUnreadCountFetched(UnReadApi.Bean unreadCount);

    void onError(Throwable throwable);
}