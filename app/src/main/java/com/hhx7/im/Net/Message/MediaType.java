package com.hhx7.im.Net.Message;

import java.util.Iterator;
import java.util.Map;

public class MediaType {

    private String type;
    private String subType;
    private NameValueCollection parameters;

    public MediaType(String mediaType){
        parse(mediaType);
    }

    public MediaType(String type,String subType){
        this.type=type;
        this.subType=subType;
    }

    public MediaType(MediaType mediaType){
        this.type=mediaType.type;
        this.subType=mediaType.subType;
        this.parameters=mediaType.parameters;
    }
    public void setType(String type){
        this.type=type;
    }

    public String getType(){
        return type;
    }

    public void setSubType(String subType){
        this.subType=subType;
    }

    public String getSubType(){
        return subType;
    }

    public void setParameter(String name,String value){
        parameters.set(name,value);
    }

    public String getParameter(String name){
        String value=null;
        try{
            value=parameters.get(name);

        }catch(Exception e){
            value=null;
        }

        return value;
    }

    public boolean hasParameter(String name){
        return parameters.has(name);
    }

    public void removeParameter(String name){
        parameters.erase(name);
    }

    public NameValueCollection parameters(){
        return parameters;
    }

    /// Returns the string representation of the media type
    /// which is <type>/<subtype>{;<parameter>=<value>}
    public String toString(){
        StringBuilder result=new StringBuilder();
        result.append(type).append("/").append(subType);
        Iterator iter=parameters.begin();
        while(iter.hasNext()){

            @SuppressWarnings("unchecked")
            Map.Entry<String,String> entry=(Map.Entry<String,String>)iter.next();
            result.append("; ").append(entry.getKey()).append("=");
            MessageHeader.quote(entry.getValue(),result.substring(0),false);
        }
        return result.substring(0);
    }

    /// Returns true iff the type and subtype match
    /// the type and subtype of the given media type.
    /// Matching is case insensitive.
    public boolean matches(MediaType mediaType){
        return matches(mediaType.type,mediaType.subType);
    }

    /// Returns true iff the type and subtype match
    /// the given type and subtype.
    /// Matching is case insensitive.

    public boolean matches(String type, String subType){
        return (this.type.equals(type) && this.subType.equals(subType));
    }

    /// Returns true iff the type matches the given type.
    /// Matching is case insensitive.

    public boolean matches(String type){
        return this.type.equals(type);
    }

    /// Returns true if the type and subtype match
    /// the type and subtype of the given media type.
    /// If the MIME type is a range of types it matches
    /// any media type within the range (e.g. "image/*" matches
    /// any image media type, "*/*" matches anything).
    /// Matching is case insensitive.

    public boolean matchesRange(MediaType mediaType){
        return matchesRange(mediaType.type,mediaType.subType);
    }

    /// Returns true if the type and subtype match
    /// the given type and subtype.
    /// If the MIME type is a range of types it matches
    /// any media type within the range (e.g. "image/*" matches
    /// any image media type, "*/*" matches anything).
    /// Matching is case insensitive.
    public boolean matchesRange(String type, String subType){
        if(this.type.equals("*") || type.equals("*") || this.type.equals(type)){
            return this.subType.equals("*") || subType.equals("*") || this.subType.equals(subType);
        }else {
            return false;
        }
    }

    public boolean matchesRange(String type){
        return (this.type.equals("*") || type.equals("*") || matches(type));
    }
    protected void parse(String mediaType){
        type="";
        subType="";
        parameters.clear();
        int beg=0;
        int end=type.length();
        while( beg != end && Character.isSpaceChar(type.charAt(beg)))
            ++beg;
        while(beg != end && type.charAt(beg) != '/')
            this.type=this.type+type.charAt(beg++);
        if( beg!= end ) ++beg;
        while( beg != end && type.charAt(beg)!= ';' && Character.isSpaceChar(type.charAt(beg)))
            this.subType=this.subType+type.charAt(beg++);
        while(beg != end && type.charAt(beg)!= ';')
            ++beg;
        MessageHeader.splitParameters(mediaType,beg,end,parameters);
    }

}
