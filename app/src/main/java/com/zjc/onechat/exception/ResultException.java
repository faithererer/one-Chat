package com.zjc.onechat.exception;


import androidx.annotation.NonNull;

import com.hjq.http.exception.HttpException;
import com.zjc.onechat.entity.Result;


public final class ResultException extends HttpException {

    private final Result<?> mData;

    public ResultException(String message, Result<?> data) {
        super(message);
        mData = data;
    }

    public ResultException(String message, Throwable cause, Result<?> data) {
        super(message, cause);
        mData = data;
    }

    @NonNull
    public Result<?> getResult() {
        return mData;
    }
}