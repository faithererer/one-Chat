package com.zjc.onechat.api;

import com.hjq.http.config.IRequestApi;



public final class QueryAvatarApi implements IRequestApi {

    @Override
    public String getApi() {
        return "api/user/avatar";
    }

    private String phone;

    public QueryAvatarApi setPhone(String phone) {
        this.phone = phone;
        return this;
    }




}
