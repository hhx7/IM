package com.hhx7.im.data.model;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;

@Entity
public  class Voice {

    @Id
    private long id;
    String url;
    int duration;

    public Voice(){

    }
    public Voice(String url, int duration) {
        this.url = url;
        this.duration = duration;
    }

    public void setId(long id) {
        this.id = id;
    }
    public long getId() {
        return id;
    }

    public String getUrl() {
        return url;
    }

    public int getDuration() {
        return duration;
    }
}
