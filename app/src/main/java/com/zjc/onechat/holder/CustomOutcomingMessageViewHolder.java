package com.zjc.onechat.holder;

import android.view.View;

import com.bumptech.glide.Glide;
import com.stfalcon.chatkit.messages.MessageHolders;
import com.stfalcon.chatkit.messages.MessagesListAdapter;
import com.zjc.onechat.R;
import com.zjc.onechat.adapter.MessageAdapter;
import com.zjc.onechat.dao.entity.Message;

import de.hdodenhof.circleimageview.CircleImageView;

public class CustomOutcomingMessageViewHolder extends MessageHolders.OutcomingTextMessageViewHolder<MessageAdapter> {

    private CircleImageView avatarImageView;

    public CustomOutcomingMessageViewHolder(View itemView, Object payload) {
        super(itemView, payload);
        avatarImageView = itemView.findViewById(R.id.outcomingMessageUserAvatar);

    }
    @Override
    public void onBind(MessageAdapter message) {
        super.onBind(message);

        // 使用 Glide 加载头像
        String avatarUrl = message.getUser().getAvatar(); // 获取头像 URL 的方法，根据你的数据源调整
        if (avatarUrl != null && !avatarUrl.isEmpty()) {
            Glide.with(itemView.getContext())
                    .load(avatarUrl)
                    .placeholder(R.drawable.ic_profile) // 设置默认头像
                    .error(R.drawable.ic_profile) // 加载错误时显示的默认头像
                    .into(avatarImageView);
        } else {
            avatarImageView.setImageResource(R.drawable.ic_profile); // 没有 URL 时显示默认头像
        }
    }
}
