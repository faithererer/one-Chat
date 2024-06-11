package com.zjc.onechat.api;


import com.hjq.http.config.IRequestApi;

public final class LoginApi implements IRequestApi {

    @Override
    public String getApi() {
        return "api/auth/login";
    }

    private String phone;
    private String code;

    public LoginApi setPhone(String phone) {
        this.phone = phone;
        return this;
    }

    public LoginApi setCode(String code) {
        this.code = code;
        return this;
    }


}
