package com.hhx7.im.data.model;

import com.stfalcon.chatkit.commons.models.IMessage;
import com.stfalcon.chatkit.commons.models.MessageContentType;

import java.io.Serializable;
import java.util.Date;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.annotation.Index;
import io.objectbox.relation.ToOne;

/*
 * Created by troy379 on 04.04.17.
 */
@Entity
public class Message implements IMessage,Serializable,
        MessageContentType.Image, /*this is for default image messages implementation*/
        MessageContentType/*and this one is for custom text type (in this case - voice message)*/ {

    @Id
    private long id_;
    @Index
    private String id;
    private String text;
    private Date createdAt;
    public  ToOne<User> sendUser;
    public ToOne<User> receiveUser;
    public ToOne<Picture> image;
    public ToOne<Voice> voice;
    private String url;


    public Message(){
    }

    public Message(String id, User sendUser, User receiveUser, String text) {
        this(id, sendUser,receiveUser, text, new Date());
    }

    public Message(String id, User sendUser,User receiveUser, String text, Date createdAt) {
        this.id = id;
        this.text = text;
        this.sendUser.setTarget(sendUser);
        this.receiveUser.setTarget(receiveUser);
        this.createdAt = createdAt;
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
    public String getText() {
        return text;
    }

    @Override
    public Date getCreatedAt() {
        return createdAt;
    }

    @Override
    public User getUser() {
        return this.sendUser.getTarget();
    }

    @Override
    public String getImageUrl() {
        return image.isNull()  ? null : image.getTarget().url;
    }

    public User getReceiveUser() {
        return receiveUser.getTarget();
    }

    public void setReceiveUser(User receiveUser) {
        this.receiveUser.setTarget(receiveUser);
    }

    public Voice getVoice() {
        return voice.getTarget();
    }

    public String getStatus() {
        return "Sent";
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public void setImage(Picture image) {

        this.image.setTarget(image);
    }

    public void setVoice(Voice voice) {
        this.voice.setTarget(voice);
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }




}
