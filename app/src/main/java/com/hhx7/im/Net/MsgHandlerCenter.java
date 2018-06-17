package com.hhx7.im.Net;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Environment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.hhx7.im.App;
import com.hhx7.im.Net.Message.MessageBody;
import com.hhx7.im.Net.Message.MyMessage;
import com.hhx7.im.data.fixtures.DialogsFixtures;
import com.hhx7.im.data.model.Message;
import com.hhx7.im.data.model.User;
import com.hhx7.im.data.model.User_;
import com.hhx7.im.features.demo.custom.layout.CustomLayoutMessagesActivity;
import com.hhx7.im.features.main.ScanFragment;
import com.hhx7.im.utils.AppUtils;

import java.io.File;
import java.util.List;


public class MsgHandlerCenter extends Thread {


    public static String URL_GET_USER_INFO="/get_user_info";

    public static String URL_USER_INFO="/user_info";

    public static String HANDLE_MESSAGE_ACTION="com.hhx7.actions.HANDLE_MESSAGE";
    public static String HANDLE_MY_MESSAGE_ACTION="com.hhx7.actions.HANDLE_MY_MESSAGE";
    public static String ARGS_MESSAGE_NAME ="MESSAGE";

    public static String ARGS_RECEIVER_ADDR="RECEIVER_ADDR";

    private App app;

    private class MyMessageHandler implements Runnable{

        private Intent intent;
        MyMessageHandler(Intent intent){
            this.intent=intent;
        }

        @Override
        public synchronized void run(){

            MyMessage myMessage=(MyMessage)intent.getSerializableExtra(ARGS_MESSAGE_NAME);
            String url=myMessage.getUrl();

            if(url!=null){
                if(url.equals(URL_GET_USER_INFO)){

                    User currentUser=app.getCurrentUser();
                    MyMessage myMessage1=new MyMessage();
                    myMessage1.setFrom(myMessage.getTo());
                    myMessage1.setTo(myMessage.getFrom());
                    myMessage1.setUrl(URL_USER_INFO);
                    myMessage1.set(User.DATA_NAME,currentUser.getName());
                    myMessage1.set(User.DATA_AVATAR,currentUser.getAvatar());
                    myMessage1.set(User.DATA_BT_ADDR,currentUser.getBtAddr());
                    Intent intent1=new Intent(EdgeManager.FORWARD_MESSAGE_ACTION);
                    intent1.putExtra(EdgeManager.ARGS_MESSAGE_NAME,myMessage1);
                    localBroadcastManager.sendBroadcast(intent1);
                }else if(url.equals(URL_USER_INFO)){
                    String name=myMessage.get(User.DATA_NAME);
                    String avatar=myMessage.get(User.DATA_AVATAR);
                    String btAddr=myMessage.get(User.DATA_BT_ADDR);


                    User user=new User(
                            DialogsFixtures.getRandomId(),
                            name,
                            avatar,
                            true
                    );
                    user.setBtAddr(btAddr);
                    app.getUserBox().put(user);

                    Intent intent1=new Intent(ScanFragment.UPDATE_USER_ACTION);
                    intent1.putExtra(ScanFragment.ARGS_USER_ID,user.getId_());
                    localBroadcastManager.sendBroadcast(intent1);
                }
            } else{
                Message message=myMessageToMessage(myMessage);

                if(message!=null){
                    sendMessage(message);
                    addMessageToDatabase(message);

                }
            }
            /////////////////////////////////
            myMessage.deatchEdge();
        }
    };
    private class LocalReceiver extends BroadcastReceiver {
        @Override public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action != null && action.equals(HANDLE_MESSAGE_ACTION)){
                Message message=(Message)intent.getSerializableExtra(ARGS_MESSAGE_NAME);
                Address to=(Address)intent.getSerializableExtra(ARGS_RECEIVER_ADDR);
                MyMessage myMessage= messageToMyMessage(message);
                myMessage.setFrom(new Address(Address.BLUETOOTH,app.getCurrentUser().getBtAddr()));
                myMessage.setTo(to);

                sendMyMessage(myMessage);
                addMessageToDatabase(message);
            }else if(action!=null && action.equals(HANDLE_MY_MESSAGE_ACTION)){

                app.getThreadPoolExecutor().execute(new MyMessageHandler(intent));



            }
        }
    }
    private Context context;
    private LocalBroadcastManager localBroadcastManager;
    public MsgHandlerCenter(Context context){
        this.context=context;
        app=(App)context;
        localBroadcastManager=LocalBroadcastManager.getInstance(context);
        IntentFilter commands=new IntentFilter();
        commands.addAction(HANDLE_MESSAGE_ACTION);
        commands.addAction(HANDLE_MY_MESSAGE_ACTION);
        localBroadcastManager.registerReceiver(new LocalReceiver(),commands);
    }

    private void handleMyMessage(MyMessage myMessage){
        Address address=myMessage.getTo();
        if(address.getAddressZone()==Address.BLUETOOTH){
            if(address.equals(((App)context).getLocalBTAddress())){//receiver is self

                return;
            }
        }

        sendMyMessage(myMessage);

    }



    private MyMessage messageToMyMessage(Message message){

        MyMessage myMessage=new MyMessage();
        if(message.getText()!=null){ //TEXT MESSAGE
            MessageBody messageBody=MessageBody.createMsgBody(MessageBody.TEXT,message.getText());
            myMessage.setMessageBody(messageBody);
        }else if(message.getUrl()!=null){//FILE MESSAGE

            File file=new File(message.getUrl());
            if(file.exists()){
                myMessage.set("Filename",file.getName());
            }
            MessageBody messageBody=MessageBody.createMsgBody(MessageBody.FILE,message.getUrl());
            myMessage.setMessageBody(messageBody);
        }

        return myMessage;
    }

    private Message myMessageToMessage(MyMessage myMessage){
        Address from=myMessage.getFrom();


        User sendUser=getUserByAddr(from);

        MessageBody messageBody=myMessage.getMessageBody();
        if(messageBody.getDataType()==MessageBody.TEXT) {

            String str=messageBody.toStr();

            return new Message(AppUtils.getRandomId(), sendUser, app.getCurrentUser(),str );
        }else if(messageBody.getDataType()==MessageBody.FILE){
            messageBody.toFile(new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+myMessage.get("Filename")));
            Message message = new Message(AppUtils.getRandomId(), sendUser,app.getCurrentUser(), null);
            return message;
        }
        return null;
    }



    private void sendMessage(Message message){
        Intent intent=new Intent(CustomLayoutMessagesActivity.DISPLAY_MESSAGE);
        intent.putExtra(CustomLayoutMessagesActivity.ARGS_MESSAGE_NAME,message);
        localBroadcastManager.sendBroadcast(intent);
    }

    private void sendMyMessage(MyMessage myMessage){
        Intent intent=new Intent(EdgeManager.FORWARD_MESSAGE_ACTION);
        intent.putExtra(EdgeManager.ARGS_MESSAGE_NAME,myMessage);
        localBroadcastManager.sendBroadcast(intent);
    }
    private void addMessageToDatabase(Message message){
        ((App)context).getMessageBox().put(message);
    }

    private Address getAddrByUser(User user){


        return null;
    }

    private User getUserByAddr(Address addr){

        List<User> list=((App)context.getApplicationContext()).getUserBox().query().equal(User_.btAddr,addr.getAddress()).build().find();
        User user=null;
        if(list.size()>0){
            user=list.get(0);
        }

        return user;
    }
}
