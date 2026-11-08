package com.cbi.coollink.items;

import com.cbi.coollink.Main;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;

public class ProgramingCable extends Item {
    public static final Identifier ID = Identifier.fromNamespaceAndPath(Main.namespace,"programing_cable");
    public static final ResourceKey<Item> ITEM_KEY = Main.createItemRegistryKey(ID);
    public ProgramingCable(Properties settings) {
        super(settings.setId(ITEM_KEY));
    }
}
