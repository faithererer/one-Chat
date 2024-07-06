package com.zjc.onechat.utils;

import static android.content.ContentValues.TAG;
import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.gson.JsonSyntaxException;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import okhttp3.Headers;
import okhttp3.Response;
import okhttp3.ResponseBody;
import android.content.ContextWrapper;
import com.hjq.gson.factory.GsonFactory;
import com.hjq.http.EasyLog;
import com.hjq.http.config.IRequestHandler;
import com.hjq.http.exception.DataException;
import com.hjq.http.exception.NullBodyException;
import com.hjq.http.exception.ResponseException;
import com.hjq.http.request.HttpRequest;
import com.zjc.onechat.R;
import com.zjc.onechat.api.LoginApi;
import com.zjc.onechat.broadcast.LoginBroadcastReceiver;
import com.zjc.onechat.entity.Result;
import com.zjc.onechat.exception.ResultException;


public final class RequestHandler implements IRequestHandler {

     private Context mContext;

    public RequestHandler(Context context) {
        mContext = context;
    }

    @NonNull
    @Override
    public Object requestSuccess(@NonNull HttpRequest<?> httpRequest, @NonNull Response response, @NonNull Type type) throws Throwable {

        if (response.code()==401) {
            SharedPreferences sharedPreferences = mContext.getSharedPreferences("authorization", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            // 删除authorization
            editor.remove("authorization");
            editor.apply();  // 确保更改被保存
            Log.d(TAG, "requestSuccess: 跳转登录界面");
            Intent intent = new Intent(mContext, LoginBroadcastReceiver.class);
            mContext.sendBroadcast(intent);
            Log.d(TAG, "requestSuccess: 跳转完成");


        }

        if (Response.class.equals(type)) {
            return response;
        }

        if (!response.isSuccessful()) {
            throw new ResponseException(String.format(mContext.getString(R.string.http_response_error),
                    response.code(), response.message()), response);
        }

        if (Headers.class.equals(type)) {
            return response.headers();
        }

        ResponseBody body = response.body();
        if (body == null) {
            throw new NullBodyException(mContext.getString(R.string.http_response_null_body));
        }

        if (ResponseBody.class.equals(type)) {
            return body;
        }

        // 如果是用数组接收，判断一下是不是用 byte[] 类型进行接收的
        if(type instanceof GenericArrayType) {
            Type genericComponentType = ((GenericArrayType) type).getGenericComponentType();
            if (byte.class.equals(genericComponentType)) {
                return body.bytes();
            }
        }

        if (InputStream.class.equals(type)) {
            return body.byteStream();
        }

        if (Bitmap.class.equals(type)) {
            return BitmapFactory.decodeStream(body.byteStream());
        }

        String text;
        try {
            text = body.string();
        } catch (IOException e) {
            // 返回结果读取异常
            throw new DataException(mContext.getString(R.string.http_data_explain_error), e);
        }

        // 打印这个 Json 或者文本
        EasyLog.printJson(httpRequest, text);

        if (String.class.equals(type)) {
            return text;
        }

        final Object result;

        try {
            result = GsonFactory.getSingletonGson().fromJson(text, type);
        } catch (JsonSyntaxException e) {
            // 返回结果读取异常
            throw new DataException(mContext.getString(R.string.http_data_explain_error), e);
        }

        if (result instanceof Result) {
            Result<?> model = (Result<?>) result;
            Headers headers = response.headers();
            int headersSize = headers.size();
            Map<String, String> headersMap = new HashMap<>(headersSize);
            for (int i = 0; i < headersSize; i++) {
                headersMap.put(headers.name(i), headers.value(i));
            }
            // Github issue 地址：https://github.com/getActivity/EasyHttp/issues/233
            model.setResponseHeaders(headersMap);

            if (model.getSuccess()) {
                // 代表执行成功
                return result;
            }



            throw new ResultException(model.getErrorMsg(), model);
        }
        return result;
    }

    @NonNull
    @Override
    public Throwable requestFail(@NonNull HttpRequest<?> httpRequest, @NonNull Throwable throwable) {
        return null;
    }
}
