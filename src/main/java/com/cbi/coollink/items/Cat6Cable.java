package com.cbi.coollink.items;

import com.cbi.coollink.blocks.cables.createadditons.WireType;

public class Cat6Cable extends ACableItem {
    public Cat6Cable(Settings settings) {
        super(settings);
        TYPE = WireType.CAT6;
    }

}
