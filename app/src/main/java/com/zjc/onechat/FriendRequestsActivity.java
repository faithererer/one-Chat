package com.zjc.onechat;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.zjc.onechat.R;
import com.zjc.onechat.adapter.FriendRequestAdapter;
import com.zjc.onechat.entity.FriendRequest;


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
        // 示例好友请求数据
        friendRequestList.add(new FriendRequest(1L, 2L, "https://example.com/avatar1.png", "好友小张"));
        friendRequestList.add(new FriendRequest(3L, 4L, "https://example.com/avatar2.png", "同事小李"));
        friendRequestList.add(new FriendRequest(5L, 6L, "https://example.com/avatar3.png", "好友小王"));

        friendRequestAdapter = new FriendRequestAdapter(friendRequestList, this::onAcceptClick, this::onRejectClick);
        friendRequestsRecyclerView.setAdapter(friendRequestAdapter);
    }

    private void onAcceptClick(FriendRequest request) {
        Toast.makeText(this, "已同意 " + request.getNickName() + " 的好友请求", Toast.LENGTH_SHORT).show();
        // TODO: 添加处理逻辑
    }

    private void onRejectClick(FriendRequest request) {
        Toast.makeText(this, "已拒绝 " + request.getNickName() + " 的好友请求", Toast.LENGTH_SHORT).show();
        // TODO: 添加处理逻辑
    }
}
