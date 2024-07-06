package com.zjc.onechat.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.zjc.onechat.dao.entity.FriendRequest;

import java.util.List;

@Dao
public interface FriendRequestDao {
    @Insert
    void insertFriendRequest(FriendRequest friendRequest);

    @Query("SELECT * FROM friend_request WHERE user_id = :userId")
    List<FriendRequest> getFriendRequestsForUser(long userId);
}
