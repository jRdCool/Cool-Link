package com.cbi.coollink.items;

import com.cbi.coollink.Main;
import com.cbi.coollink.blocks.cables.createadditons.WireType;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;

public class Cat6Cable extends ACableItem {
    public static final Identifier ID = Identifier.fromNamespaceAndPath(Main.namespace,"cat6_ethernet_cable");
    public static final ResourceKey<Item> ITEM_KEY = Main.createItemRegistryKey(ID);
    public Cat6Cable(Item.Properties settings) {
        super(settings.setId(ITEM_KEY));
        TYPE = WireType.CAT6;
    }

}
