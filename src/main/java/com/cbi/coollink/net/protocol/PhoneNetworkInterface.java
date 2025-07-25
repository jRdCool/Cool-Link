package com.cbi.coollink.net.protocol;

import com.cbi.coollink.Main;
import com.cbi.coollink.net.WIFIClientIpPacket;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.HashMap;

public class PhoneNetworkInterface extends ProgramNetworkInterface{

    private boolean connectedToNetwork = false;
    private boolean deviceOnline = false;
    private World playerWorld = null;
    private BlockPos accessPointPos = null;

    private final HashMap<String, Mac> ipMacCash = new HashMap<>();

    public PhoneNetworkInterface(Mac deviceMacAddress) {
        super(deviceMacAddress, "127.0.0.1");
    }

    public PhoneNetworkInterface(Mac deviceMacAddress, String deviceIpAddress, World playerWorld, BlockPos accessPointPos, boolean deviceOnline) {
        super(deviceMacAddress, deviceIpAddress);
        connectedToNetwork = true;
        this.playerWorld = playerWorld;
        this.accessPointPos = accessPointPos;
        this.deviceOnline = deviceOnline;
    }



    @Override
    public void sendIpPacketOverNetwork(IpDataPacket data) {
        if(!connectedToNetwork){
            Main.LOGGER.error("App tried to send data packet but device is offline");
            return;
        }
        //Main.LOGGER.info("Phone sending packet: "+data);
        if(!data.hasDestinationMac()){
            Mac destmac = ipMacCash.get(data.getSourceIpAddress());
            if(destmac != null){
                data.setDestinationMacAddress(destmac);
            }
        }
        ClientPlayNetworking.send(new WIFIClientIpPacket(playerWorld.getRegistryKey(),accessPointPos,data));

    }

    public boolean isConnectedToNetwork(){
        return connectedToNetwork;
    }

    public boolean isDeviceOnline(){
        return deviceOnline;
    }

    @Override
    public void processReceivedDataPacket(IpDataPacket receivedData) {
        //update the mac cash
        ipMacCash.put(receivedData.getSourceIpAddress(),receivedData.getSourceMacAddress());
        //handle the packet normally
        super.processReceivedDataPacket(receivedData);
    }
}
