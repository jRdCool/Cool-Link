package com.cbi.coollink.blocks.networkdevices;

public interface Switch {
    void switchProcessPacketQueue();

    boolean knowsWhereRouterIs();
}
