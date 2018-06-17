package com.hhx7.im.data.model;

import com.stfalcon.chatkit.commons.models.IDialog;

import java.util.ArrayList;
import java.util.List;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.annotation.Index;

import io.objectbox.relation.ToMany;
import io.objectbox.relation.ToOne;

/*
 * Created by troy379 on 04.04.17.
 */

@Entity
public class Dialog implements IDialog<Message> {



    @Id
    private long id_;
    @Index
    private String id;
    private String dialogPhoto;
    private String dialogName;
    private int unreadCount;


    public ToMany<User> users_;
    public ToOne<User> user;
    public ToOne<Message> lastMessage_;


    public Dialog(){

    }

    public Dialog(String id, String name, String photo,
                  List<User> users, Message lastMessage, int unreadCount) {

        this.id = id;
        this.dialogName = name;
        this.dialogPhoto = photo;
        this.users_.addAll(users);
        this.lastMessage_.setTarget(lastMessage);
        this.unreadCount = unreadCount;
    }

    public Dialog(String id, String name, String photo,
                  User user, Message message,int unreadCount) {

        this.id = id;
        this.dialogName = name;
        this.dialogPhoto = photo;
        this.users_.add(user);

        this.lastMessage_.setTarget(message);
        this.unreadCount = unreadCount;
    }

    public long getId_() {
        return id_;
    }

    public void setId_(long id_) {
        this.id_ = id_;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getDialogPhoto() {
        return dialogPhoto;
    }

    @Override
    public String getDialogName() {
        return dialogName;
    }

    @Override
    public List<User> getUsers() {
        return users_.subList(0,users_.size());
    }

    @Override
    public Message getLastMessage() {
        return lastMessage_.getTarget();
    }

    @Override
    public void setLastMessage(Message lastMessage) {
        this.lastMessage_.setTarget( lastMessage);
    }

    @Override
    public int getUnreadCount() {
        return unreadCount;
    }

    public void setUnreadCount(int unreadCount) {
        this.unreadCount = unreadCount;
    }
}
