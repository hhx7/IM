package com.hhx7.im.Net;

import java.io.Serializable;

public class Address implements Serializable{

    public static final int EMPTY=0;
    public static final int BLUETOOTH=1;
    public static final int IPV6 =2;
    public static final int IPV4 =3;

    private int addressZone;
    private String address;


    public void setAddress(String address) {
        this.address = address;
    }


    public Address(){

    }

    public Address(int zone,String address){
        this.addressZone=zone;
        this.address=address;
    }

    public int getAddressZone() {
        return addressZone;
    }

    public void setAddressZone(int addressZone) {
        this.addressZone = addressZone;
    }

    public String getAddress() {
        return address;
    }

    @Override
    public int hashCode(){
        return (Integer.toString(addressZone)+"/"+address).hashCode();
    }

    @Override
    public boolean equals(Object o)
    {
        if(this == o)
        {
            return true;
        }
        if(!(o instanceof Address))
        {
            return false;
        }
        Address ne = (Address) o;
        return addressZone==ne.addressZone && address.equals(ne.address);
    }
}
