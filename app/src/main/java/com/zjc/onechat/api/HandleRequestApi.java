package com.zjc.onechat.api;

import com.airbnb.lottie.L;
import com.hjq.http.config.IRequestApi;

public class HandleRequestApi implements IRequestApi {
    @Override
    public String getApi() {
        return "api/friends/handleRequest";
    }

    private Long requestId;
    private Boolean accept;

    public HandleRequestApi setRequestId(Long requestId) {
        this.requestId = requestId;
        return this;
    }

    public HandleRequestApi setAccept(Boolean accept) {
        this.accept = accept;
        return this;
    }

    public Boolean getAccept() {
        return accept;
    }

    public Long getRequestId() {
        return requestId;
    }
}
