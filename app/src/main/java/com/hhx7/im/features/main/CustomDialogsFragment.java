package com.hhx7.im.features.main;

import android.content.Context;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;

import com.hhx7.im.App;
import com.hhx7.im.data.fixtures.DialogsFixtures;
import com.hhx7.im.data.model.Dialog;

import com.hhx7.im.features.demo.custom.layout.CustomLayoutMessagesActivity;
import com.hhx7.im.utils.AppUtils;
import com.squareup.picasso.Picasso;
import com.stfalcon.chatkit.commons.ImageLoader;
import com.stfalcon.chatkit.dialogs.DialogsList;
import com.stfalcon.chatkit.dialogs.DialogsListAdapter;
import com.stfalcon.chatkit.sample.R;

public class CustomDialogsFragment extends Fragment
        implements DialogsListAdapter.OnDialogClickListener<Dialog>,
        DialogsListAdapter.OnDialogLongClickListener<Dialog>{



    private ImageLoader imageLoader;
    private DialogsListAdapter<Dialog> dialogsAdapter;
    private Context context;
    private DialogsList dialogsList;

    private App app;



    public static CustomDialogsFragment newInstance(){
        CustomDialogsFragment ff=new CustomDialogsFragment();
        //Supply the construction argument for this fragment

        return(ff);
    }



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context=getActivity();
        imageLoader = new ImageLoader() {
            @Override
            public void loadImage(ImageView imageView, String url) {
                Picasso.with(context).load(url).into(imageView);
            }
        };
    }

    @Override
    public void onDialogLongClick(Dialog dialog) {
        AppUtils.showToast(
                context,
                dialog.getDialogName(),
                false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_custom_layout_dialogs, container, false);
        dialogsList = (DialogsList) v.findViewById(R.id.dialogsList);
        initAdapter();

        return v;
    }


    @Override
    public void onDialogClick(Dialog dialog) {
        CustomLayoutMessagesActivity.open(getActivity(),dialog.getDialogName());
    }

    private void initAdapter() {
        dialogsAdapter = new DialogsListAdapter<>(R.layout.item_custom_dialog, imageLoader);


        dialogsAdapter.setItems(((App)getActivity().getApplication()).getDialogs());

        dialogsAdapter.setOnDialogClickListener(this);
        dialogsAdapter.setOnDialogLongClickListener(this);

        dialogsList.setAdapter(dialogsAdapter);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

}
