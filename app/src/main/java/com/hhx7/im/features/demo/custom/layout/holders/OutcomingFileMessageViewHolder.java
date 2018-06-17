package com.hhx7.im.features.demo.custom.layout.holders;

import android.view.View;
import android.widget.TextView;
import com.hhx7.im.data.model.Message;
import com.hhx7.im.utils.FormatUtils;
import com.stfalcon.chatkit.messages.MessageHolders;
import com.stfalcon.chatkit.sample.R;
import com.stfalcon.chatkit.utils.DateFormatter;

/*
 * Created by troy379 on 05.04.17.
 */
public class OutcomingFileMessageViewHolder
        extends MessageHolders.OutcomingTextMessageViewHolder<Message> {

    private TextView filename;
    private TextView tvTime;

    public OutcomingFileMessageViewHolder(View itemView) {
        super(itemView);
        filename = (TextView) itemView.findViewById(R.id.file_msg_filename);
        tvTime=(TextView)itemView.findViewById(R.id.time);
    }

    @Override
    public void onBind(Message message) {
        super.onBind(message);
        filename.setText(message.getUrl());
        tvTime.setText(DateFormatter.format(message.getCreatedAt(), DateFormatter.Template.TIME));
    }
}
