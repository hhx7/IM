package com.hhx7.im.features.demo.custom.holder;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import com.hhx7.im.data.fixtures.MessagesFixtures;
import com.hhx7.im.data.model.Message;
import com.hhx7.im.features.demo.DemoMessagesActivity;
import com.hhx7.im.features.demo.custom.holder.holders.messages.CustomIncomingImageMessageViewHolder;
import com.hhx7.im.features.demo.custom.holder.holders.messages.CustomIncomingTextMessageViewHolder;
import com.hhx7.im.features.demo.custom.holder.holders.messages.CustomOutcomingImageMessageViewHolder;
import com.hhx7.im.features.demo.custom.holder.holders.messages.CustomOutcomingTextMessageViewHolder;
import com.hhx7.im.features.demo.custom.layout.holders.IncomingFileMessageViewHolder;
import com.hhx7.im.features.demo.custom.layout.holders.OutcomingFileMessageViewHolder;
import com.hhx7.im.utils.AppUtils;
import com.stfalcon.chatkit.messages.MessageHolders;
import com.stfalcon.chatkit.messages.MessageInput;
import com.stfalcon.chatkit.messages.MessagesList;
import com.stfalcon.chatkit.messages.MessagesListAdapter;
import com.stfalcon.chatkit.sample.R;

public class CustomHolderMessagesActivity extends DemoMessagesActivity
        implements MessagesListAdapter.OnMessageLongClickListener<Message>,
        MessageInput.InputListener,
        MessageInput.AttachmentsListener,
        MessageHolders.ContentChecker<Message>{

    private static final byte CONTENT_TYPE_VOICE = 1;
    
    public static void open(Context context) {
        context.startActivity(new Intent(context, CustomHolderMessagesActivity.class));
    }

    private MessagesList messagesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_holder_messages);

        messagesList = (MessagesList) findViewById(R.id.messagesList);
        initAdapter();

        MessageInput input = (MessageInput) findViewById(R.id.input);
        input.setInputListener(this);
        input.setAttachmentsListener(this);
    }

    @Override
    public boolean onSubmit(CharSequence input) {
        messagesAdapter.addToStart(
                MessagesFixtures.getTextMessage(input.toString()), true);
        return true;
    }

    @Override
    public void onAddAttachments() {
        messagesAdapter.addToStart(MessagesFixtures.getImageMessage(), true);
    }

    @Override
    public void onMessageLongClick(Message message) {
        AppUtils.showToast(this, R.string.on_log_click_message, false);
    }


    @Override
    public boolean hasContentFor(Message message, byte type) {
        switch (type) {
            case CONTENT_TYPE_VOICE:
                return message.getVoice() != null
                        && message.getVoice().getUrl() != null
                        && !message.getVoice().getUrl().isEmpty();
        }
        return false;
    }
    
    private void initAdapter() {
        MessageHolders holdersConfig = new MessageHolders()
                .setIncomingTextConfig(
                        CustomIncomingTextMessageViewHolder.class,
                        R.layout.item_custom_incoming_text_message)
                .setOutcomingTextConfig(
                        CustomOutcomingTextMessageViewHolder.class,
                        R.layout.item_custom_outcoming_text_message)
                .setIncomingImageConfig(
                        CustomIncomingImageMessageViewHolder.class,
                        R.layout.item_custom_incoming_image_message)
                .setOutcomingImageConfig(
                        CustomOutcomingImageMessageViewHolder.class,
                        R.layout.item_custom_outcoming_image_message)
                .registerContentType(
                        CONTENT_TYPE_VOICE,
                        IncomingFileMessageViewHolder.class,
                        R.layout.item_custom_incoming_file_message,
                        OutcomingFileMessageViewHolder.class,
                        R.layout.item_custom_outcoming_file_message,
                        this);;

        super.messagesAdapter = new MessagesListAdapter<>(super.senderId, holdersConfig, super.imageLoader);
        super.messagesAdapter.setOnMessageLongClickListener(this);
        super.messagesAdapter.setLoadMoreListener(this);
        messagesList.setAdapter(super.messagesAdapter);
    }
}
