package com.cbi.coollink.items;

import com.cbi.coollink.Main;
import com.cbi.coollink.blocks.cables.createadditons.WireType;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;

public class CoaxialCable extends ACableItem {
    public static final Identifier ID = Identifier.of(Main.namespace,"coaxial_cable");
    public static final RegistryKey<Item> ITEM_KEY = Main.createItemRegistryKey(ID);
    public CoaxialCable(Settings settings) {
        super(settings);
        TYPE = WireType.COAX;
    }
}
