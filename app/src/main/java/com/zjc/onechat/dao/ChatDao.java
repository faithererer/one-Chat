package com.zjc.onechat.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.zjc.onechat.dao.entity.Chat;

import java.util.List;

@Dao
public interface ChatDao {
    @Insert
    void insertChat(Chat chat);

    @Query("SELECT * FROM chat WHERE user_id = :userId")
    List<Chat> getChatsForUser(long userId);
    @Query("SELECT EXISTS(SELECT 1 FROM chat WHERE id = :chatId)")
    boolean chatExists(long chatId);
    // 根据user_id和friend_id查询chat
    @Query("SELECT * FROM chat WHERE user_id = :userId AND friend_id = :friendId")
    Chat getChat(long userId, long friendId);
    @Update
    void updateChatById(Chat chat);


}
