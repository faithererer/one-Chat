package com.zjc.onechat;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomnavigation.LabelVisibilityMode;
import com.google.android.material.navigation.NavigationBarView;
import com.zjc.onechat.R;
import com.zjc.onechat.adapter.ChatListAdapter;
import com.zjc.onechat.entity.Chat;
import com.zjc.onechat.fragment.FriendsFragment;
import com.zjc.onechat.fragment.MessagesFragment;
import com.zjc.onechat.fragment.ProfileFragment;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private FragmentManager fragmentManager;
    private Fragment messagesFragment;
    private Fragment friendsFragment;
    private Fragment profileFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

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
}
