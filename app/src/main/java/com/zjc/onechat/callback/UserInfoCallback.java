package com.zjc.onechat.callback;

import com.zjc.onechat.api.UserInfo;
import com.zjc.onechat.dao.entity.User;
import com.zjc.onechat.entity.Result;

public interface UserInfoCallback {
    void onSuccess(Result<UserInfo.Bean> result);
    void onFailure(String errorMessage);
}

