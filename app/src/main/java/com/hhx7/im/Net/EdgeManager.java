package com.hhx7.im.Net;


import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;


import com.hhx7.im.App;
import com.hhx7.im.Net.Message.MyMessage;
import com.hhx7.im.Net.listeners.BTListener;
import com.hhx7.im.Net.listeners.IPListener;


import java.io.IOException;
import java.util.*;


public class  EdgeManager extends Thread {

    public static String FORWARD_MESSAGE_ACTION = "com.hhx7.actions.FORWARD_MESSAGE";

    public static String ARGS_MESSAGE_NAME = "MESSAGE";

    private Context context;

    private Map<Address, Edge> edges;
    private Set<Address> failedDevices=new HashSet<>();

    private class FarwordMessageAction implements Runnable{

        Intent intent;
        public FarwordMessageAction(Intent intent){
            this.intent=intent;
        }
        @Override
        public synchronized void run(){
            MyMessage myMessage = (MyMessage) intent.getSerializableExtra(ARGS_MESSAGE_NAME);
            Address to = myMessage.getTo();
            if (!failedDevices.contains(to)) {
                try {
                    Edge edge = get(to);
                    if (edge != null && edge.connected()) {
                         if(!edge.sendMessage(myMessage)){
                            edge.sendMessage(myMessage);

                        }
                    }
                } catch (Exception e) {
                    Log.e("Write Message",e.getMessage());
                }
            }
        }
    }
    //receive intent and handle connection and session
    private class LocalReveiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action != null && action.equals(FORWARD_MESSAGE_ACTION)) {

                ((App)context).getThreadPoolExecutor().execute(new FarwordMessageAction(intent));
            }
        }
    }




    public EdgeManager(Context context) {

        this.context = context;
        edges = new HashMap<>();


        IntentFilter commands = new IntentFilter();
        commands.addAction(FORWARD_MESSAGE_ACTION);



        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(context); //获取实例
        localBroadcastManager.registerReceiver(new LocalReveiver(), commands); //注册广播监听器
    }


    @Override
    public void run() {
        try {
            new Thread(new BTListener(this)).start();
            new Thread(new IPListener(this)).start();
        } catch (IOException e) {

        }
    }

    public Context getContext() {
        return context;
    }

    //Add the specified edge and start it;
    public synchronized void start(Address id, Edge edge) {
        edges.put(id, edge);
        edge.attactEdgeManager(this);
        edge.start();
    }

    //Stop a specified session;
    public synchronized void close(Address id) {
       edges.remove(id);
    }

    //Stop all session
    public synchronized void closeAll() {
        Collection values = edges.values();
        Iterator iter = values.iterator();
        while (iter.hasNext()) {
            Edge edge = (Edge) iter.next();
            edge.close();
        }
        edges.clear();

    }

    public synchronized Edge get(Address address) {

        if (!existEdge(address)) {

            Edge edge = Edge.createEdge(context, address);
            if (edge != null && edge.connect())
                start(address, edge);
            else{
                failedDevices.add(address);
            }

        } else {
            Edge edge = edges.get(address);
            if (!edge.connect())
                edges.remove(address);

        }
        return edges.get(address);
    }

    private synchronized boolean existEdge(Address id) {

        return edges.containsKey(id);
    }


}
