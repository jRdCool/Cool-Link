package com.cbi.coollink.items;

import com.cbi.coollink.Main;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;

public class ProgramingCable extends Item {
    public static final Identifier ID = Identifier.of(Main.namespace,"programing_cable");
    public static final RegistryKey<Item> ITEM_KEY = Main.createItemRegistryKey(ID);
    public ProgramingCable(Settings settings) {
        super(settings);
    }
}
