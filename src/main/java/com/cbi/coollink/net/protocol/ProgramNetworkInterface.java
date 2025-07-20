package com.cbi.coollink.net.protocol;

import net.minecraft.nbt.NbtCompound;

abstract public class ProgramNetworkInterface {
    private final Mac deviceMac;
    private final String deviceIp;

    private PacketReceiver packetReceiver;


    public ProgramNetworkInterface(Mac deviceMacAddress, String deviceIpAddress){
        deviceIp = deviceIpAddress;
        deviceMac =deviceMacAddress;
    }

    public void sendRawData(String ipAddress, NbtCompound sendData){
        IpDataPacket data = new IpDataPacket(ipAddress,deviceIp,deviceMac,sendData);
        sendIpPacketOverNetwork(data);
    }

    public abstract void sendIpPacketOverNetwork(IpDataPacket data);

    public void processReceivedDataPacket(IpDataPacket receivedData){
        if(packetReceiver != null){
            packetReceiver.receive(receivedData);
        }
    }

    public void setOnPacketReceived(PacketReceiver receiver){
        packetReceiver = receiver;
    }

    public interface PacketReceiver{
        void receive(IpDataPacket receivedData);
    }

    public abstract boolean isConnectedToNetwork();

    public abstract boolean isDeviceOnline();
}
