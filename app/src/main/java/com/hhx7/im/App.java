package com.hhx7.im;

import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.util.Log;

import com.hhx7.im.Net.Address;
import com.hhx7.im.data.fixtures.DialogsFixtures;
import com.hhx7.im.data.model.Dialog;
import com.hhx7.im.data.model.Message;
import com.hhx7.im.data.model.MyObjectBox;
import com.hhx7.im.data.model.Picture;
import com.hhx7.im.data.model.User;
import com.hhx7.im.data.model.Voice;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import io.objectbox.Box;
import io.objectbox.BoxStore;
import io.objectbox.android.AndroidObjectBrowser;
import io.objectbox.android.BuildConfig;
import io.objectbox.query.Query;

public class App extends Application {




    public static final UUID MY_UUID=UUID.fromString("00001001-0000-1000-8000-00805F9B34FA");

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    private User currentUser;

    public Message getDefaultMsg() {
        return defaultMsg;
    }

    public void setDefaultMsg(Message defaultMsg) {
        this.defaultMsg = defaultMsg;
    }

    private Message defaultMsg;
    private User defaultUser;


    public Address getLocalBTAddress() {
        return localBTAddress;
    }


    public void setLocalBTAddress(Address localBTAddress) {
        this.localBTAddress = localBTAddress;
    }

    public Address getLocalIpAddress() {
        return localIpAddress;
    }

    public void setLocalIpAddress(Address localIpAddress) {
        this.localIpAddress = localIpAddress;
    }

    private Address localBTAddress;
    private Address localIpAddress;
    private BoxStore boxStore;

    public Box<Dialog> getDialogBox() {
        return dialogBox;
    }

    public void setDialogBox(Box<Dialog> dialogBox) {
        this.dialogBox = dialogBox;
    }

    private Box<Dialog> dialogBox;
    private Box<Message> messageBox;
    private Box<User> userBox;
    private Box<Picture> pictureBox;
    private Box<Voice> voiceBox;
    private Query<Dialog> dialogQuery;
    private Query<Message> messageQuery;

    public ThreadPoolExecutor getThreadPoolExecutor() {
        return threadPoolExecutor;
    }

    private ThreadPoolExecutor threadPoolExecutor;

    @Override
    public void onCreate() {

        super.onCreate();
        threadPoolExecutor=new ThreadPoolExecutor(5,10,5000, TimeUnit.MILLISECONDS,new LinkedBlockingQueue<Runnable>());
        boxStore = MyObjectBox.builder().androidContext(App.this).build();
        dialogBox= (boxStore.boxFor(Dialog.class));
        messageBox=boxStore.boxFor(Message.class);
        userBox=boxStore.boxFor(User.class);
        pictureBox=boxStore.boxFor(Picture.class);
        voiceBox=boxStore.boxFor(Voice.class);

        messageBox.removeAll();
        userBox.removeAll();
        pictureBox.removeAll();
        voiceBox.removeAll();
        dialogBox.removeAll();




        dialogQuery=dialogBox.query().build();
        messageQuery=messageBox.query().build();

        defaultUser=new User("0","","",false);
        defaultMsg=new Message("0",defaultUser,defaultUser,"",new Date());


        if (BuildConfig.DEBUG) {
            new AndroidObjectBrowser(boxStore).start(this);
        }

        Log.d("App", "Using ObjectBox " + BoxStore.getVersion() + " (" + BoxStore.getVersionNative() + ")");
    }

    public BoxStore getBoxStore() {
        return boxStore;
    }

    public Box<Message> getMessageBox(){
        return messageBox;
    }

    public Box<User> getUserBox(){
        return userBox;
    }
    public List<Dialog> getDialogs() {

        Log.i("Is Null",Long.toString(dialogQuery.count()));
        return dialogQuery.find();
    }

    public List<Message> getMessages(){
        return messageQuery.find();
    }

    public User getCurrentUser(){
        return currentUser;
    }

    private void initDialog(){
        List<User> users=userBox.query().build().find();
        List<Message> messages=messageBox.query().build().find();
        Dialog dialog=new Dialog(
                DialogsFixtures.getRandomId(),
                DialogsFixtures.groupChatTitles.get(0),
                DialogsFixtures.groupChatImages.get(0),
                users,
                messages.get(0),
                0);
        dialogBox.put(dialog);
    }
    private void initUser(){
        User user=new User(
                DialogsFixtures.getRandomId(),
                DialogsFixtures.getRandomName(),
                DialogsFixtures.getRandomAvatar(),
                DialogsFixtures.getRandomBoolean()
        );
        userBox.put(user);
    }

    private void initMessage(){

        Message msg=new Message(
                DialogsFixtures.getRandomId(),
                userBox.query().build().find().get(0),
                userBox.query().build().find().get(0),
                DialogsFixtures.getRandomMessage(),
                new Date()
        );
        messageBox.put(msg);
    }

}
