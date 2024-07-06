package com.zjc.onechat.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.zjc.onechat.dao.entity.Message;

import java.util.List;

@Dao
public interface MessageDao {
    @Insert
    void insertMessage(Message message);

    @Query("SELECT * FROM message WHERE chat_id = :chatId ORDER BY sent_time ASC")
    List<Message> getMessagesForChat(String chatId);
    @Update
    void update(Message message);
    // 根据chatId获取未读消息数量
    @Query("SELECT COUNT(*) FROM message WHERE chat_id = :chatId AND sender_id= :friendId AND is_read = 0")
    int getUnreadMessageCount(String chatId, String friendId);


}

