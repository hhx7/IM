package com.hhx7.im.Net.Message;

import android.util.Log;

import java.io.*;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

/// MessageHeader supports writing and reading the
/// header data in RFC 2822 format.
///
/// The maximum number of fields can be restricted
/// by calling setFieldLimit(). This is useful to
/// defend against certain kinds of denial-of-service
/// attacks. The limit is only enforced when parsing
/// header fields from a stream, not when programmatically
/// adding them. The default limit is 100.
public class MessageHeader extends NameValueCollection {

    private enum Limits{
        MAX_NAME_LENGTH(256) ,
        MAX_VALUELENGTH(8192),
        DFL_FIELD_LIMIT(100);


        private final long value;
        Limits(int value) {
            this.value = value;
        }

        public long getValue() {
            return value;
        }
    }
    private int fieldLimit;

    static final int eof=-1;
    /// Writes the message header to the given output stream.
    ///
    /// The format is one name-value pair per line, with
    /// name and value separated by a colon and lines
    /// delimited by a carriage return and a linefeed
    /// character. See RFC 2822 for details.
    public void write(OutputStream ostr)throws IOException{
        Iterator iter=begin();
        while(iter.hasNext()){
            Map.Entry<String,String> entry=(Map.Entry<String,String>) iter.next();

            String res=entry.getKey()+":"+entry.getValue()+"\r\n";
            ostr.write(res.getBytes(),0,res.length());

        }
        if(size()>0){
            String end="\r\n";
            ostr.write(end.getBytes());
        }

    }

    /// Reads the message header from the given input stream.
    ///
    /// See write() for the expected format.
    /// Also supported is folding of field content, according
    /// to section 2.2.3 of RFC 2822.
    ///
    /// Reading stops at the first empty line (a line only
    /// containing \r\n or \n), as well as at the end of
    /// the stream.
    ///
    /// Some basic sanity checking of the input stream is
    /// performed.
    ///
    /// Throws a MessageException if the input stream is
    /// malformed.

    public void read(InputStream istr)throws IOException{



        StringBuilder name,value;
        InputStream bis=istr;
        int ch=bis.read();

        if(ch==eof)
            throw new IOException("NO MESSAGE EXCEPTION");
        int fieldsCount=0;
        while(ch !=eof && ch !='\r' && ch != '\n'){
            if(fieldLimit>0 && fieldsCount==fieldLimit){
                throw new IOException("Too much header fields");
            }
            name=new StringBuilder("");
            value=new StringBuilder("");

            while(ch != eof && ch!=':' && ch!='\n' && name.length() <Limits.MAX_NAME_LENGTH.getValue()){
                name.append((char)ch);
                ch=bis.read();
            }

            // ignore invalid header lines
            if(ch== '\n'){
                ch=bis.read();
                if(ch==eof){
                    throw new IOException("NO MESSAGE EXCEPTION");
                }
                continue;
            }

            if(ch != ':'){
                throw new IOException("Field name too long/no colon found");
            }
            ch=bis.read();
            if(ch==eof){
                throw new IOException("NO MESSAGE EXCEPTION");
            }

            while (Character.isSpaceChar(ch)||ch=='\r'){
                ch=bis.read();
                if(ch==eof){
                    throw new IOException("NO MESSAGE EXCEPTION");
                }
            }

            while ( ch != eof && ch != '\r' && ch !='\n' && value.length()<Limits.MAX_VALUELENGTH.getValue()){
                value.append((char)ch);
                ch=bis.read();
                if(ch==eof){
                    throw new IOException("NO MESSAGE EXCEPTION");
                }
            }

            if(ch =='\r') {
                ch=bis.read();
                if(ch==eof){
                    throw new IOException("NO MESSAGE EXCEPTION");
                }
            }else{
                throw new IOException("Message Format error");
            }
            if(ch == '\n') {
                ch=bis.read();
                if(ch==eof){
                    throw new IOException("NO MESSAGE EXCEPTION");
                }
            }else {
                throw new IOException("Message Format error");
            }



            String n=new String(name.substring(0).getBytes(),"UTF8");
            add(name.substring(0),value.substring(0));
            Log.i("Header",n);

            ++fieldsCount;

            if(ch == '\r') {
                ch=bis.read();
                if(ch==eof){
                    throw new IOException("NO MESSAGE EXCEPTION");
                }
                if(ch=='\n'){
                    return;
                }
            }

        }

    }

    /// Returns the maximum number of header fields
    /// allowed.
    public int getFieldLimit(){
        return fieldLimit;
    }


    /// Sets the maximum number of header fields
    /// allowed.
    public void setFieldLimit(int limit){
        assert (limit>=0);
        fieldLimit=limit;
    }

    /// Returns true iff the field with the given fieldName contains
    /// the given token. Tokens in a header field are expected to be
    /// comma-separated and are case insensitive.
    boolean hasToken(String fieldName,String token){
        String field=get(fieldName,null);
        Vector<String> tokens=new Vector<>();
        splitElements(field,tokens,true);
        Iterator iter=tokens.iterator();
        while (iter.hasNext()){
            String tok=(String)iter.next();
            if(tok.equals(token))
                return true;
        }

        return false;
    }

