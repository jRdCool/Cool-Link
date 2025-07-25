package com.cbi.coollink.blocks;

import com.cbi.coollink.Main;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class TestBlock extends Block {
    public static final Identifier ID = Identifier.of(Main.namespace,"test_block");
    public static final RegistryKey<Block> BLOCK_KEY = Main.createBlockRegistryKey(ID);
    public static final RegistryKey<Item> ITEM_KEY = Main.createItemRegistryKey(ID);

    public static final TestBlock ENTRY = new TestBlock(AbstractBlock.Settings.create().hardness(0.5f).registryKey(BLOCK_KEY));
    public TestBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        int testing = 0b11000000 >>> 6;


        Main.LOGGER.info(testing+"");


        return super.onUse(state, world, pos, player, hit);
    }
}
