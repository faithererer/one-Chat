package com.zjc.onechat.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.stfalcon.chatkit.commons.models.IMessage;
import com.stfalcon.chatkit.commons.models.IUser;
import com.stfalcon.chatkit.commons.models.MessageContentType;
import com.zjc.onechat.R;
import com.zjc.onechat.dao.entity.Message;

import java.util.Date;
import java.util.List;

// IMessageAdapter 适配器类
public class MessageAdapter implements IMessage , MessageContentType.Image {
    private Message message;
    private IUser userAdapter;  // 使用 IUser 接口类型

    public MessageAdapter(Message message, IUser userAdapter) {
        this.message = message;
        this.userAdapter = userAdapter;  // 从外部提供 IUser 实现

    }

    @Override
    public String getId() {

        return String.valueOf(message.getId());  // 确保你的 Message 实体有 getId() 方法
    }


    @Override
    public String getText() {
        return message.getContent();  // 确保你的 Message 实体有 getText() 方法
    }

    @Override
    public IUser getUser() {
        return userAdapter;  // 封装 Message 的 User 实体为 IUser
    }

    @Override
    public Date getCreatedAt() {
        Log.d("时间: ","开始");
        Date date = new Date(message.getSent_time());
        Log.d("时间: ", String.valueOf(date));

        Log.d("时间: ","结束");
        return date;

    }

    @Nullable
    @Override
    public String getImageUrl() {
        if (message != null && message.getMessage_type() != null && message.getMessage_type() == 2) {
            return message.getContent();
        }
        return null;
    }

    @Override
    public String toString() {
        return "MessageAdapter{" +
                "message=" + message +
                ", userAdapter=" + userAdapter +
                '}';
    }
}

//public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {
//    private List<Message> messages;
//
//    public MessageAdapter(List<Message> messages) {
//        this.messages = messages;
//    }
//
//    @Override
//    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message, parent, false);
//        return new MessageViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(MessageViewHolder holder, int position) {
//        Message message = messages.get(position);
//        holder.messageText.setText(message.content);
//        // 更多设置，如显示时间、发送者等
//    }
//
//    @Override
//    public int getItemCount() {
//        return messages.size();
//    }
//
//    public void addMessage(Message message) {
//        messages.add(message);
//        notifyItemInserted(messages.size() - 1);
//    }
//
//    public void setMessages(List<Message> newMessages) {
//        this.messages = newMessages;
//        notifyDataSetChanged();  // 通知适配器数据已改变，需要刷新
//    }
//
//    static class MessageViewHolder extends RecyclerView.ViewHolder {
//        TextView messageText;
//
//        public MessageViewHolder(View itemView) {
//            super(itemView);
//            messageText = itemView.findViewById(R.id.tvMessage);
//        }
//    }
//}
