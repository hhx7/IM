package com.hhx7.im.Net.Message;

import android.util.Log;


import java.io.*;
import java.nio.charset.Charset;

public class MessageBody {

    public static final int EMPTY=0;
    public static final int TEXT=1;
    public static final int FILE=2;

    private int dataType;

    private Long contentLen;
    private InputStream mBodyIs;


    public static MessageBody createMsgBody(int dataType,String data){



        InputStream mBodyIs=null;
        Long contentLen=0L;

        if(dataType==TEXT){

            byte[] buf=data.getBytes(Charset.defaultCharset());


            contentLen=new Long(buf.length);
            mBodyIs=new ByteArrayInputStream(buf);



        }else if(dataType==FILE){
            File file=new File(data);
            if(!file.exists()){
                return null;
            }else {
                try{
                    mBodyIs=new FileInputStream(file);
                    contentLen=file.length();
                }catch (IOException e){
                    return null;
                }
            }
        }

        return new MessageBody(dataType,mBodyIs,contentLen);
    }



    MessageBody(int dataType,InputStream is,Long contentLen){
        this.dataType=dataType;

        if(is==null || contentLen==0){
            this.dataType=EMPTY;
        }else {
            mBodyIs=is;

            this.contentLen=contentLen;
        }

    }


    public int getDataType() {
        return dataType;
    }

    public Long getContentLen() {
        return contentLen;
    }

    public void write(OutputStream os)throws IOException{
        Log.i("SentSize",contentLen.toString());

        for(int i=0;i<contentLen;++i){
            os.write(mBodyIs.read());
        }


        mBodyIs.close();
    }



    public String toStr(){

        Log.i("ReceiveSize1",contentLen.toString());
        if(contentLen>Integer.MAX_VALUE){
            return null;
        }

        byte[] data=new byte[contentLen.intValue()];
        try{


            int hasRead=0;
            while (hasRead<contentLen){
                int readSize=mBodyIs.read(data,hasRead,contentLen.intValue()-hasRead);
                hasRead+=readSize;
            }




            return new String(data, Charset.defaultCharset());
        }catch (IOException e){
            return null;
        }
    }

    public boolean toFile(File file){
        if(file==null || file.exists())
            return false;
        try{
            if(file.createNewFile()){
                try(BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file))){

                    for(int i=0;i<contentLen;++i){
                        bos.write(mBodyIs.read());
                    }
                }

                return true;
            }
        }catch (IOException e){
            return false;
        }
        return false;


    }


    public void setDataType(int dataType){
        this.dataType=dataType;
    }
}
