package com.cbi.coollink.net.protocol;


import com.cbi.coollink.Main;

import java.beans.ConstructorProperties;
import java.lang.annotation.Documented;
import java.util.Random;

public class Mac {

    private int[] mac;

@ConstructorProperties({"0x00-0xFF"})
    public Mac(int deviceType){
        mac = new int[3];
        setMac(generateMac(deviceType));
    }

    public int[] getMac() {
        return mac;
    }

    private int[] generateMac(int deviceType){
        if (deviceType>0xFF||deviceType<0x00)
        {
            throw new RuntimeException("INVALID MAC ADDRESS, ADDRESSES MUST BE BETWEEN 0x00-0xFF");
        }
        int [] address = new int[3];
        address[0]=deviceType;
        boolean DNE = false;
        do {
            address[1] = (int) Math.round(Math.random() * 255);
            address[2] = (int) Math.round(Math.random() * 255);
            DNE = Main.macDNE(address);
        }while(!DNE);
        Main.setKnownMacs(address);
        return address;
    }

    private void setMac(int[] mac){
        this.mac=mac;
    }

    @Override
    public String toString() {
    String ab=Integer.toHexString(mac[0]);//takes the hex value of mac set 1 as a string
    String cd=Integer.toHexString(mac[1]);//takes the hex value of mac set 2 as a string
    String ef=Integer.toHexString(mac[2]);//takes the hex value of mac set 3 as a string
    if(ab.length()==1){ab="0"+ab;}//adds on a leading 0 if the number is 1 character long
    if(cd.length()==1){cd="0"+cd;}//adds on a leading 0 if the number is 1 character long
    if(ef.length()==1){ef="0"+ef;}//adds on a leading 0 if the number is 1 character long
    return ab+":"+cd+":"+ef;
    }
}
