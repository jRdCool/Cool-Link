package com.cbi.coollink.blocks.networkdevices;

import com.cbi.coollink.Main;
import com.cbi.coollink.blocks.blockentities.RedstoneControllerWiredBE;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKey;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockRenderView;
import net.minecraft.world.RedstoneView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.block.WireOrientation;
import net.minecraft.world.tick.ScheduledTickView;
import org.jetbrains.annotations.Nullable;

import static net.minecraft.state.property.Properties.FACING;

public class RSReceiverWired extends RedstoneControllerWired {
    public RSReceiverWired(Settings settings) {
        super(settings);
        this.setDefaultState((this.stateManager.getDefaultState()).with(RECEIVED_POWER,0));

    }


    public static final IntProperty RECEIVED_POWER = IntProperty.of("received_power",0,15);
    public static final Identifier ID = Identifier.of(Main.namespace,"redstone_receiver_wired");
    public static final RegistryKey<Block> BLOCK_KEY = Main.createBlockRegistryKey(ID);
    public static final RegistryKey<Item> ITEM_KEY = Main.createItemRegistryKey(ID);
    public static final RSReceiverWired ENTRY = new RSReceiverWired(AbstractBlock.Settings.create().hardness(0.5f).registryKey(BLOCK_KEY));


    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new RedstoneControllerWiredBE(pos,state,Main.RS_RECEIVER_WIRED_BLOCK_ENTITY);
    }

    @Override
    public boolean emitsRedstonePower(BlockState state) {return false;}

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING)
                .add(RECEIVED_POWER);
        super.appendProperties(builder);
    }


    protected BlockState getStateForNeighborUpdate(BlockState state, WorldView world, ScheduledTickView tickView, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, Random random) {
        //TODO : input power logic here
        //Main.LOGGER.info(neighborPos.toString());
        return shouldPower(world,pos,direction);

        //super.onStateReplaced(state,world);
        /*if (direction == Direction.DOWN) {
            return !this.canRunOnTop(world, neighborPos, neighborState) ? Blocks.AIR.getDefaultState() : state;
        } else if (direction == Direction.UP) {
            return this.getPlacementState(world, state, pos);
        } else {
            WireConnection wireConnection = this.getRenderConnectionType(world, pos, direction);
            return wireConnection.isConnected() == ((WireConnection)state.get((Property)DIRECTION_TO_WIRE_CONNECTION_PROPERTY.get(direction))).isConnected() && !isFullyConnected(state) ? (BlockState)state.with((Property)DIRECTION_TO_WIRE_CONNECTION_PROPERTY.get(direction), wireConnection) : this.getPlacementState(world, (BlockState)((BlockState)this.dotState.with(POWER, (Integer)state.get(POWER))).with((Property)DIRECTION_TO_WIRE_CONNECTION_PROPERTY.get(direction), wireConnection), pos);
        }*/
    }

    protected BlockState shouldPower(RedstoneView world, BlockPos pos, Direction direction) {
        if(world.isReceivingRedstonePower(pos)){

            return world.getBlockState(pos).with(RECEIVED_POWER,world.getEmittedRedstonePower(pos,direction));//.with(POWERED,true);

        }
        return world.getBlockState(pos).with(RECEIVED_POWER,0);//.with(POWERED,false);
    }
//


    @Override
    public BlockState getAppearance(BlockState state, BlockRenderView renderView, BlockPos pos, Direction side, @Nullable BlockState sourceState, @Nullable BlockPos sourcePos) {
        return super.getAppearance(state, renderView, pos, side, sourceState, sourcePos);
    }

    protected boolean onSyncedBlockEvent(BlockState state, World world, BlockPos pos, int type, int data) {
        if(!world.isClient) {
            shouldPower(world, pos, state.get(FACING));
            Main.LOGGER.info("State: " + state + " World: " + world + "\n BlockPos: " + pos + "\ntype: " + type + " data" + data);

        }
        return true;
    }

    protected void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, @Nullable WireOrientation wireOrientation, boolean notify) {
        if (!world.isClient) {
            this.shouldPower(world, pos, state.get(FACING));
        }

        boolean receivingPower = world.isReceivingRedstonePower(pos) || world.isReceivingRedstonePower(pos.up());
        boolean isPowered = state.get(POWERED);
        if (receivingPower && !isPowered) {
            world.scheduleBlockTick(pos, this, 4);
            world.setBlockState(pos, state.with(POWERED, true), 2);
        } else if (!receivingPower && isPowered) {
            world.setBlockState(pos, state.with(POWERED, false), 2);
        }

    }
}
