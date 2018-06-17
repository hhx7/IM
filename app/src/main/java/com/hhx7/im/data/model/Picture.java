package com.hhx7.im.data.model;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;

@Entity
public  class Picture {




    @Id
    private long id;
    String url;

    public Picture(){

    }
    public Picture(String url) {
        this.url = url;
    }
    public void setId(long id) {
        this.id = id;
    }
    public long getId() {
        return id;
    }
}