    /// Splits the given string into separate elements. Elements are expected
    /// to be separated by commas.
    ///
    /// For example, the string
    ///   text/plain; q=0.5, text/html, text/x-dvi; q=0.8
    /// is split into the elements
    ///   text/plain; q=0.5
    ///   text/html
    ///   text/x-dvi; q=0.8
    ///
    /// Commas enclosed in double quotes do not split elements.
    ///
    /// If ignoreEmpty is true, empty elements are not returned.
    public static void splitElements(String s, Vector<String> elements,Boolean ignoreEmpty){
        elements.clear();
        StringBuilder elem=new StringBuilder();
        int i=0;
        while(i!=s.length()){

            if(s.charAt(i)=='"'){
                elem.append(s.charAt(i++));

                while(i != s.length() && s.charAt(i) != '"'){
                    if(s.charAt(i) == '\\'){
                        ++i;
                        if(i != s.length())
                            elem.append(s.charAt(i++));
                    }else{
                        elem.append(s.charAt(i++));
                    }
                }
                if(i != s.length()) elem.append(s.charAt(i++));
            }else if(s.charAt(i) == '\\'){
                ++i;
                if(i != s.length()) elem.append(s.charAt(i++));
            }else if(s.charAt(i) == ','){
                if(!ignoreEmpty || elem.length()>0){
                    elements.add(elem.substring(0));
                }
                elem.delete(0,elem.length());
                ++i;
            }else{
                elem.append(s.charAt(i++));
            }
        }
        if(elem.length()>0){
            if(!ignoreEmpty || elem.length()>0){
                elements.add(elem.substring(0));
            }
        }
    }

    /// Splits the given string into a value and a collection of parameters.
    /// Parameters are expected to be separated by semicolons.
    ///
    /// Enclosing quotes of parameter values are removed.
    ///
    /// For example, the string
    ///   multipart/mixed; boundary="MIME_boundary_01234567"
    /// is split into the value
    ///   multipart/mixed
    /// and the parameter
    ///   boundary -> MIME_boundary_01234567
    public static void splitParameters(String s,String value,NameValueCollection parameters){
        StringBuilder val=new StringBuilder();
        parameters.clear();
        int beg=0,end=s.length();
        while (beg != end && Character.isSpaceChar(s.charAt(beg++)));
        while(beg != end && s.charAt(beg) !=';')
            val.append(s.charAt(beg++));
        if(beg != end) ++beg;
        splitParameters(s,beg,end,parameters);
      
    }


    /// Splits the given string into a collection of parameters.
    /// Parameters are expected to be separated by semicolons.
    ///
    /// Enclosing quotes of parameter values are removed.

    public static void splitParameters(String s,int beg,int end,NameValueCollection parameters){

        while(beg != end){
            StringBuilder pname=new StringBuilder();
            StringBuilder pvalue=new StringBuilder();

            while( beg != end && Character.isSpaceChar(s.charAt(beg)))
                ++beg;
            while(beg != end && s.charAt(beg)!= '=' && s.charAt(beg) != ';')
                pname.append(s.charAt(beg++));

            if(beg != end && s.charAt(beg)!=';')
                ++beg;
            while (beg != end && Character.isSpaceChar(s.charAt(beg)))
                ++beg;
            while(beg !=end && s.charAt(beg) != ';'){
                if(s.charAt(beg)=='"'){
                    ++beg;
                    while( beg != end && s.charAt(beg) != '"'){
                        if(s.charAt(beg)=='\\'){
                            ++beg;
                            if(beg != end)
                                pvalue.append(s.charAt(beg++));
                        }else
                            pvalue.append(s.charAt(beg++));
                    }
                    if(beg != end) ++beg;
                }else if(s.charAt(beg) == '\\'){
                    ++beg;
                    if(beg != end)
                        pvalue.append(s.charAt(beg++));
                }else
                    pvalue.append(s.charAt(beg++));
            }
            if(pname.length()>0)
                parameters.add(pname.substring(0),pvalue.substring(0));
            if(beg != end) ++beg;
        }
    }

    /// Checks if the value must be quoted. If so, the value is
    /// appended to result, enclosed in double-quotes.
    /// Otherwise, the value is appended to result as-is.
    static void quote(String value, String result,boolean allowSpace){
        boolean mustQuote = false;
        int beg=0;
        int end=value.length();
        for(;!mustQuote && beg != end ;++beg){
            if(!Character.isSpaceChar(value.charAt(beg)) && value.charAt(beg)!='.' && value.charAt(beg)!='_'
                    && value.charAt(beg)!='-' && !(Character.isSpaceChar(value.charAt(beg)) && allowSpace))
                mustQuote=true;
            if(mustQuote) result=result+'"';
            result=result+value;
            if(mustQuote) result=result+'"';
        }
    }


}
