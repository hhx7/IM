package com.hhx7.im.Net;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.hhx7.im.Net.Message.MyMessage;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;


public abstract class Edge extends Thread{


    private static final int eof=-1;
    private static final int BUFFER_SIZE=2048;

    protected Address address;
    private LocalBroadcastManager localBroadcastManager;
    private byte[] pBuffer=new byte[BUFFER_SIZE];
    private int pCurrent;
    private int pEnd;
    private boolean keepAlive;
    protected Context context;
    private ReentrantLock lock=new ReentrantLock();
    private final Condition condition=lock.newCondition();
    private AtomicBoolean attachedMyMsg=new AtomicBoolean(false);

    private EdgeManager manager;



    public static Edge createEdge(Context context,Address address){
        switch (address.getAddressZone()){
            case Address.BLUETOOTH:
                return new BTEdge(context,address);
                case Address.IPV6:
                    return new IPEdge(context,address);
                default:
                    return null;
        }
    }




    public static Edge createEdge(Context context, BluetoothSocket socket,Address address){
        switch (address.getAddressZone()){
            case Address.BLUETOOTH:
                return new BTEdge(context,socket,address);
            default:
                return null;
        }
    }

    public static Edge createEdge(Context context, Socket socket, Address address){
        switch (address.getAddressZone()){
            case Address.BLUETOOTH:
                return new IPEdge(context,socket,address);
            default:
                return null;
        }
    }

    private void init(Context context,Address address,boolean keepAlive){
        this.context=context;
        pCurrent=0;
        pEnd=0;
        this.keepAlive=keepAlive;
        this.address=address;

        localBroadcastManager=LocalBroadcastManager.getInstance(context);
    }

    Edge(Context context,Address address){
        init(context,address,false);

    }


    protected Edge(Context context,Address address, boolean keepAlive){
        init(context,address,keepAlive);
    }

    public abstract InputStream getInputStream()throws IOException;
    public abstract OutputStream getOutputStream()throws IOException;

    public abstract void close();

    public void attactEdgeManager(EdgeManager edgeManager){
        manager=edgeManager;
    }

    public synchronized void lock(){
        attachedMyMsg.set(true);
    }

    public  void unlock(){

        lock.lock();
        try{
            attachedMyMsg.set(false);
            condition.signalAll();
        }finally {
            lock.unlock();
        }



    }

    public abstract boolean connected();


    public void setKeepAlive(boolean keepAlive){
        this.keepAlive=keepAlive;
    }

    public boolean getKeepAlive(){
        return keepAlive;
    }

    public void drainBuffer(byte[] buffer){
        int i=0;
        while(pCurrent<pEnd){
            buffer[i++]=this.pBuffer[pCurrent++];
        }
    }



    /// Connects the underlying socket to the given address
    /// and sets the socket's receive timeout.
    protected boolean connect(Address address)throws IOException{

        if(address==null)
            return false;
        this.address=address;
        return connect();
    }

    public abstract boolean connect();

    /// Sends the header for the given HTTP request to
    /// the server.
    ///
    /// The returned output stream can be used to write
    /// the request body. The stream is valid until
    /// receiveResponse() is called or the session
    /// is destroyed.
    public synchronized boolean sendMessage(MyMessage myMessage){

        if(connected()){

            try{

                OutputStream os=getOutputStream();
                if(myMessage!=null && os !=null){
                    myMessage.write(os);
                    Log.i("zz","send message from "+myMessage.getFrom().getAddress()+"->"+myMessage.getTo().getAddress());
                    return true;
                }
            }catch (IOException e){
                Log.e("Write Message",e.getMessage());
                if(manager!=null)
                    manager.close(address);
                return false;
            }

        }
        return false;

    }



    @Override
    public void run(){
        while (true){
            if(!connected()){
                connect();
            }
            InputStream is;
            try{
                is=getInputStream();
            }catch (Exception e){
                Log.e("zz",e.getMessage());
                break;
            }
            lock.lock();
            try{
                while (attachedMyMsg.get())
                    condition.await();
            }catch (InterruptedException e){

            }finally {
                lock.unlock();
            }

            try{

                MyMessage myMessage=new MyMessage();
                myMessage.read(is);
                myMessage.attachEdge(this);
                myMessage.setFrom(address);
                Log.i("zz","receive message from "+myMessage.getFrom().getAddress()+"->"+myMessage.getTo().getAddress());
                Intent intent=new Intent(MsgHandlerCenter.HANDLE_MY_MESSAGE_ACTION);
                intent.putExtra(MsgHandlerCenter.ARGS_MESSAGE_NAME,myMessage);
                localBroadcastManager.sendBroadcast(intent);
            }catch (IOException e){
                Log.e("zz",e.getMessage());
                if(manager!=null)
                    manager.close(address);
                return;
            }


        }
    }

    /// Returns the next byte in the buffer.
    /// Reads more data from the socket if there are
    /// no bytes left in the buffer.
    protected int get()throws Exception{
        if(pCurrent == pEnd)
            refill();
        if(pCurrent < pEnd)
            return pBuffer[pCurrent++];
        else
            return eof;
    }

    /// Peeks at the next character in the buffer.
    /// Reads more data from the socket if there are
    /// no bytes left in the buffer.
    protected int peek()throws Exception{
        if(pCurrent == pEnd)
            refill();
        if(pCurrent < pEnd)
            return pBuffer[pCurrent];
        else
            return eof;
    }


    /// Reads up to length bytes.
    ///
    /// If there is data in the buffer, this data
    /// is returned. Otherwise, data is read from
    /// the socket to avoid unnecessary buffering.
    protected int read(byte[] buffer,int length)throws Exception{
        if(pCurrent< pEnd){
            int n=pEnd-pCurrent;
            if(n>length) n=length;
            for(int i=0;i<n;++i){
                buffer[i]=pBuffer[pCurrent++];
            }
            return n;
        }else
            return receive(buffer,length);
    }


    /// Writes data to the socket.
    protected int write(byte[] buffer,int length)throws Exception{
        try{
            OutputStream os=getOutputStream();
            os.write(buffer,0,length);
            return length;

        }catch(IOException e){

            throw e;
        }

    }

    /// Reads up to length bytes.
    protected int receive(byte[] buffer,int length)throws Exception{
        try{
            InputStream is=getInputStream();
            return is.read(buffer,0,length);

        }catch(IOException e){
            throw e;
        }
    }

    /// Returns the number of bytes in the buffer.
    public int buffered(){
        return pEnd-pCurrent;
    }

    /// Refills the internal buffer.
    private void refill()throws Exception{
        pCurrent=pEnd=0;
        int n=receive(pBuffer,BUFFER_SIZE);
        pEnd+=n;
    }


}
