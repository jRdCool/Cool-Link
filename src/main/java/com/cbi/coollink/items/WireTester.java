package com.cbi.coollink.items;

import com.cbi.coollink.Main;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;

public class WireTester extends Item {

    public static final Identifier ID = Identifier.of(Main.namespace,"wire_tester");
    public static final RegistryKey<Item> ITEM_KEY = Main.createItemRegistryKey(ID);
    public WireTester(Settings settings) {
        super(settings);
    }
}
