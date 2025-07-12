package com.cbi.coollink.blocks.networkdevices;

import com.cbi.coollink.Main;
import com.mojang.serialization.MapCodec;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class RedstoneControllerWired extends BlockWithEntity implements BlockEntityProvider {
    public static final Identifier ID = Identifier.of(Main.namespace,"redstone_ctrl_wired");
    public static final RegistryKey<Block> BLOCK_KEY = Main.createBlockRegistryKey(ID);
    public static final RegistryKey<Item> ITEM_KEY = Main.createItemRegistryKey(ID);
    public static final RedstoneControllerWired ENTRY = new RedstoneControllerWired(AbstractBlock.Settings.create().hardness(0.5f).registryKey(BLOCK_KEY));
    protected RedstoneControllerWired(Settings settings) {
        super(settings);
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return null;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return null;
    }
}
