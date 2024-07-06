package com.zjc.onechat.activity;


import static android.content.ContentValues.TAG;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.L;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.hjq.http.EasyHttp;
import com.hjq.http.listener.HttpCallbackProxy;
import com.hjq.toast.Toaster;
import com.zjc.onechat.ChatApp;
import com.zjc.onechat.R;
import com.zjc.onechat.api.AddFriendApi;
import com.zjc.onechat.api.SearchFriendApi;
import com.zjc.onechat.dao.UserDao;
import com.zjc.onechat.dao.entity.User;
import com.zjc.onechat.entity.Friend;
import com.zjc.onechat.entity.FriendSearchDTO;
import com.zjc.onechat.entity.Result;
import com.zjc.onechat.entity.UserDTO;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AddFriendActivity extends BaseActivity {
    private ExecutorService executorService = Executors.newSingleThreadExecutor();


    private EditText editTextFriendId;
    private Button buttonSearchFriend;
    private RelativeLayout layoutSearchResult;
    private ImageView imageViewFriendAvatar;
    private TextView textViewFriendName;
    private Button buttonAddFriend;
    private FriendSearchDTO foundFriend;
    private UserDTO user_me;
    private UserDao userDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);
        SharedPreferences sharedPreferences = getSharedPreferences("authorization",MODE_PRIVATE);
        String json = sharedPreferences.getString("me", null);
        userDao = ((ChatApp) getApplication()).getUserDao();

        if (json == null) {
            // 处理用户信息不存在的情况，例如创建一个新的 UserDTO 对象，或者返回错误等。
            user_me = new UserDTO();
            // 或其他逻辑处理
        } else {
            Gson gson = new Gson();
            try {
                user_me = gson.fromJson(json, UserDTO.class);
                Log.d(TAG, "我是 "+user_me.toString());
            } catch (JsonSyntaxException e) {
                // 处理 JSON 格式错误的情况
                e.printStackTrace();
                // 可能需要重置存储的 JSON 数据或其他恢复操作
            }
        }

        editTextFriendId = findViewById(R.id.edit_text_friend_id);
        buttonSearchFriend = findViewById(R.id.button_search_friend);
        layoutSearchResult = findViewById(R.id.layout_search_result);
        imageViewFriendAvatar = findViewById(R.id.image_view_friend_avatar);
        textViewFriendName = findViewById(R.id.text_view_friend_name);
        buttonAddFriend = findViewById(R.id.button_add_friend);

        buttonSearchFriend.setOnClickListener(v -> searchFriend());
        buttonAddFriend.setOnClickListener(v -> addFriend());
        View backBtn = findViewById(R.id.add_ivBack);
        backBtn.setOnClickListener((v)->finish());


    }

    private void searchFriend() {
        String friendId = editTextFriendId.getText().toString();
        if (friendId.isEmpty()) {
            Toaster.show("请输入好友ID");
            return;
        }

        EasyHttp.get(this)
                .api(new SearchFriendApi().setFriendId(friendId))
                .request(new HttpCallbackProxy<Result<FriendSearchDTO>>(this) {
                    @Override
                    public void onHttpSuccess(Result<FriendSearchDTO> data) {
                        if (data.getSuccess()&&data.getData()!=null) {
                            foundFriend = data.getData();
                            textViewFriendName.setText(foundFriend.getNickName());
                            // 创建圆形变换器
                            RequestOptions requestOptions = new RequestOptions()
                                    .placeholder(R.drawable.ic_profile) // 默认头像
                                    .transform(new CircleCrop()); // 圆形变换
                            Glide.with(AddFriendActivity.this)
                                    .load(foundFriend.getAvatar())
                                    .placeholder(R.drawable.ic_profile) // 默认头像
                                    .apply(requestOptions)
                                    .into(imageViewFriendAvatar);
                            Toaster.show(foundFriend.getFriend());
                            Boolean isFriend = foundFriend.getFriend();
                            executorService.execute(()->{
                                runOnUiThread(()->{
                                    if(isFriend){
                                        findViewById(R.id.button_add_friend).setVisibility(View.INVISIBLE);
                                    } else{
                                        findViewById(R.id.button_add_friend).setVisibility(View.VISIBLE);
                                    }
                                    layoutSearchResult.setVisibility(View.VISIBLE);

                                });

                            });


                        } else {
                            Toaster.show("未找到该好友");
                            layoutSearchResult.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onHttpFail(Throwable throwable) {
                        Toast.makeText(AddFriendActivity.this, "搜索好友失败", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void addFriend() {
        if (foundFriend == null) {
            Toast.makeText(this, "请先搜索好友", Toast.LENGTH_SHORT).show();
            return;
        }
        Log.d(TAG, "我和我的雪豹 "+user_me.getNickname()+foundFriend.getNickName());

        EasyHttp.post(this)
                .api(new AddFriendApi().setFriendId(foundFriend.getId().toString()).setUserId(user_me.getId().toString()))
                 .request(new HttpCallbackProxy<Result<String>>(this) {
                    @Override
                    public void onHttpSuccess(Result<String> data) {
                        if (data.getSuccess()) {
                            Toast.makeText(AddFriendActivity.this, "好友请求已发送", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(AddFriendActivity.this, "发送好友请求失败: " + data.getErrorMsg(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onHttpFail(Throwable throwable) {
                        Toast.makeText(AddFriendActivity.this, "发送好友请求失败", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
