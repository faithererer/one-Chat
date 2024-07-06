package com.zjc.onechat.aliyunOss;

import android.app.Application;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback;
import com.alibaba.sdk.android.oss.common.auth.OSSPlainTextAKSKCredentialProvider;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class OSSUploader {

    private OSS oss;
    private String endpoint;
    private String bucketName;
    private String accessKeyId;
    private String accessKeySecret;
    private Application application;

    public OSSUploader(Application application) {
        this.application=application;
        initOSSClient();
    }

    private void initOSSClient() {
        endpoint= Configs.OSS_ENDPOINT;
        bucketName= Configs.BUCKET_NAME;
        accessKeyId= Configs.OSS_ACCESS_KEY_ID;
        accessKeySecret= Configs.OSS_ACCESS_KEY_SECRET;
        OSSPlainTextAKSKCredentialProvider credentialProvider = new OSSPlainTextAKSKCredentialProvider(accessKeyId, accessKeySecret);
        oss = new OSSClient(application, endpoint, credentialProvider);
    }

    public void uploadImage(Uri imageUri, final OnImageUploadListener listener) {
        // 获取图片文件的实际路径
        String imagePath = getPathFromUri(imageUri);

        if (imagePath == null) {
            // 如果路径为空，表示获取路径失败，可以进行相应的错误处理
            Log.e("OSSUploader", "Failed to get image path from URI");
            if (listener != null) {
                listener.onUploadFailure(new Exception("Failed to get image path from URI"), null);
            }
            return;
        }

        // 创建 File 对象
        File imageFile = new File(imagePath);

        if (!imageFile.exists() || !imageFile.isFile()) {
            // 文件不存在或者不是一个有效的文件
            Log.e("OSSUploader", "File does not exist or is not a valid file: " + imagePath);
            if (listener != null) {
                listener.onUploadFailure(new FileNotFoundException("File does not exist or is not a valid file: " + imagePath), null);
            }
            return;
        }

        // 配置上传到 OSS 的路径和文件名
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
        String objectKey = "images/" + sdf.format(new Date()) + "_" + imageFile.getName();

        // 创建上传请求
        PutObjectRequest putRequest = new PutObjectRequest(bucketName, objectKey, imageFile.getPath());

        // 异步上传
        oss.asyncPutObject(putRequest, new OSSCompletedCallback<PutObjectRequest, PutObjectResult>() {
            @Override
            public void onSuccess(PutObjectRequest request, PutObjectResult result) {
                // 上传成功，生成临时签名 URL
                Date expiration = new Date(System.currentTimeMillis() + 3600 * 1000); // 设置URL过期时间为1小时
                String imageUrl = null;
                try {
                    imageUrl = oss.presignConstrainedObjectURL(bucketName, objectKey, expiration.getTime());
                } catch (ClientException e) {
                    throw new RuntimeException(e);
                }
                Log.d("OSSUploader", "Image uploaded: " + imageUrl);

                // 回调通知上传成功
                if (listener != null) {
                    listener.onUploadSuccess(imageUrl);
                }
            }

            @Override
            public void onFailure(PutObjectRequest request, ClientException clientException, ServiceException serviceException) {
                // 上传失败，处理异常
                Log.e("OSSUploader", "Upload failed: " + clientException.getMessage(), clientException);
                if (serviceException != null) {
                    Log.e("OSSUploader", "Error code: " + serviceException.getErrorCode());
                    Log.e("OSSUploader", "Error message: " + serviceException.getRawMessage());
                }

                // 回调通知上传失败
                if (listener != null) {
                    listener.onUploadFailure(clientException, serviceException);
                }
            }


        });
    }

    // 辅助方法：从 Uri 获取文件实际路径
    private String getPathFromUri(Uri uri) {
        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        Cursor cursor = application.getContentResolver().query(uri, filePathColumn, null, null, null);
        if (cursor == null) {
            return null;
        }
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String filePath = cursor.getString(columnIndex);
        cursor.close();
        return filePath;
    }


    public interface OnImageUploadListener {
        void onUploadSuccess(String imageUrl);
        void onUploadFailure(Exception e, com.alibaba.sdk.android.oss.ServiceException serviceException);
    }
}
