package com.zjc.onechat.api;



import androidx.annotation.Nullable;

import com.hjq.http.config.IRequestApi;

public final class SendCodeApi implements IRequestApi {

    @Override
    @Nullable
    public String getApi() {
        return "api/auth/sendCode";
    }
    private String phone;

    public SendCodeApi setPhone(String phone) {
        this.phone = phone;
        return this;
    }





}
