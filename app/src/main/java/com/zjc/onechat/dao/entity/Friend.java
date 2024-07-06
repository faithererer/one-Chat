package com.zjc.onechat.dao.entity;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "friend",
        foreignKeys = {
                @ForeignKey(entity = User.class, parentColumns = "id", childColumns = "user_id"),
        },
        indices = { @Index("user_id"), @Index("friend_id") })
public class Friend {
    @PrimaryKey(autoGenerate = true)
    public long id;
    public long user_id;
    public long friend_id;
}

