package com.cbi.coollink.items;

import com.cbi.coollink.Main;
import com.cbi.coollink.blocks.cables.createadditons.WireType;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;


public class CoaxialCable extends ACableItem {
    public static final Identifier ID = Identifier.fromNamespaceAndPath(Main.namespace,"coaxial_cable");
    public static final ResourceKey<Item> ITEM_KEY = Main.createItemRegistryKey(ID);
    public CoaxialCable(Item.Properties settings) {
        super(settings.setId(ITEM_KEY));
        TYPE = WireType.COAX;
    }
}
