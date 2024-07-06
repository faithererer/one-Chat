package com.zjc.onechat.config;

import androidx.annotation.NonNull;

import com.hjq.http.config.IRequestBodyStrategy;
import com.hjq.http.config.IRequestServer;
import com.hjq.http.model.RequestBodyType;

public class RequestServer implements IRequestServer {


    @NonNull
    @Override
    public IRequestBodyStrategy getBodyType() {
        // 参数以 Json 格式提交（默认是表单）
        return RequestBodyType.JSON;
    }

    @NonNull
    @Override
    public String getHost() {
        return "http://192.168.85.144:8080/";
    }
    public static String getWebsocketChatUrl() {
        return "ws://192.168.85.144:8080/chat";
    }

}