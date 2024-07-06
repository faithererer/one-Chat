package com.zjc.onechat.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.Nullable;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.navigation.NavigationBarView;
import com.zjc.onechat.R;
import com.zjc.onechat.fragment.FriendsFragment;
import com.zjc.onechat.fragment.MessagesFragment;
import com.zjc.onechat.fragment.ProfileFragment;

public class OpenAppActivity extends BaseActivity{
    private static final long ANIMATION_DURATION = 1500; // 动画播放时间，单位：毫秒;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_openapp);

        LottieAnimationView animationView = findViewById(R.id.open_an);
        animationView.setAnimation(R.raw.open); // 设置动画资源
        animationView.playAnimation(); // 播放动画

        // 延迟跳转到下一个界面或主界面
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish(); // 销毁当前 Activity
        }, ANIMATION_DURATION);
    }
}
