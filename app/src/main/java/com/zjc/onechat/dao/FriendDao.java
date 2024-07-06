package com.zjc.onechat.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.zjc.onechat.dao.entity.Friend;

import java.util.List;

@Dao
public interface FriendDao {
    @Insert
    void insertFriend(Friend friend);

    @Query("SELECT * FROM friend WHERE user_id = :userId")
    List<Friend> getFriendsForUser(long userId);

    @Query("SELECT * FROM friend WHERE user_id = :userId")
    Friend getFriendByFId(long userId);

    // 好友是否存在
    @Query("SELECT COUNT(*) FROM friend WHERE user_id = :userId AND friend_id = :friendId")
    int checkIfFriendExists(long userId, long friendId);

}
