package com.cbi.coollink.blocks.blockentities;

import com.cbi.coollink.Main;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class ServerRackBlockEntity extends BlockEntity {
    public ServerRackBlockEntity(BlockPos pos, BlockState state) {
        super(Main.SERVER_RACK_BLOCK_ENTITY, pos, state);
    }
}
