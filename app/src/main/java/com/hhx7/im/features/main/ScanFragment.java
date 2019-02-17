package com.hhx7.im.features.main;

import android.os.ParcelUuid;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hhx7.im.App;
import com.hhx7.im.Net.Address;
import com.hhx7.im.Net.Edge;
import com.hhx7.im.Net.EdgeManager;
import com.hhx7.im.Net.Message.MyMessage;
import com.hhx7.im.Net.MsgHandlerCenter;
import com.hhx7.im.data.fixtures.DialogsFixtures;
import com.hhx7.im.data.model.Dialog;
import com.hhx7.im.data.model.Message;
import com.hhx7.im.data.model.User;
import com.hhx7.im.data.model.User_;
import com.hhx7.im.features.demo.custom.layout.CustomLayoutMessagesActivity;
import com.hhx7.im.utils.AppUtils;
import com.squareup.picasso.Picasso;
import com.stfalcon.chatkit.commons.ImageLoader;
import com.stfalcon.chatkit.dialogs.DialogsList;
import com.stfalcon.chatkit.dialogs.DialogsListAdapter;
import com.stfalcon.chatkit.sample.R;


import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Created by pi on 17-9-29.
 */

public class ScanFragment extends Fragment
        implements DialogsListAdapter.OnDialogClickListener<Dialog>,
        DialogsListAdapter.OnDialogLongClickListener<Dialog>{


    public static String UPDATE_USER_ACTION = "com.hhx7.actions.UPDATE_USER";

    public static String ARGS_USER_ID = "User-Id";

    private ImageLoader imageLoader;
    private DialogsListAdapter<Dialog> dialogsAdapter;

    private DialogsList dialogsList;

    private App app;

    private LocalBroadcastManager localBroadcastManager;
    private Context context;
    private BluetoothAdapter mBluetoothAdapter=BluetoothAdapter.getDefaultAdapter();

    private Set<String> dialogs=new HashSet<>();



    private class DisplayDeviceAction implements Runnable{

        private BluetoothDevice device;
        DisplayDeviceAction(BluetoothDevice device){
            this.device=device;
        }
        public void run(){

            MyMessage message=new MyMessage();
            message.setUrl(MsgHandlerCenter.URL_GET_USER_INFO);
            Address to=new Address(Address.BLUETOOTH,device.getAddress());
            Address from=new Address(Address.BLUETOOTH,app.getCurrentUser().getBtAddr());
            message.setFrom(from);
            message.setTo(to);
            Intent getUserInfo=new Intent(EdgeManager.FORWARD_MESSAGE_ACTION);
            getUserInfo.putExtra(EdgeManager.ARGS_MESSAGE_NAME,message);
            localBroadcastManager.sendBroadcast(getUserInfo);

        }
    }
    // Create a BroadcastReceiver for ACTION_FOUND
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public  void onReceive(Context context, Intent intent) {

            String action = intent.getAction();
             // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {

                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                List<User> list=((App)context.getApplicationContext()).getUserBox().query().equal(User_.btAddr,device.getAddress()).build().find();
                User user=null;
                if(list.size()>0){
                    user=list.get(0);
                }

                if(user==null)
                    app.getThreadPoolExecutor().execute(new DisplayDeviceAction(device));
                else{
                    Dialog dialog=new Dialog(
                            DialogsFixtures.getRandomId(),
                            user.getName(),
                            user.getAvatar(),
                            user,
                            app.getDefaultMsg(),
                            0);
                    if(!dialogs.contains(device.getAddress())){
                        dialogsAdapter.addItem(dialog);
                        dialogs.add(device.getAddress());
                    }
                }
            }else if(BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)){
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if(device.getBondState()==BluetoothDevice.BOND_NONE){
                    Toast.makeText(context,"BOND CHANGE",Toast.LENGTH_SHORT);
                }
            }else if(action.equals(UPDATE_USER_ACTION)){
                Long userId=intent.getLongExtra(ARGS_USER_ID,-1);
                User user=app.getUserBox().query().equal(User_.id_,userId).build().findUnique();
                Dialog dialog=new Dialog(
                        DialogsFixtures.getRandomId(),
                        user.getName(),
                        user.getAvatar(),
                        user,
                        app.getDefaultMsg(),
                        0);
                if(!dialogs.contains(user.getBtAddr())){
                    dialogs.add(user.getBtAddr());
                    dialogsAdapter.addItem(dialog);
                }

            }

        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context=getActivity();

        app=(App)context.getApplicationContext();
        imageLoader = new ImageLoader() {
            @Override
            public void loadImage(ImageView imageView, String url) {
                if(!url.equals(""))
                Picasso.with(context).load(url).into(imageView);
            }
        };

        IntentFilter btFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        IntentFilter updateUserFilter = new IntentFilter(UPDATE_USER_ACTION);


        localBroadcastManager=LocalBroadcastManager.getInstance(context);
        context.registerReceiver(mReceiver, btFilter);
        localBroadcastManager.registerReceiver(mReceiver,updateUserFilter);

        Intent discoverableIntent = new
                Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
        startActivity(discoverableIntent);
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
        View v = inflater.inflate(R.layout.scan_fragment_layout, container, false);
        dialogsList = (DialogsList) v.findViewById(R.id.dialogsList);

        Button startFind=v.findViewById(R.id.StartFindDevices);

        startFind.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                scanDevices(view);
            }
        });

        Button stopFind=v.findViewById(R.id.StopFindDevices);

        stopFind.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                mBluetoothAdapter.cancelDiscovery();
            }
        });

        initAdapter();

        return v;
    }


    @Override
    public void onDialogClick(Dialog dialog) {


        BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
        //launch a session
        User user=dialog.users_.get(0);

        Address to=new Address(Address.BLUETOOTH,user.getBtAddr());
        CustomLayoutMessagesActivity.open(getActivity(),user.getBtAddr(),to);

    }

    private void initAdapter() {
        dialogsAdapter = new DialogsListAdapter<>(R.layout.item_custom_dialog, imageLoader);

        dialogsAdapter.setOnDialogClickListener(this);
        dialogsAdapter.setOnDialogLongClickListener(this);

        dialogsList.setAdapter(dialogsAdapter);
    }

    private boolean openBluetooth(){
        //open bluetooth
        if (mBluetoothAdapter == null) {
            // Device does not support Bluetooth
            Toast.makeText(context, "can't use bluetooth", Toast.LENGTH_LONG).show();
            return false;
        }else if (!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, 0);
            // Otherwise, setup the chat session
        }
        return true;
    }

    public void scanDevices(View view){
        //start finding device nearly
        //dialogsAdapter.clear();
        //dialogs.clear();
        if(openBluetooth())
           mBluetoothAdapter.startDiscovery();
        Log.i("zz","staring scanning");
    }






    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

}
