package com.zjc.onechat.fragment;

import static android.app.Activity.RESULT_OK;
import static android.content.ContentValues.TAG;
import static android.content.Context.MODE_PRIVATE;


import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.alibaba.sdk.android.oss.ServiceException;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hjq.http.EasyHttp;
import com.hjq.http.listener.OnHttpListener;
import com.hjq.toast.Toaster;
import com.zjc.onechat.ChatApp;
import com.zjc.onechat.R;
import com.zjc.onechat.activity.LoginActivity;
import com.zjc.onechat.adapter.MessageAdapter;
import com.zjc.onechat.adapter.UserAdapter;
import com.zjc.onechat.aliyunOss.OSSUploader;
import com.zjc.onechat.api.UpdateUserApi;
import com.zjc.onechat.dao.UserDao;
import com.zjc.onechat.dao.entity.Message;
import com.zjc.onechat.dao.entity.User;
import com.zjc.onechat.entity.Result;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ProfileFragment extends Fragment {
    private static final int IMAGE_PICKER_REQUEST = 123; // 任意正整数作为请求码

    private ImageView avatarImageView;
    private EditText nicknameEditText;
    private Button changeAvatarButton;
    private Button saveButton;
    private UserDao userDao;
    private OSSUploader ossUploader;
    private User newUser;

    private SharedPreferences sharedPreferences;

    private ExecutorService executorService = Executors.newSingleThreadExecutor();



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);
        userDao = ((ChatApp) getActivity().getApplication()).getUserDao();
        ossUploader = new OSSUploader(getActivity().getApplication());
        // Initialize views
        avatarImageView = rootView.findViewById(R.id.image_avatar);
        nicknameEditText = rootView.findViewById(R.id.edit_text_nickname);
        changeAvatarButton = rootView.findViewById(R.id.btn_change_avatar);
        saveButton = rootView.findViewById(R.id.btn_save);
        executorService.execute(()->{
            newUser = userDao.getUserById(Long.parseLong(getCurrentUserId()));
            // Load avatar using Glide (assuming avatarUrl is fetched from database)
            String avatarUrl = newUser.getAvatar();
            getActivity().runOnUiThread(()->{
                Glide.with(this)
                        .load(avatarUrl)
                        .circleCrop()
                        .apply(RequestOptions.circleCropTransform())
                        .error(R.drawable.ic_profile) // Error image
                        .into(avatarImageView);
                nicknameEditText.setText(newUser.getNick_name());
            });
            View jokerView = getActivity().findViewById(R.id.animationView);
            jokerView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    logOut();
                }
            });


        });




        // Setup button click listeners
        changeAvatarButton.setOnClickListener(v -> {
            openImagePicker();
        });

        saveButton.setOnClickListener(v -> {
            executorService.execute(()->{
                EasyHttp.post(this)
                        .api(new UpdateUserApi().setId(newUser.getId())
                                .setNick_name(String.valueOf(nicknameEditText.getText()))
                                .setAvatar(newUser.getAvatar()))
                        .request(new OnHttpListener<Result<String>>() {
                            @Override
                            public void onHttpSuccess(Result<String> stringResult) {
                                executorService.execute(()->{
                                    newUser.setNick_name(String.valueOf(nicknameEditText.getText()));
                                    userDao.updateUser(newUser);
                                });
                                Toaster.show("修改成功");
                            }

                            @Override
                            public void onHttpFail(Throwable throwable) {
                                Toaster.show("修改失败，请检查网络..");
                            }
                        });
            });
        });

        return rootView;
    }

    private void logOut() {
        // 创建确认对话框
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("确认退出登录");
        builder.setMessage("您确定要退出登录吗？");
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 用户点击确认，执行退出登录操作
                sharedPreferences = getActivity().getSharedPreferences("authorization", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.remove("authorization");
                editor.apply(); // 应用更改
                // 在此处添加其他退出逻辑，如跳转到登录界面等
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // 清除栈中的所有 Activity
                startActivity(intent);
                getActivity().finish(); // 结束当前 Activity
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 用户点击取消，不执行任何操作，对话框会自动关闭
            }
        });
        // 显示对话框
        builder.show();
    }


    private String getCurrentUserId() {
        sharedPreferences = getActivity().getSharedPreferences("authorization", MODE_PRIVATE);
        return sharedPreferences.getString("id", "");
    }
    private void openImagePicker() {
        // 这里是打开图片选择器的具体实现，可以根据具体情况选择合适的方式
        // 例如，使用系统自带的图片选择器
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, IMAGE_PICKER_REQUEST); // IMAGE_PICKER_REQUEST 是一个标识请求码
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGE_PICKER_REQUEST && resultCode == RESULT_OK) {
            if (data != null) {
                // 获取选中的图片 URI
                Uri selectedImageUri = data.getData();


                ossUploader.uploadImage(selectedImageUri, new OSSUploader.OnImageUploadListener() {
                    @Override
                    public void onUploadSuccess(String imageUrl) {
                        Toaster.show("上传图片成功："+imageUrl);
                        newUser.setAvatar(imageUrl);
                    }

                    @Override
                    public void onUploadFailure(Exception e, ServiceException serviceException) {
                        Log.d(TAG, "onUploadFailure: "+e.getMessage());
                        Toaster.show(e);
                    }
                });

            }
        }
    }

}