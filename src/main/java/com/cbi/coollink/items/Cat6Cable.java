package com.cbi.coollink.items;

import com.cbi.coollink.Main;
import com.cbi.coollink.blocks.cables.createadditons.WireType;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;

public class Cat6Cable extends ACableItem {
    public static final Identifier ID = Identifier.of(Main.namespace,"cat6_ethernet_cable");
    public static final RegistryKey<Item> ITEM_KEY = Main.createItemRegistryKey(ID);
    public Cat6Cable(Settings settings) {
        super(settings);
        TYPE = WireType.CAT6;
    }

}
