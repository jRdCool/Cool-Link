package com.cbi.coollink.blocks;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;

public class AIOBlockEntity extends BlockEntity {
    public DAIOBlockEntity(BlockPos pos, BlockState state) {
        super(ExampleMod.AIO_BLOCK_ENTITY, pos, state);
    }
}
