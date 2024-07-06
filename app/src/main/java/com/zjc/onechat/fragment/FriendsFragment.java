package com.zjc.onechat.fragment;


import static android.content.ContentValues.TAG;
import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hjq.http.EasyHttp;
import com.hjq.http.listener.OnHttpListener;
import com.hjq.toast.Toaster;
import com.zjc.onechat.ChatApp;
import com.zjc.onechat.activity.AddFriendActivity;
import com.zjc.onechat.activity.FriendRequestsActivity;
import com.zjc.onechat.R;
import com.zjc.onechat.adapter.FriendsAdapter;
import com.zjc.onechat.api.FriendListApi;
import com.zjc.onechat.dao.AppDatabase;
import com.zjc.onechat.dao.FriendDao;
import com.zjc.onechat.dao.UserDao;
import com.zjc.onechat.dao.entity.User;
import com.zjc.onechat.entity.Friend;
import com.zjc.onechat.entity.Result;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.widget.RelativeLayout;

public class FriendsFragment extends Fragment {
    private ExecutorService executorService = Executors.newSingleThreadExecutor();


    private RecyclerView friendsRecyclerView;
    private FriendsAdapter friendsAdapter;
    private List<Friend> friendsList;
    private UserDao userDao;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friends, container, false);
        friendsRecyclerView = view.findViewById(R.id.friends_recycler_view);
        friendsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        friendsList = new ArrayList<>();
        userDao = ((ChatApp) getActivity().getApplication()).getUserDao();

        // 添加一些示例好友数据
//        String url="data:image/jpeg;base64,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";

//        friendsList.add(new Friend("好友小张", url, 4));
//        friendsList.add(new Friend("同事小李", url, 5));
//        friendsList.add(new Friend("好友小王", url, 6));

        friendsAdapter = new FriendsAdapter(friendsList,getContext());
        friendsRecyclerView.setAdapter(friendsAdapter);

        // 搜索添加好友按钮点击事件
        ImageView searchAddFriends = view.findViewById(R.id.search_add_friends);
        searchAddFriends.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), AddFriendActivity.class);
            startActivity(intent);
        });
        // 获取RelativeLayout实例
        RelativeLayout verificationEntry = view.findViewById(R.id.verification_entry);
        // 设置点击事件
        verificationEntry.setOnClickListener(this::onVerificationClicked);

        return view;
    }
    @Override
    public void onResume() {
        super.onResume();
        fetchFriendsList();
    }


    public void onVerificationClicked(View view) {
        // 跳转验证信息activity
        Intent intent = new Intent(getContext(), FriendRequestsActivity.class);
        startActivity(intent);

    }

    // 获取好友列表
    public void fetchFriendsList() {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("authorization", getContext().MODE_PRIVATE);
        String id = sharedPreferences.getString("id", null);
        if(id==null){
            Toaster.show("未找到本地信息");
            return;
        }
        EasyHttp.get(this)
                .api(new FriendListApi().setId(id))
                .request(new OnHttpListener<Result<List<FriendListApi.Bean>>>() {
                    @Override
                    public void onHttpSuccess(Result<List<FriendListApi.Bean>> data) {
                        if (data.getSuccess() && data.getData() != null) {
                            friendsList.clear();
                             List<FriendListApi.Bean> friendList = data.getData();
                             // 更新好友列表
                            Log.d(TAG, "好的: "+friendList);
                            for (FriendListApi.Bean friend : friendList) {
                                friendsList.add(new Friend(friend.getNickName(), friend.getAvatar(), friend.getId()));
                                executorService.execute(()->{
                                    if(!userDao.userExists(friend.getId())) {
                                        User user = new User();
                                        user.setAvatar(friend.getAvatar());
                                        user.setNick_name(friend.getNickName());
                                        user.setId(friend.getId());
                                        user.setPhone(friend.getPhone());
                                        userDao.insertUser(user);
                                    }else{
                                        User user = new User();
                                        user.setAvatar(friend.getAvatar());
                                        user.setNick_name(friend.getNickName());
                                        user.setId(friend.getId());
                                        user.setPhone(friend.getPhone());
                                        userDao.updateUser(user);
                                    }
                                });
                            }
                            friendsAdapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onHttpFail(Throwable throwable) {
                        Toaster.show("获取好友列表失败");
                    }
                });



            }


    private String getCurrentUserId() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("authorization", MODE_PRIVATE);
        return sharedPreferences.getString("id", "");
    }


}
