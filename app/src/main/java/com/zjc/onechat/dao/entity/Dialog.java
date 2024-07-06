package com.zjc.onechat.dao.entity;

import androidx.annotation.NonNull;

import com.stfalcon.chatkit.commons.models.IDialog;
import com.zjc.onechat.adapter.MessageAdapter;
import com.zjc.onechat.adapter.UserAdapter;
import com.zjc.onechat.dao.entity.Message;

import java.util.ArrayList;
import java.util.List;

public class Dialog implements IDialog<MessageAdapter>{

    private String id;
    private String dialogName;
    private String dialogPhoto;
    private List<UserAdapter> users;
    private MessageAdapter lastMessage;
    private int unreadCount;
    private String friendId;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    private String userId;



    public Dialog() {
        users = new ArrayList<>();

    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDialogName() {
        return dialogName;
    }

    public void setDialogName(String dialogName) {
        this.dialogName = dialogName;
    }

    public String getDialogPhoto() {
        return dialogPhoto;
    }

    public void setDialogPhoto(String dialogPhoto) {
        this.dialogPhoto = dialogPhoto;
    }

    public List<UserAdapter> getUsers() {
        return users;
    }

    public void setUsers(List<UserAdapter> users) {
        this.users = users;
    }

    public MessageAdapter getLastMessage() {
        return lastMessage;
    }

    @Override
    public void setLastMessage(MessageAdapter message) {
        this.lastMessage = message;

    }



    public int getUnreadCount() {
        return unreadCount;
    }

    public void setUnreadCount(int unreadCount) {
        this.unreadCount = unreadCount;
    }

    @NonNull
    @Override
    public String toString() {
        return "Dialog{" +
                "id='" + id + '\'' +
                ", dialogName='" + dialogName + '\'' +
                ", dialogPhoto='" + dialogPhoto + '\'' +
                ", users=" + users +
                ", lastMessage=" + lastMessage +
                ", unreadCount=" + unreadCount +
                '}';
    }

    public String getFriendId() {
        return friendId;
    }

    public void setFriendId(String friendId) {
        this.friendId = friendId;
    }
}
