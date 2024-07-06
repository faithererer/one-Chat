package com.zjc.onechat.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hjq.http.EasyHttp;
import com.hjq.http.listener.HttpCallbackProxy;
import com.hjq.http.listener.OnHttpListener;
import com.hjq.toast.Toaster;
import com.zjc.onechat.R;
import com.zjc.onechat.adapter.FriendRequestAdapter;
import com.zjc.onechat.api.FriendListApi;
import com.zjc.onechat.api.FriendRequestApi;
import com.zjc.onechat.api.HandleRequestApi;
import com.zjc.onechat.entity.FriendRequest;
import com.zjc.onechat.entity.Result;


import java.util.ArrayList;
import java.util.List;

public class FriendRequestsActivity extends AppCompatActivity {

    private RecyclerView friendRequestsRecyclerView;
    private FriendRequestAdapter friendRequestAdapter;
    private List<FriendRequest> friendRequestList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_requests);
        friendRequestsRecyclerView = findViewById(R.id.friend_requests_recycler_view);
        friendRequestsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        friendRequestList = new ArrayList<>();
        fetchFriendRequest();
//        // 示例好友请求数据
//        friendRequestList.add(new FriendRequest(1L, 2L, "https://img.loliapi.cn/i/pc/img226.webp", "好友小张"));
//        friendRequestList.add(new FriendRequest(3L, 4L, "https://img.loliapi.cn/i/pc/img226.webp", "同事小李"));
//        friendRequestList.add(new FriendRequest(5L, 6L, "https://img.loliapi.cn/i/pc/img226.webp", "好友小王"));
        findViewById(R.id.req_ivBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void onAcceptClick(FriendRequest request) {
        EasyHttp.get(this)
                .api(new HandleRequestApi()
                        .setRequestId(request.getId())
                        .setAccept(true))
                .request(new OnHttpListener<Result<String>>() {
                    @Override
                    public void onHttpSuccess(Result<String> stringResult) {
                        if(stringResult.getCode()==0) {
                            Toaster.show("已同意 " + request.getNickName() + " 的好友请求");
                        }
                     }

                    @Override
                    public void onHttpFail(Throwable throwable) {
                        Toaster.show("处理异常");
                    }
                });
        // TODO: 添加处理逻辑
    }

    private void onRejectClick(FriendRequest request) {
        EasyHttp.get(this)
                .api(new HandleRequestApi()
                        .setRequestId(request.getId())
                        .setAccept(false))
                .request(new OnHttpListener<Result<String>>() {
                    @Override
                    public void onHttpSuccess(Result<String> stringResult) {
                        if(stringResult.getCode()==0) {
                            Toaster.show("已拒绝 " + request.getNickName() + " 的好友请求");
                        }
                    }

                    @Override
                    public void onHttpFail(Throwable throwable) {
                        Toaster.show("处理异常");
                    }
                });
    }

    public void fetchFriendRequest(){
        String currentUserId = getCurrentUserId();
        EasyHttp.get(this)
                .api(new FriendRequestApi()
                        .setUserId(getCurrentUserId()))
                .request(new OnHttpListener<Result<List<FriendRequest>>>() {
                    @Override
                    public void onHttpSuccess(Result<List<FriendRequest>> beanResult) {
                        List<FriendRequest> data =  beanResult.getData();
                        friendRequestList=data;
                        Log.d("列表", friendRequestList.toString());
                        friendRequestAdapter = new FriendRequestAdapter(friendRequestList, FriendRequestsActivity.this::onAcceptClick, FriendRequestsActivity.this::onRejectClick);
                        runOnUiThread(()->{
                            friendRequestsRecyclerView.setAdapter(friendRequestAdapter);
                        });
                    }

                    @Override
                    public void onHttpFail(Throwable throwable) {

                    }
                });

    }
    private String getCurrentUserId() {
        SharedPreferences sharedPreferences = getSharedPreferences("authorization", MODE_PRIVATE);
        return sharedPreferences.getString("id", "");
    }
}
