package com.hhx7.im.features.demo.custom.layout;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;

import android.view.View;
import android.widget.Toast;
import com.aditya.filebrowser.Constants;

import com.aditya.filebrowser.FileChooser;

import com.hhx7.im.App;
import com.hhx7.im.Net.Address;
import com.hhx7.im.Net.MsgHandlerCenter;
import com.hhx7.im.data.model.Message;
import com.hhx7.im.data.model.User;
import com.hhx7.im.data.model.User_;
import com.hhx7.im.features.demo.DemoMessagesActivity;
import com.hhx7.im.features.demo.custom.layout.holders.IncomingFileMessageViewHolder;
import com.hhx7.im.features.demo.custom.layout.holders.OutcomingFileMessageViewHolder;
import com.hhx7.im.utils.AppUtils;
import com.stfalcon.chatkit.messages.MessageHolders;
import com.stfalcon.chatkit.messages.MessageInput;
import com.stfalcon.chatkit.messages.MessagesList;
import com.stfalcon.chatkit.messages.MessagesListAdapter;
import com.stfalcon.chatkit.sample.R;

import java.util.List;

import io.objectbox.Box;


public class CustomLayoutMessagesActivity extends DemoMessagesActivity
        implements MessagesListAdapter.OnMessageLongClickListener<Message>,
        MessageInput.InputListener,
        MessageInput.AttachmentsListener ,
        MessageHolders.ContentChecker<Message>{

    public static final String DISPLAY_MESSAGE="com.hhx7.actions.DISPLAY_MESSAGE";
    public static final String TEST="com.hhx7.actions.TEST";
    public static final String ARGS_MESSAGE_NAME ="MESSAGE";
    public static final String ARGS_FRIEND_USER ="FRIEND_USER";

    public static final String ARGS_FRIEND_USER_ADDR ="FRIEND_USER_ADDR";
    private MessagesList messagesList;
    private static final int PICK_FILE_REQUEST=0;

    private static final byte CONTENT_TYPE_FILE = 1;
    private User currentUser;
    private User friendUser;

    private Address to;

    LocalBroadcastManager localBroadcastManager;

    //receive intent and handle connection and session
    private class LocalReveiver extends BroadcastReceiver {
        @Override public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action.equals(DISPLAY_MESSAGE)){
                Toast.makeText(context,"msg",Toast.LENGTH_LONG).show();
                Message message = (Message) (intent.getSerializableExtra(ARGS_MESSAGE_NAME));
                receiveMessage(message);
            }else if(action.equals(TEST)){
                Toast.makeText(context,"TEST",Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void addMessageToAdapter(Message message){

        messagesAdapter.addToStart(message, true);
    }


    private void receiveMessage(Message message){
        addMessageToAdapter(message);

    }

    public static void open(Context context,String friendAddr) {
        Intent startSessionActivity=new Intent(context,CustomLayoutMessagesActivity.class);
        startSessionActivity.putExtra(ARGS_FRIEND_USER,friendAddr);
        context.startActivity(startSessionActivity);
    }
    public static void open(Context context,String friendAddr,Address to) {
        Intent startSessionActivity=new Intent(context,CustomLayoutMessagesActivity.class);
        startSessionActivity.putExtra(ARGS_FRIEND_USER,friendAddr);
        startSessionActivity.putExtra(ARGS_FRIEND_USER_ADDR,to);
        context.startActivity(startSessionActivity);
    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent=getIntent();
        String friendId=(String)intent.getSerializableExtra(ARGS_FRIEND_USER);

        App app=((App)getApplication());
        Box<User> userBox=app.getUserBox();
        currentUser=app.getCurrentUser();
        to=(Address)intent.getSerializableExtra(ARGS_FRIEND_USER_ADDR);
        List<User> list=userBox.query().equal(User_.btAddr,to.getAddress()).build().find();
        if(list.size()>0)
            friendUser=list.get(0);
        setTitle(friendUser==null?"":friendUser.getName());




        setContentView(R.layout.activity_custom_layout_messages);

        messagesList = (MessagesList) findViewById(R.id.messagesList);
        initAdapter();

        MessageInput input = (MessageInput) findViewById(R.id.input);
        input.setInputListener(this);
        input.setAttachmentsListener(this);

        //register receiver
        IntentFilter commands=new IntentFilter();
        commands.addAction(DISPLAY_MESSAGE);
        commands.addAction(TEST);

        localBroadcastManager = LocalBroadcastManager.getInstance(this); //获取实例
        LocalReveiver localReceiver = new LocalReveiver();
        localBroadcastManager.registerReceiver(localReceiver, commands); //注册广播监听器

    }

    private void sendMessage(Message message){
        Intent intent=new Intent(MsgHandlerCenter.HANDLE_MESSAGE_ACTION);
        intent.putExtra(MsgHandlerCenter.ARGS_MESSAGE_NAME, message);
        //Address ipAddress=new Address(Address.IPV6,"/2001:0:53aa:64c:18d8:6edf:4850:f41f:9000");
        intent.putExtra(MsgHandlerCenter.ARGS_RECEIVER_ADDR, to);

        localBroadcastManager.sendBroadcast(intent);
        addMessageToAdapter(message);
    }

    @Override
    public boolean onSubmit(CharSequence input) {


        Message message=new Message(AppUtils.getRandomId(),currentUser,friendUser,input.toString());
        sendMessage(message);

        return true;
    }

    @Override
    public void onAddAttachments() {
        //messagesAdapter.addToStart(MessagesFixtures.getImageMessage(), true);
        openFileBrowser(null);
    }

    @Override
    public boolean hasContentFor(Message message, byte type) {
        switch (type) {
            case CONTENT_TYPE_FILE:
                return message.getUrl()!=null;
        }
        return false;
    }

    @Override
    public void onMessageLongClick(Message message) {
        AppUtils.showToast(this, R.string.on_log_click_message, false);
    }

    private void initAdapter() {
        MessageHolders holdersConfig = new MessageHolders()
                .setIncomingTextLayout(R.layout.item_custom_incoming_text_message)
                .setOutcomingTextLayout(R.layout.item_custom_outcoming_text_message)
                .setIncomingImageLayout(R.layout.item_custom_incoming_image_message)
                .setOutcomingImageLayout(R.layout.item_custom_outcoming_image_message)
                .registerContentType(
                        CONTENT_TYPE_FILE,
                        IncomingFileMessageViewHolder.class,
                        R.layout.item_custom_incoming_file_message,
                        OutcomingFileMessageViewHolder.class,
                        R.layout.item_custom_outcoming_file_message,
                        this);

        super.messagesAdapter = new MessagesListAdapter<>(super.senderId, holdersConfig, super.imageLoader);
        super.messagesAdapter.setOnMessageLongClickListener(this);
        super.messagesAdapter.setLoadMoreListener(this);

        messagesList.setAdapter(super.messagesAdapter);
    }

    public void openFileBrowser(View view){

        Intent i2 = new Intent(getApplicationContext(), FileChooser.class);
        i2.putExtra(Constants.SELECTION_MODE, Constants.SELECTION_MODES.SINGLE_SELECTION.ordinal());
        startActivityForResult(i2,PICK_FILE_REQUEST);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_FILE_REQUEST && data!=null) {
            if (resultCode == RESULT_OK) {
                Uri path = data.getData();
                Toast.makeText(this,path.getPath(),Toast.LENGTH_LONG).show();
                Message message = new Message(AppUtils.getRandomId(), currentUser,friendUser, null);
                message.setUrl(path.getPath());
                sendMessage(message);

            }
        }

    }
}
