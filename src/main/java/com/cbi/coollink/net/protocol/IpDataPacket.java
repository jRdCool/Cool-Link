package com.cbi.coollink.net.protocol;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;

import java.nio.charset.Charset;

public class IpDataPacket implements WireDataPacket{

    public static final PacketCodec<ByteBuf, IpDataPacket> PACKET_CODEC = new PacketCodec<ByteBuf, IpDataPacket>() {
        public IpDataPacket decode(ByteBuf byteBuf) {
            boolean hasDestMac;
            Mac mac1 = null;
            Mac mac2;

            String sourceIp;
            String destIp;

            NbtCompound data;
            hasDestMac = byteBuf.readBoolean();
            if(hasDestMac){
                int[] m1i = {0,0,0};
                m1i[0] = byteBuf.readInt();
                m1i[1] = byteBuf.readInt();
                m1i[2] = byteBuf.readInt();
                mac1 = new Mac(m1i);
            }
            int[] m2i = {0,0,0};
            m2i[0] = byteBuf.readInt();
            m2i[1] = byteBuf.readInt();
            m2i[2] = byteBuf.readInt();
            mac2 = new Mac(m2i);

            int strLength = byteBuf.readInt();
            destIp = byteBuf.readCharSequence(strLength,Charset.defaultCharset()).toString();
            strLength = byteBuf.readInt();
            sourceIp = byteBuf.readCharSequence(strLength,Charset.defaultCharset()).toString();

            data = PacketCodecs.NBT_COMPOUND.decode(byteBuf);
            if(hasDestMac){
                return new IpDataPacket(destIp,sourceIp,mac2,mac1,data);
            }else{
                return new IpDataPacket(destIp,sourceIp,mac2,data);
            }
        }

        public void encode(ByteBuf byteBuf, IpDataPacket data) {
            byteBuf.writeBoolean(data.hasDestinationMac());
            if(data.hasDestinationMac()){
                int[] mac1Arr = data.destinationMacAddress.getMac();
                byteBuf.writeInt(mac1Arr[0]);
                byteBuf.writeInt(mac1Arr[1]);
                byteBuf.writeInt(mac1Arr[2]);
            }
            int[] mac2Arr = data.getSourceMacAddress().getMac();
            byteBuf.writeInt(mac2Arr[0]);
            byteBuf.writeInt(mac2Arr[1]);
            byteBuf.writeInt(mac2Arr[2]);

            int byteLength = data.destinationIpAddress.getBytes(Charset.defaultCharset()).length;
            byteBuf.writeInt(byteLength);
            byteBuf.writeCharSequence(data.destinationIpAddress, Charset.defaultCharset());
            byteLength = data.sourceIpAddress.getBytes(Charset.defaultCharset()).length;
            byteBuf.writeInt(byteLength);
            byteBuf.writeCharSequence(data.sourceIpAddress, Charset.defaultCharset());

            PacketCodecs.NBT_COMPOUND.encode(byteBuf,data.ipData);

        }
    };;

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

    /**Create a new ip data packet to respond to a received request. Basically just creates a bew packet with eh source and dest values swapped
     * @param responseData The data to respond with
     * @return A new data packet that can be sent back to whoever sent it
     */
    public IpDataPacket createResponsePacket(NbtCompound responseData){
        return new IpDataPacket(sourceIpAddress,destinationIpAddress,destinationMacAddress,sourceMacAddress,responseData);
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
