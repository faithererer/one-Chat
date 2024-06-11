package com.zjc.onechat.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.zjc.onechat.R;
import com.zjc.onechat.entity.FriendRequest;

import java.util.List;

public class FriendRequestAdapter extends RecyclerView.Adapter<FriendRequestAdapter.ViewHolder> {

    private final List<FriendRequest> friendRequests;
    private final OnClickListener acceptClickListener;
    private final OnClickListener rejectClickListener;

    public FriendRequestAdapter(List<FriendRequest> friendRequests, OnClickListener acceptClickListener, OnClickListener rejectClickListener) {
        this.friendRequests = friendRequests;
        this.acceptClickListener = acceptClickListener;
        this.rejectClickListener = rejectClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_friend_request, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FriendRequest request = friendRequests.get(position);
        holder.nickNameText.setText(request.getNickName());
        Glide.with(holder.itemView.getContext()).load(request.getAvatar()).into(holder.avatarImage);

        holder.acceptButton.setOnClickListener(v -> acceptClickListener.onClick(request));
        holder.rejectButton.setOnClickListener(v -> rejectClickListener.onClick(request));
    }

    @Override
    public int getItemCount() {
        return friendRequests.size();
    }

    public interface OnClickListener {
        void onClick(FriendRequest request);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView avatarImage;
        TextView nickNameText;
        Button acceptButton;
        Button rejectButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            avatarImage = itemView.findViewById(R.id.avatar_image);
            nickNameText = itemView.findViewById(R.id.nick_name_text);
            acceptButton = itemView.findViewById(R.id.accept_button);
            rejectButton = itemView.findViewById(R.id.reject_button);
        }
    }
}
