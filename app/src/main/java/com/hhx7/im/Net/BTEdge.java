package com.hhx7.im.Net;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothSocket;
import android.content.Context;

import com.hhx7.im.App;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class BTEdge extends Edge{


    private BluetoothSocket socket;

    BTEdge(Context context,Address address){
        super(context,address);
    }

    BTEdge(Context context,BluetoothSocket sock,Address address){
        super(context,address);
        this.socket=sock;
    }


    @Override
    public final boolean connect(){
        if(address==null){
            return false;
        }
        if(connected())
            return true;
        try{
            BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
            switch (address.getAddressZone()){
                case Address.BLUETOOTH:
                    socket= BluetoothAdapter.getDefaultAdapter().getRemoteDevice(address.getAddress()).createInsecureRfcommSocketToServiceRecord(App.MY_UUID);
                    socket.connect();

                    return connected();
                default:
                    return false;
            }
        }catch (IOException e){

            BluetoothAdapter.getDefaultAdapter().startDiscovery();
            return false;
        }


    }

    @Override
    public final InputStream getInputStream()throws IOException{

        return socket.getInputStream();
    }

    @Override
    public final OutputStream getOutputStream()throws IOException{
        return socket.getOutputStream();
    }

    @Override
    public final boolean connected(){

        return (socket!=null && socket.isConnected());
    }

    @Override
    public final void close(){
        try{
            socket.close();
        }catch (IOException e){

        }

    }
}
