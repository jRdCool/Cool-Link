package com.cbi.coollink.net.protocol;

public class CoaxDataPacket implements WireDataPacket{
    private final boolean requestOnline;
    private final boolean uplinkOnline;

    private CoaxDataPacket(boolean online,boolean request){
        requestOnline = request;
        uplinkOnline = online;
    }

    public static CoaxDataPacket ofRequest(){
        return new CoaxDataPacket(false,true);
    }

    public static CoaxDataPacket ofResponse(boolean online){
        return new CoaxDataPacket(online,false);
    }


    public boolean isRequestOnline() {
        return requestOnline;
    }

    public boolean isUplinkOnline() {
        return uplinkOnline;
    }

    @Override
    public String toString() {
        return "CoaxDataPacket{" +
                "requestOnline=" + requestOnline +
                ", uplinkOnline=" + uplinkOnline +
                '}';
    }
}
