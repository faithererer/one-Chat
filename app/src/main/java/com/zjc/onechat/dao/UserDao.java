package com.zjc.onechat.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.zjc.onechat.dao.entity.User;

import java.util.List;

@Dao
public interface UserDao {
    @Insert
    void insertUser(User user);

    @Query("SELECT * FROM user WHERE id = :userId")
    User getUserById(long userId);

    @Query("SELECT * FROM user")
    List<User> getAllUsers();
    @Query("SELECT EXISTS(SELECT 1 FROM user WHERE id = :userId)")
    boolean userExists(long userId);
    // 根据id更新
    @Update
    void updateUser(User user);
}
