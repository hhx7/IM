package com.hhx7.im.Net.listeners;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import com.hhx7.im.App;
import com.hhx7.im.Net.Address;
import com.hhx7.im.Net.Edge;
import com.hhx7.im.Net.EdgeManager;

import java.io.IOException;

public class BTListener implements Runnable{

    private BluetoothServerSocket serverSocket;

    private EdgeManager manager;

    public BTListener(EdgeManager manager)throws IOException{
        this.manager=manager;
        BluetoothAdapter mBluetoothAdapter=BluetoothAdapter.getDefaultAdapter();
        this.serverSocket=mBluetoothAdapter.listenUsingInsecureRfcommWithServiceRecord("Insecure", App.MY_UUID);
    }

    @Override
    public void run(){

        while(true) {
            try{
                BluetoothSocket temp=serverSocket.accept();
                BluetoothDevice device=temp.getRemoteDevice();

                Address address=new Address(Address.BLUETOOTH,device.getAddress());
                manager.start(address, Edge.createEdge(manager.getContext(),temp,address));

            }catch (IOException e){
                Log.e("ZZ",e.getMessage());
            }

        }

    }

}
