package com.zjc.onechat.adapter;


 import android.content.Context;
 import android.content.Intent;
 import android.util.Log;
 import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
 import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
 import com.bumptech.glide.load.resource.bitmap.CircleCrop;
 import com.bumptech.glide.request.RequestOptions;
 import com.zjc.onechat.activity.ChatActivity;
 import com.zjc.onechat.R;
import com.zjc.onechat.entity.Friend;

 import java.util.List;


public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.FriendsViewHolder> {
    private Context context;
    private List<Friend> friendsList;
    public FriendsAdapter(List<Friend> friendsList, Context context) {
        this.friendsList = friendsList;
        this.context = context;
    }



    @NonNull
    @Override
    public FriendsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_friend, parent, false);
        return new FriendsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendsViewHolder holder, int position) {
        Friend friend = friendsList.get(position);
        holder.friendName.setText(friend.getNickName());

        // 创建圆形变换器
        RequestOptions requestOptions = new RequestOptions()
                .placeholder(R.drawable.ic_profile) // 默认头像
                .transform(new CircleCrop()); // 圆形变换

        // 加载图片并应用圆形变换
        Glide.with(holder.friendAvatar.getContext())
                .load(friend.getAvatar())
                .apply(requestOptions)
                .into(holder.friendAvatar);

        holder.itemView.setOnClickListener(v -> {
            Log.d("投喂1","aa");

            Log.d("投喂1", friend.getId().toString());
            Intent intent = new Intent(context, ChatActivity.class);
            intent.putExtra("friend_name", friend.getNickName());
            intent.putExtra("friend_id", friend.getId().toString());
            intent.putExtra("friend_avatar", friend.getAvatar());

            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return friendsList.size();
    }

    static class FriendsViewHolder extends RecyclerView.ViewHolder {

        ImageView friendAvatar;
        TextView friendName;

        public FriendsViewHolder(@NonNull View itemView) {
            super(itemView);
            friendAvatar = itemView.findViewById(R.id.friend_avatar);
            friendName = itemView.findViewById(R.id.friend_name);
        }
    }
}

