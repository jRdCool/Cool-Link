package com.cbi.coollink.blocks.networkdevices;

import com.cbi.coollink.Main;
import com.cbi.coollink.blocks.blockentities.RedstoneControllerWiredBE;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.block.OrientationHelper;
import net.minecraft.world.block.WireOrientation;
import net.minecraft.world.tick.ScheduledTickView;
import org.jetbrains.annotations.Nullable;

import static net.minecraft.state.property.Properties.FACING;

public class RSSenderWired extends RedstoneControllerWired{
    public RSSenderWired(Settings settings) {
        super(settings);

    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new RedstoneControllerWiredBE(pos,state,Main.RS_SENDER_WIRED_BLOCK_ENTITY);
    }

    //private static final BooleanProperty POWERED = Properties.POWERED;

    public static final Identifier ID = Identifier.of(Main.namespace,"redstone_sender_wired");
    public static final RegistryKey<Block> BLOCK_KEY = Main.createBlockRegistryKey(ID);
    public static final RegistryKey<Item> ITEM_KEY = Main.createItemRegistryKey(ID);
    public static final RSSenderWired ENTRY = new RSSenderWired(AbstractBlock.Settings.create().hardness(0.5f).registryKey(BLOCK_KEY));

    //Properties.POWERED
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing());
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING);
        super.appendProperties(builder);
    }
    public boolean emitsRedstonePower(BlockState state) {
        return true;
    }

    protected void updateNeighbors(World world, BlockPos pos, BlockState state) {
        Direction direction = state.get(FACING);
        BlockPos blockPos = pos.offset(direction.getOpposite());
        WireOrientation wireOrientation = OrientationHelper.getEmissionOrientation(world, direction.getOpposite(), null);
        world.updateNeighbor(blockPos, this, wireOrientation);
        world.updateNeighborsExcept(blockPos, this, direction, wireOrientation);
    }

    /*
    protected BlockState getStateForNeighborUpdate(BlockState state, WorldView world, ScheduledTickView tickView, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, Random random) {
        if (state.get(FACING) == direction && !(Boolean)state.get(POWERED)) {
            this.scheduleTick(world, tickView, pos);
        }

        return super.getStateForNeighborUpdate(state, world, tickView, pos, direction, neighborPos, neighborState, random);
    }*/

    protected void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (state.get(POWERED)) {
            world.setBlockState(pos, state.with(POWERED, false), 2);
        } else {
            world.setBlockState(pos, state.with(POWERED, true), 2);
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
