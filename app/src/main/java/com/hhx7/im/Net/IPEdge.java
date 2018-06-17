package com.hhx7.im.Net;


import android.content.Context;
import android.util.Log;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Inet4Address;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

public class IPEdge extends Edge{

    private Socket socket;

    IPEdge(Context context, Address address){
        super(context,address);

    }

    IPEdge(Context context, Socket sock, Address address){
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

            String mAddress=address.getAddress();
            int pos_start=mAddress.indexOf('/')+1;

            int pos_mid=mAddress.lastIndexOf(':');
            String host=mAddress.substring(pos_start,pos_mid);
            int port=Integer.parseInt(mAddress.substring(pos_mid+1));
            SocketAddress saddress=null;
            if(address.getAddressZone()==Address.IPV6){//build socket and connect


                saddress=new InetSocketAddress(InetAddress.getByName(host),port);


            }else if(address.getAddressZone()==Address.IPV4){
                saddress=new InetSocketAddress(Inet4Address.getByAddress(host.getBytes()),port);
            }

            socket=new Socket(host,port);

            return connected();
        }catch (IOException e){
            Log.e("IPV6",e.getMessage());
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
