package com.hhx7.im.Net.Message;


import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

//A collection of name-value pairs that are used in
//various internet protocols
//
//The name is case-insensitive.
//
//THere can be more than one name-value pair with the
//same name
public class NameValueCollection {

    private Map<String,String> map=new HashMap<>();



    public void set(String name,String value){

       map.put(name,value);
    }

    public void add(String name,String value){
        map.put(name,value);
    }

    public String get(String  name)
    {
        if(map.containsKey(name)){
            return map.get(name);
        }
        return null;
    }
    /// Returns the value of the first name-value pair with the given name.
    /// If no value with the given name has been found, the defaultValue is returned.
    public String get(String name,String defaultValue){

        if(map.containsKey(name)){
            return map.get(name);
        }
        return defaultValue;
    }


    /// Returns true if there is at least one name-value pair
    /// with the given name.
    public boolean has(String name){

        return map.containsKey(name);
    }

    /// Returns an iterator pointing to the first name-value pair
    /// with the given name.
    public Iterator find(String name){
        Iterator iter=map.keySet().iterator();
        while(iter.hasNext()){
            if(iter.next().equals(name))
                return iter;
        }
        return iter;
    }

    /// Returns an iterator pointing to the begin of
    /// the name-value pair collection.
    public Iterator begin(){

        return map.entrySet().iterator();
    }


    /// Returns true iff the header does not have any content.

    public boolean empty(){
        return map.isEmpty();
    }

    /// Returns the number of name-value pairs in the
    /// collection.
    public long size(){
        return map.size();
    }

    /// Removes all name-value pairs with the given name.
    public void erase(String name){

        map.remove(name);
    }

    /// Removes all name-value pairs and their values.
    public void clear(){

        map.clear();
    }



}
