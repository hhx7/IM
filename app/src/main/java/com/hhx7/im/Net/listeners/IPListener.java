package com.hhx7.im.Net.listeners;



import com.hhx7.im.Net.Address;
import com.hhx7.im.Net.Edge;
import com.hhx7.im.Net.EdgeManager;
import com.hhx7.im.Net.Util;

import java.io.IOException;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Enumeration;


public class IPListener implements Runnable{

    private ServerSocket serverSocket;

    private EdgeManager manager;

    public IPListener(EdgeManager manager)throws IOException {
        this.manager=manager;
        serverSocket=new ServerSocket(9000);

    }

    @Override
    public void run(){

        while(true) {
            try{
                Socket temp=serverSocket.accept();


                Address address=new Address(Address.IPV6,temp.getRemoteSocketAddress().toString());
                manager.start(address, Edge.createEdge(manager.getContext(),temp,address));

            }catch (IOException e){

            }

        }

    }

}
