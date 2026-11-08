package com.cbi.coollink.blocks.networkdevices;

import com.cbi.coollink.Main;
import com.cbi.coollink.blocks.blockentities.RedstoneControllerWiredBE;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.redstone.ExperimentalRedstoneUtils;
import net.minecraft.world.level.redstone.Orientation;
import org.jspecify.annotations.NonNull;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.FACING;

public class RSSenderWired extends RedstoneControllerWired{
    public RSSenderWired(BlockBehaviour.Properties settings) {
        super(settings.setId(BLOCK_KEY));

    }

    @Override
    public BlockEntity newBlockEntity(@NonNull BlockPos pos, @NonNull BlockState state) {
        return new RedstoneControllerWiredBE(pos,state,Main.RS_SENDER_WIRED_BLOCK_ENTITY);
    }

    //private static final BooleanProperty POWERED = Properties.POWERED;

    public static final Identifier ID = Identifier.fromNamespaceAndPath(Main.namespace,"redstone_sender_wired");
    public static final ResourceKey<Block> BLOCK_KEY = Main.createBlockRegistryKey(ID);
    public static final ResourceKey<Item> ITEM_KEY = Main.createItemRegistryKey(ID);
    public static final RSSenderWired ENTRY = new RSSenderWired(BlockBehaviour.Properties.of().strength(0.5f));

    //Properties.POWERED
    @Override
    public @org.jspecify.annotations.Nullable BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return defaultBlockState().setValue(FACING, ctx.getHorizontalDirection());
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> stateManager) {
        stateManager.add(FACING);
        super.createBlockStateDefinition(stateManager);
    }
    public boolean emitsRedstonePower(BlockState state) {
        return true;
    }

    protected void updateNeighbors(Level world, BlockPos pos, BlockState state) {
        Direction direction = state.getValue(FACING);
        BlockPos blockPos = pos.offset(direction.getOpposite().getUnitVec3i());
        Orientation wireOrientation = ExperimentalRedstoneUtils.initialOrientation(world, direction.getOpposite(), null);
        world.updateNeighborsAt(blockPos, this, wireOrientation);
        world.updateNeighborsAtExceptFromFacing(blockPos, this, direction, wireOrientation);

    }

    /*
    protected BlockState getStateForNeighborUpdate(BlockState state, WorldView world, ScheduledTickView tickView, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, Random random) {
        if (state.get(FACING) == direction && !(Boolean)state.get(POWERED)) {
            this.scheduleTick(world, tickView, pos);
        }

        return super.getStateForNeighborUpdate(state, world, tickView, pos, direction, neighborPos, neighborState, random);
    }*/

    protected void scheduledTick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
        if (state.getValue(POWERED)) {
            world.setBlock(pos, state.setValue(POWERED, false), 2);
        } else {
            world.setBlock(pos, state.setValue(POWERED, true), 2);
            //world.scheduleBlockTick(pos, this, 2);
        }

        this.updateNeighbors(world, pos, state);
    }


/*
    private void scheduleTick(WorldView world, ScheduledTickView tickView, BlockPos pos) {
        if (!world.isClient() && !tickView.getBlockTickScheduler().isQueued(pos, this)) {
            tickView.scheduleBlockTick(pos, this, 2);
        }

    }*/


}
