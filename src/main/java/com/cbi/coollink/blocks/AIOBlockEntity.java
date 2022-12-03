package com.cbi.coollink.blocks;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;

public class AIOBlockEntity extends BlockEntity {
    public AIOBlockEntity(BlockPos pos, BlockState state) {
        super(coollink.AIO_BLOCK_ENTITY, pos, state);
    }
}
