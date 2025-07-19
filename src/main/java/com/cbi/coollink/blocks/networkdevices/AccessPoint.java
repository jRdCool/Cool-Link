package com.cbi.coollink.blocks.networkdevices;

import com.cbi.coollink.net.WIFIClientIpPacket;
import com.cbi.coollink.net.protocol.Mac;
import net.minecraft.server.network.ServerPlayerEntity;

public interface AccessPoint {
    /**Take an ip packet from the wifi and transmits it over the network
     * @param packet The ip packet transmitted over the wifi
     * @param player The player that transmitted it
     */
    void processIncomingWifiPacket(WIFIClientIpPacket packet, ServerPlayerEntity player);

    /**Send the requesting player an array if block positions repressing all the wireless access points connected to this network
     * @param player The player sending the request
     */
    void getNetworkAccessPointLocations(ServerPlayerEntity player);

    /**Handle a wifi client (the phone) requesting to connect to the network
     * @param password What the client sent as the password
     * @param deviceMacAddress The mac address of the device that wants to join the network
     * @param player The player that is holding the phone
     * @param deviceName The name of the device connecting to the network
     */
    void handleClientWifiConnectionRequest(String password, Mac deviceMacAddress, ServerPlayerEntity player, String deviceName);
}
