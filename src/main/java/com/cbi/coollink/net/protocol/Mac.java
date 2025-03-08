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

    public Mac(byte[] bytes){
        mac = new int[3];
        //if the passed in array is empty then something went wrong in the transfer process so to avoid error just generate a new mac
        if(bytes.length == 0){
            setMac(generateMac(1));
            return;
        }

        //surprisingly the bit mask at the end is necessary to ensure nothing funky happens with the other bits
        mac[0] |= (bytes[0] << 24) & 0xFF000000;
        mac[0] |= (bytes[1] << 16) & 0x00FF0000;
        mac[0] |= (bytes[2] << 8) & 0x0000FF00;
        mac[0] |= (bytes[3]) & 0x000000FF;
        mac[1] |= (bytes[4] << 24) & 0xFF000000;
        mac[1] |= (bytes[5] << 16) & 0x00FF0000;
        mac[1] |= (bytes[6] << 8) & 0x0000FF00;
        mac[1] |= (bytes[7]) & 0x000000FF;
        mac[2] |= (bytes[8] << 24) & 0xFF000000;
        mac[2] |= (bytes[9] << 16) & 0x00FF0000;
        mac[2] |= (bytes[10] << 8) & 0x0000FF00;
        mac[2] |= (bytes[11]) & 0x000000FF;
    }

    public int[] getMac() {
        return mac;
    }

    public byte[] getBytes(){
        byte[] data = new byte[3*4];
        data[0] = (byte)((mac[0] & 0xFF000000) >>> 24);
        data[1] = (byte)((mac[0] & 0x00FF0000) >>> 16);
        data[2] = (byte)((mac[0] & 0x0000FF00) >>> 8);
        data[3] = (byte)((mac[0] & 0x000000FF));
        data[4] = (byte)((mac[1] & 0xFF000000) >>> 24);
        data[5] = (byte)((mac[1] & 0x00FF0000) >>> 16);
        data[6] = (byte)((mac[1] & 0x0000FF00) >>> 8);
        data[7] = (byte)((mac[1] & 0x000000FF));
        data[8] = (byte)((mac[2] & 0xFF000000) >>> 24);
        data[9] = (byte)((mac[2] & 0x00FF0000) >>> 16);
        data[10] = (byte)((mac[2] & 0x0000FF00) >>> 8);
        data[11] = (byte)((mac[2] & 0x000000FF));
        return data;
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