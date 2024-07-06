package com.zjc.onechat.dao.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import com.google.gson.annotations.SerializedName;

@Entity(tableName = "message")
public class Message {

    @PrimaryKey(autoGenerate = true)
    private Long id;

    @SerializedName("chatId")
    private long chat_id;

    @SerializedName("senderId")
    private long sender_id;

    private String content;

    @SerializedName("sentTime")
    private long sent_time;

    @SerializedName("toUserId")
    private long to_user_id;

    @SerializedName("isRead")
    private boolean is_read;
    @SerializedName("messageType")
    private Integer message_type;

    public Integer getMessage_type() {
        return message_type;
    }


    public void setMessage_type(Integer message_type) {
        this.message_type = message_type;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public long getChat_id() {
        return chat_id;
    }

    public void setChat_id(long chat_id) {
        this.chat_id = chat_id;
    }

    public long getSender_id() {
        return sender_id;
    }

    public void setSender_id(long sender_id) {
        this.sender_id = sender_id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getSent_time() {
        return sent_time;
    }

    public void setSent_time(long sent_time) {
        this.sent_time = sent_time;
    }

    public long getTo_user_id() {
        return to_user_id;
    }

    public void setTo_user_id(long to_user_id) {
        this.to_user_id = to_user_id;
    }

    public boolean isIs_read() {
        return is_read;
    }

    public void setIs_read(boolean is_read) {
        this.is_read = is_read;
    }

    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", chat_id=" + chat_id +
                ", sender_id=" + sender_id +
                ", content='" + content + '\'' +
                ", sent_time=" + sent_time +
                ", to_user_id=" + to_user_id +
                ", is_read=" + is_read +
                '}';
    }
}
