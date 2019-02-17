package com.hhx7.im.Net.Message;
import android.os.Environment;
import android.util.Log;

import com.hhx7.im.Net.Address;
import com.hhx7.im.Net.Edge;

import java.io.*;
import java.security.acl.LastOwnerException;

/**
 * Created by pi on 17-10-17.
 */


public  class MyMessage extends MessageHeader implements Serializable{


    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {

        this.url = url;
        set(URL,url);
    }

    private String url;
    private Address from;
    private Address to;

    private MessageBody messageBody;

    private Edge edge;

    public MyMessage(){}

    public void attachEdge(Edge edge){
        this.edge=edge;
        edge.lock();
    }

    public void deatchEdge(){
        if(edge!=null){
            edge.unlock();
            edge=null;
        }

    }
    public MyMessage(Address from,Address to){
        this.from=from;
        this.to=to;
    }

    public Address getFrom() {
        return from;
    }

    public void setFrom(Address from) {

        this.from = from;
    }

    public Address getTo() {
        return to;
    }

    public void setTo(Address to) {
        this.to = to;
    }

    public void setMessageBody(MessageBody messageBody) {
        if(messageBody!=null){
            setContentType(Integer.toString(messageBody.getDataType()));
            setContentLength(messageBody.getContentLen());
            this.messageBody = messageBody;
        }

    }

    public MessageBody getMessageBody() {
        return messageBody;
    }


    public final void read(InputStream is)throws IOException{

        super.read(is);
        url=get(URL);

        String fromZone=get("From-zone");
        String fromAddr=get("From-address");
        if(fromAddr!=null && fromZone!=null){
            from=new Address(Integer.parseInt(fromZone),fromAddr);
        }

        String toZone=get("To-zone");
        String toAddr=get("To-address");
        if(toAddr!=null && toZone!=null){
            to=new Address(Integer.parseInt(toZone),toAddr);
        }
        Log.i("Value","Header parsed completely");

        int dataType=MessageBody.EMPTY;
        if(!getContentType().equals(EMPTY)){
            dataType=Integer.parseInt(getContentType());
        }
        Long len=0L;
        if(hasContentLength()){
            len=getContentLength().longValue();
        }


        messageBody=new MessageBody(dataType,is,len);


    }

    public void write(OutputStream os)throws IOException{
        if(from!=null){
            set("From-zone",Integer.toString(from.getAddressZone()));
            set("From-address",from.getAddress());
        }
        if(to!=null){
            set("To-zone",Integer.toString(to.getAddressZone()));
            set("To-address",to.getAddress());
        }

        super.write(os);
        if(messageBody!=null)
            messageBody.write(os);

    }



    public void setContentLength(long length){
        if(length != UNKNOWN_CONTENT_LENGTH)
            set(CONTENT_LENGTH,Long.toString(length));
        else
            erase(CONTENT_LENGTH);
    }

    public Integer getContentLength(){
        String contentLength=get(CONTENT_LENGTH,EMPTY);
        if(!contentLength.isEmpty()){
            return Integer.parseInt(contentLength);
        }else
            return UNKNOWN_CONTENT_LENGTH;
    }

    public boolean hasContentLength(){
        return has(CONTENT_LENGTH);
    }

    public void setTransferEncoding(String transferEncoding){
        if(transferEncoding.equals(IDENTITY_TRANSFER_ENCODING))
            erase(TRANSFER_ENCODING);
        else
            set(TRANSFER_ENCODING,transferEncoding);
    }

    public String getTransferEncoding(){
        return get(TRANSFER_ENCODING,IDENTITY_TRANSFER_ENCODING);
    }

    public void setChunkedTransferEncoding(boolean flag){
        if(flag){
            setTransferEncoding(CHUNKED_TRANSFER_ENCODING);
        }else{
            setTransferEncoding(IDENTITY_TRANSFER_ENCODING);
        }
    }

    public boolean getChunkedTransferEncoding(){
        return getTransferEncoding().equals(CHUNKED_TRANSFER_ENCODING);
    }


    public void setContentType(String mediaType){
        if(mediaType.isEmpty())
            erase(CONTENT_TYPE);
        else
            set(CONTENT_TYPE,mediaType);
    }

    public void setContentType(MediaType mediaType){
       setContentType(mediaType.toString());
    }

    public String getContentType(){
        return get(CONTENT_TYPE,UNKNOWN_CONTENT_TYPE);

    }

    public void setKeepAlive(boolean keepAlive){
        if(keepAlive)
            set(CONNECTION,CONNECTION_KEEP_ALIVE);
        else
            set(CONNECTION,CONNECTION_CLOSE);
    }

    public boolean getKeepAlive(){
        String connection=get(CONNECTION,EMPTY);
        if(!connection.isEmpty())
            return !connection.equals(CONNECTION_CLOSE);
        else
            return true;
    }




    static final String HTTP_1_0= "HTTP/1.0";
    static final String HTTP_1_1="HTTP/1.1";

    static final String IDENTITY_TRANSFER_ENCODING= "identity";
    static final String CHUNKED_TRANSFER_ENCODING= "chunked";

    static final int    UNKNOWN_CONTENT_LENGTH= -1;
    static final String UNKNOWN_CONTENT_TYPE="";

    static final String CONTENT_LENGTH= "Content-Length";
    static final String CONTENT_TYPE= "Content-Type";
    static final String TRANSFER_ENCODING= "Transfer-Encoding";
    static final String CONNECTION= "Connection";

    static final String CONNECTION_KEEP_ALIVE= "Keep-Alive";
    static final String CONNECTION_CLOSE= "Close";

    static final String EMPTY="";

    static final String URL="Url";


}
