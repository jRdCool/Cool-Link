package com.cbi.coollink.blocks.networkdevices;

import com.cbi.coollink.Main;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;

public class RSSenderWired extends RedstoneControllerWired{
    public RSSenderWired(Settings settings) {
        super(settings);
    }

    public static final Identifier ID = Identifier.of(Main.namespace,"redstone_sender_wired");
    public static final RegistryKey<Block> BLOCK_KEY = Main.createBlockRegistryKey(ID);
    public static final RegistryKey<Item> ITEM_KEY = Main.createItemRegistryKey(ID);
    public static final RSSenderWired ENTRY = new RSSenderWired(AbstractBlock.Settings.create().hardness(0.5f).registryKey(BLOCK_KEY));
}
