package com.zjc.onechat.adapter;

import com.stfalcon.chatkit.commons.models.IDialog;

import com.stfalcon.chatkit.commons.models.IDialog;
import com.stfalcon.chatkit.commons.models.IMessage;
import com.stfalcon.chatkit.commons.models.IUser;
import com.zjc.onechat.dao.entity.Dialog;

import java.util.List;

public class DialogAdapter implements IDialog<IMessage> {

    private Dialog dialog;

    public DialogAdapter(Dialog dialog) {
        this.dialog=dialog;
    }


    @Override
    public String getId() {
        return dialog.getId();
    }

    @Override
    public String getDialogPhoto() {
        return getDialogPhoto();
    }

    @Override
    public String getDialogName() {
        return dialog.getDialogName();
    }

    @Override
    public List<? extends IUser> getUsers() {
        return dialog.getUsers();
    }

    @Override
    public IMessage getLastMessage() {
        return getLastMessage();
    }

    @Override
    public void setLastMessage(IMessage message) {

    }

    @Override
    public int getUnreadCount() {
        return dialog.getUnreadCount();
    }
}
