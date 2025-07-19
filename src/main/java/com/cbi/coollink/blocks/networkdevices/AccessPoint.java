package com.cbi.coollink.blocks.networkdevices;

import com.cbi.coollink.net.WIFIClientIpPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;

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
}
