package com.cbi.coollink.net.protocol;

import com.cbi.coollink.Util;
import net.minecraft.nbt.NbtCompound;

import java.util.HashMap;

abstract public class ProgramNetworkInterface {
    private final Mac deviceMac;
    private final String deviceIp;

    private PacketReceiver packetReceiver;

    private final HashMap<Integer,PackerResponse> responseMap = new HashMap<>();


    public ProgramNetworkInterface(Mac deviceMacAddress, String deviceIpAddress){
        deviceIp = deviceIpAddress;
        deviceMac =deviceMacAddress;
    }

    /**Send a packet to the specified ip address
     * @param ipAddress The Ip to send the data to
     * @param sendData The data to send to the other device
     */
    public void sendRawData(String ipAddress, NbtCompound sendData){
        IpDataPacket data = new IpDataPacket(ipAddress,deviceIp,deviceMac,sendData);
        sendIpPacketOverNetwork(data);
    }

    /**Send a packet to the specified ip address on a random port and listed for a response on that port
     * @param ipAddress The Ip to send the data to
     * @param sendData The data to send to the other device
     * @param responseCallback The code to run when a response is received, note this call balk is single use
     */
    public void sendRawData(String ipAddress, NbtCompound sendData,PackerResponse responseCallback){
        //check if a port was included
        int port = -1;
        while(port == -1){
            //generate a random port
            port = (int)(Math.random()*5900)+100;
            //check that port is not in use allready
            if(responseMap.containsKey(port)){
                port = -1;
            }
        }

        //register that port in the response map
        responseMap.put(port,responseCallback);

        //send the packet with the port in the ip
        IpDataPacket data = new IpDataPacket(ipAddress,deviceIp+":"+port,deviceMac,sendData);
        sendIpPacketOverNetwork(data);
    }

    /**Sends a packet over the network via device specific means.
     * Note: if you are writing a program you should be using sendRawData
     * @param data The packet to send over the network
     */
    public abstract void sendIpPacketOverNetwork(IpDataPacket data);

    public void processReceivedDataPacket(IpDataPacket receivedData){
        //when a new packet comes in

        //update the internal ip to mac cash

        //check if the port responded on has a handler associated with it
        int responsePort = Util.parseIpGetPort(receivedData.getDestinationIpAddress());
        if(responsePort != -1) {//if -1 then no port was included so just go to the normal handler
            PackerResponse responseHandler = responseMap.get(responsePort);
            if (responseHandler != null) {//if a handler for this port was found
                responseMap.remove(responsePort);//remove this so further packets on this port won't accidentally trigger this code again
                responseHandler.responseReceived(new ResponseContext(responsePort, receivedData));//activate that handler
            } else {//otherwise
                //just do the normal response handler
                if (packetReceiver != null) {
                    packetReceiver.receive(receivedData);
                }
            }
        }
        if (packetReceiver != null) {
            packetReceiver.receive(receivedData);
        }
    }

    public void setOnPacketReceived(PacketReceiver receiver){
        packetReceiver = receiver;
    }

    public void clearPacketReceivers(){
        packetReceiver = null;
        responseMap.clear();
    }

    public interface PacketReceiver{
        void receive(IpDataPacket receivedData);
    }

    public interface PackerResponse{
        void responseReceived(ResponseContext context);
    }

    public abstract boolean isConnectedToNetwork();

    public abstract boolean isDeviceOnline();

    public String getDeviceIp(){
        return deviceIp;
    };

    public class ResponseContext{
        private final int port;
        private final IpDataPacket responseData;

        private ResponseContext(int port, IpDataPacket responseData){
            this.responseData = responseData;
            this.port=port;
        }

        /**Respond to the incoming data and do not listen for another response
         * @param data The data to respond with
         */
        public void respond(NbtCompound data){
            sendIpPacketOverNetwork(responseData.createResponsePacket(data));
        }

        /**Respond to the incoming data and listen for another response
         * @param data The data to respond with
         * @param callback The code to run  when the next response is received, note this call back is single use
         */
        public void respond(NbtCompound data, PackerResponse callback){
            responseMap.put(port,callback);
            sendIpPacketOverNetwork(responseData.createResponsePacket(data));
        }

        public IpDataPacket getData(){
            return responseData;
        }
    }
}
