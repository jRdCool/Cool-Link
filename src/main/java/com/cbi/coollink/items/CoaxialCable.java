package com.cbi.coollink.items;

import com.cbi.coollink.blocks.cables.createadditons.WireType;

public class CoaxialCable extends ACableItem {
    public CoaxialCable(Settings settings) {
        super(settings);
        TYPE = WireType.COAX;
    }
}
