package com.hhx7.im;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;

import android.bluetooth.BluetoothServerSocket;

import android.content.Intent;
import android.os.*;


import com.hhx7.im.Net.EdgeManager;
import com.hhx7.im.Net.MsgHandlerCenter;

import java.io.*;

import java.util.UUID;


/**
 * Created by pi on 17-9-21.
 */

public class Node extends Service {

    @Override
    public void onCreate(){
        EdgeManager edgeManager=new EdgeManager(getApplication());
        MsgHandlerCenter msgHandlerCenter=new MsgHandlerCenter(getApplication());

        msgHandlerCenter.start();
        edgeManager.start();


    }
    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }




}
