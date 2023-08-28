package com.cbi.coollink.blocks;

import com.cbi.coollink.Main;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;

public class ConduitBlockEntity extends BlockEntity {
    public ConduitBlockEntity(BlockPos pos, BlockState state) {
        super(Main.CONDUIT_BLOCK_ENTITY, pos, state);
    }
}
