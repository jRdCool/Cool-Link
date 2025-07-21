package com.cbi.coollink.blocks.networkdevices;

import com.cbi.coollink.net.protocol.IpDataPacket;

public interface Router {
    /**Resolve the mac address of the incoming data packet if it does not have a dest mac
     * @param data The packet without a dest mac
     * @return true if this packet was requesting to be given an ip by the rougher and no further regular packet processing is required
     */
  boolean resolveMacAddress(IpDataPacket data);
}
