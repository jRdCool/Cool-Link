package com.cbi.coollink.net.protocol;

import net.minecraft.nbt.NbtCompound;

public class IpDataPacket {

    private Mac destinationMacAddress;
    private final Mac sourceMacAddress;

    private final String destinationIpAddress;
    private final String sourceIpAddress;

    private final NbtCompound ipData;

    /**Create a new Ip data packet when the destination mac is unknown
     * @param destinationIpAddress The ip address to send the packet to
     * @param sourceIpAddress The ip address the packet is coming from
     * @param sourceMacAddress The mac address of the device sending the packet
     * @param data The data to send
     */
    public IpDataPacket(String destinationIpAddress, String sourceIpAddress, Mac sourceMacAddress, NbtCompound data){
        this.destinationIpAddress = destinationIpAddress;
        this.sourceIpAddress = sourceIpAddress;
        this.sourceMacAddress = sourceMacAddress;
        ipData = data;
    }

    /**Create a new Ip data packet when the destination mac is unknown, IMPORTANT NOTE THE ORDER OF THE PARAMETERS, DEST MAC IS LAST!!!
     * @param destinationIpAddress The ip address to send the packet to
     * @param sourceIpAddress The ip address the packet is coming from
     * @param sourceMacAddress The mac address of the device sending the packet
     * @param destinationMacAddress The mac address of the device the packet is going to
     * @param data The data to send
     */
    public IpDataPacket(String destinationIpAddress, String sourceIpAddress, Mac sourceMacAddress, Mac destinationMacAddress,NbtCompound data){
        this.destinationIpAddress = destinationIpAddress;
        this.sourceIpAddress = sourceIpAddress;
        this.sourceMacAddress = sourceMacAddress;
        this.destinationMacAddress = destinationMacAddress;
        ipData = data;
    }

    /**Check if the destination mac address has been set
     * @return ture if the mac is not present
     */
    public boolean hasDestinationMac(){
        return  destinationIpAddress != null;
    }

    /**Set the destination mac address
     * @param destinationMacAddress The new destination mac address
     */
    public void setDestinationMacAddress(Mac destinationMacAddress){
        this.destinationMacAddress = destinationMacAddress;
    }

    /**Get the payload data this packet is transmitting
     * @return The data being transmitted
     */
    public NbtCompound getData(){
        return ipData;
    }

    /**Get the mac address of the device sending the packet
     * @return The mac address of the sending device
     */
    public Mac getSourceMacAddress(){
        return sourceMacAddress;
    }

    /**Get the mac address of the device that is receiving the data
     * @return The mac address of the device that is receiving the data
     */
    public Mac getDestinationMacAddress() {
        return destinationMacAddress;
    }

    /**Get the ip address of the device that is sending the data
     * @return The ip address of the device that send the data
     */
    public String getSourceIpAddress() {
        return sourceIpAddress;
    }

    /**Get the ip address of the device that is getting the data
     * @return The ip address the data is being sent to
     */
    public String getDestinationIpAddress() {
        return destinationIpAddress;
    }

    @Override
    public String toString() {
        return "IpDataPacket{" +
                "destinationMacAddress=" + destinationMacAddress +
                ", sourceMacAddress=" + sourceMacAddress +
                ", destinationIpAddress='" + destinationIpAddress + '\'' +
                ", sourceIpAddress='" + sourceIpAddress + '\'' +
                ", ipData=" + ipData +
                '}';
    }
}
