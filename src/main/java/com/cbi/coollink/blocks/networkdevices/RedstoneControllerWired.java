package com.cbi.coollink.blocks.networkdevices;

import com.cbi.coollink.Main;
import com.mojang.serialization.MapCodec;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

public abstract class RedstoneControllerWired extends BlockWithEntity implements BlockEntityProvider {

    private static final IntProperty POWER;
    static {
        POWER = Properties.POWER;
    }


    //private static final int REGULAR_POWER_DELAY = 8;

    //private static final int RECOVERABLE_POWER_DELAY = 20;

    protected RedstoneControllerWired(AbstractBlock.Settings settings) {
        super(settings);
        this.setDefaultState((this.stateManager.getDefaultState()).with(POWER, 0));
    }//Constructor

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return null;
    }

    @Nullable
    @Override
    public abstract BlockEntity createBlockEntity(BlockPos pos, BlockState state);

    private static void setPower(WorldAccess world, BlockState state, int power, BlockPos pos, int delay) {
        world.setBlockState(pos, state.with(POWER, power), 3);
        world.scheduleBlockTick(pos, state.getBlock(), delay);
    }

    protected void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (state.get(POWER) != 0) {
            world.setBlockState(pos, state.with(POWER, 0), 3);
        }

    }

    protected int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        return state.get(POWER);
    }

    protected abstract boolean emitsRedstonePower(BlockState state);

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(POWER);
    }

    protected void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        if (!world.isClient() && !state.isOf(oldState.getBlock())) {
            if (state.get(POWER) > 0 && !world.getBlockTickScheduler().isQueued(pos, this)) {
                world.setBlockState(pos, state.with(POWER, 0), 18);
            }

        }
    }



}
