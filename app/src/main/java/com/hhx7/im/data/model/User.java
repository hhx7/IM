package com.hhx7.im.data.model;

import com.stfalcon.chatkit.commons.models.IUser;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.annotation.Index;

/*
 * Created by troy379 on 04.04.17.
 */

@Entity
public class User implements IUser {

    public static String DATA_NAME="name";
    public static String DATA_AVATAR="avatar";
    public static String DATA_BT_ADDR="addr";

    @Id
    private long id_;
    @Index
    private String id;
    private String name;
    private String avatar;

    private String btAddr;
    private boolean online;

    public User(){

    }
    public User(String id, String name, String avatar, boolean online) {
        this.id = id;
        this.name = name;
        this.avatar = avatar;
        this.online = online;
    }

    public String getBtAddr() {
        return btAddr;
    }

    public void setBtAddr(String btAddr) {
        this.btAddr = btAddr;
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
    public String getName() {
        return name;
    }

    @Override
    public String getAvatar() {
        return avatar;
    }

    public boolean isOnline() {
        return online;
    }
}
