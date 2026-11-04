package com.cbi.coollink.blocks;

import com.cbi.coollink.Main;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;



public class TestBlock extends Block {
    public static final Identifier ID = Identifier.fromNamespaceAndPath(Main.namespace,"test_block");
    public static final ResourceKey<Block> BLOCK_KEY = Main.createBlockRegistryKey(ID);
    public static final ResourceKey<Item> ITEM_KEY = Main.createItemRegistryKey(ID);

    public static final TestBlock ENTRY = new TestBlock(Properties.of().strength(0.5f)/*.registryKey(BLOCK_KEY)*/);
    public TestBlock(Properties settings) {
        super(settings);
    }
}
