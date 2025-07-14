package com.cbi.coollink.blocks.networkdevices;

import com.cbi.coollink.Main;
import com.cbi.coollink.blocks.blockentities.RedstoneControllerWiredBE;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class RSSenderWired extends RedstoneControllerWired{
    public RSSenderWired(Settings settings) {
        super(settings);
    }

    public static final Identifier ID = Identifier.of(Main.namespace,"redstone_sender_wired");
    public static final RegistryKey<Block> BLOCK_KEY = Main.createBlockRegistryKey(ID);
    public static final RegistryKey<Item> ITEM_KEY = Main.createItemRegistryKey(ID);
    public static final RSSenderWired ENTRY = new RSSenderWired(AbstractBlock.Settings.create().hardness(0.5f).registryKey(BLOCK_KEY));

    protected boolean emitsRedstonePower(BlockState state) {
        return true;
    }


}
