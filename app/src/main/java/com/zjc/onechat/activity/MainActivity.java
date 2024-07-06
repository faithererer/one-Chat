package com.zjc.onechat.activity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.zjc.onechat.R;
import com.zjc.onechat.fragment.FriendsFragment;
import com.zjc.onechat.fragment.MessagesFragment;
import com.zjc.onechat.fragment.ProfileFragment;

import jp.wasabeef.glide.transformations.BlurTransformation;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private FragmentManager fragmentManager;
    private Fragment messagesFragment;
    private Fragment friendsFragment;
    private Fragment profileFragment;
    private static final int MY_PERMISSIONS_REQUEST_READ_MEDIA_IMAGES = 123; // 可以是任何非负整数
    private RelativeLayout relativeLayout;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        relativeLayout = findViewById(R.id.message_main);
        initBackground();
        bottomNavigationView = findViewById(R.id.navigation);
        bottomNavigationView.setLabelVisibilityMode(NavigationBarView.LABEL_VISIBILITY_LABELED);
        fragmentManager = getSupportFragmentManager();
        if (savedInstanceState == null) {
            messagesFragment = new MessagesFragment();
            friendsFragment = new FriendsFragment();
            profileFragment = new ProfileFragment();
            fragmentManager.beginTransaction().add(R.id.container, messagesFragment).commit();
        }

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull android.view.MenuItem item) {
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                switch (item.getItemId()) {
                    case R.id.navigation_messages:
                        transaction.replace(R.id.container, messagesFragment).commit();
                        return true;
                    case R.id.navigation_friends:
                        transaction.replace(R.id.container, friendsFragment).commit();
                        return true;
                    case R.id.navigation_profile:
                        transaction.replace(R.id.container, profileFragment).commit();
                        return true;
                }
                return false;
            }
        });
    }

    void initBackground(){
        // 在你的Activity或Fragment中加载背景图片，并设置毛玻璃效果
        RequestOptions requestOptions = new RequestOptions()
                .transform(new BlurTransformation(25)); // 设置毛玻璃效果，数字越大效果越明显


        Glide.with(this)
                .load(R.drawable.bg_main) // 加载背景图片资源
                .apply(requestOptions)
                .into(new SimpleTarget<Drawable>() {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        // 设置加载完成的背景图片到ConstraintLayout
                        relativeLayout.setBackground(resource);
                    }


                });

    }

}
