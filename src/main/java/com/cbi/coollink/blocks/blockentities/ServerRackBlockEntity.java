package com.cbi.coollink.blocks.blockentities;

import com.cbi.coollink.Main;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;

public class ServerRackBlockEntity extends BlockEntity {
    public ServerRackBlockEntity(BlockPos pos, BlockState state) {
        super(Main.SERVER_RACK_BLOCK_ENTITY, pos, state);
    }
}
