package com.cbi.coollink.blocks.networkdevices;

import com.cbi.coollink.Main;
import com.cbi.coollink.blocks.blockentities.ServerRackBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.redstone.Orientation;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.FACING;

public class RSReceiverWired extends RedstoneControllerWired {
    public RSReceiverWired(BlockBehaviour.Properties settings) {
        super(settings);
        registerDefaultState(defaultBlockState()
                .setValue(RECEIVED_POWER,0)
        );

    }


    public static final IntegerProperty RECEIVED_POWER = IntegerProperty.create("received_power",0,15);
    public static final Identifier ID = Identifier.fromNamespaceAndPath(Main.namespace,"redstone_receiver_wired");
    public static final ResourceKey<Block> BLOCK_KEY = Main.createBlockRegistryKey(ID);
    public static final ResourceKey<Item> ITEM_KEY = Main.createItemRegistryKey(ID);
    public static final RSReceiverWired ENTRY = new RSReceiverWired(BlockBehaviour.Properties.of().strength(0.5f));


    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NonNull BlockPos pos, @NonNull BlockState state) {
        return new ServerRackBlockEntity(pos,state);
    }

    @Override
    public boolean emitsRedstonePower(BlockState state) {return false;}

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> stateManager) {
        stateManager.add(FACING)
                .add(RECEIVED_POWER);
        super.createBlockStateDefinition(stateManager);
    }

    @Override
    protected @NonNull BlockState updateShape(@NonNull BlockState state, @NonNull LevelReader level, @NonNull ScheduledTickAccess ticks, @NonNull BlockPos pos, @NonNull Direction directionToNeighbour, @NonNull BlockPos neighbourPos, @NonNull BlockState neighbourState, @NonNull RandomSource random) {
        //Main.LOGGER.info(neighborPos.toString());
        return shouldPower(level,pos,directionToNeighbour);

        //super.onStateReplaced(state,world);
        /*if (direction == Direction.DOWN) {
            return !this.canRunOnTop(world, neighborPos, neighborState) ? Blocks.AIR.getDefaultState() : state;
        } else if (direction == Direction.UP) {
            return this.getPlacementState(world, state, pos);
        } else {
            WireConnection wireConnection = this.getRenderConnectionType(world, pos, directionToNeighbour);
            return wireConnection.isConnected() == ((WireConnection)state.get((Property)DIRECTION_TO_WIRE_CONNECTION_PROPERTY.get(directionToNeighbour))).isConnected() && !isFullyConnected(state) ? (BlockState)state.with((Property)DIRECTION_TO_WIRE_CONNECTION_PROPERTY.get(directionToNeighbour), wireConnection) : this.getPlacementState(world, (BlockState)((BlockState)this.dotState.with(POWER, (Integer)state.get(POWER))).with((Property)DIRECTION_TO_WIRE_CONNECTION_PROPERTY.get(directionToNeighbour), wireConnection), pos);
        }*/
    }

    protected BlockState shouldPower(LevelReader world, BlockPos pos, Direction direction) {
        if(world.hasNeighborSignal(pos)){

            return world.getBlockState(pos).setValue(RECEIVED_POWER,world.getSignal(pos,direction));//.with(POWERED,true);

        }
        return world.getBlockState(pos).setValue(RECEIVED_POWER,0);//.with(POWERED,false);
    }
//


    @Override
    public @NonNull BlockState getAppearance(@NonNull BlockState state, @NonNull BlockAndLightGetter renderView, @NonNull BlockPos pos, @NonNull Direction side, @Nullable BlockState sourceState, @Nullable BlockPos sourcePos) {
        return super.getAppearance(state, renderView, pos, side, sourceState, sourcePos);
    }

    protected boolean onSyncedBlockEvent(BlockState state, Level world, BlockPos pos, int type, int data) {
        if(!world.isClientSide()) {
            shouldPower(world, pos, state.getValue(FACING));
            Main.LOGGER.info("State: " + state + " World: " + world + "\n BlockPos: " + pos + "\ntype: " + type + " data" + data);

        }
        return true;
    }

    protected void neighborChanged(@NonNull BlockState state, Level world, @NonNull BlockPos pos, @NonNull Block sourceBlock, @Nullable Orientation wireOrientation, boolean notify) {
        if (!world.isClientSide()) {
            this.shouldPower(world, pos, state.getValue(FACING));
        }


        boolean receivingPower = world.hasNeighborSignal(pos) || world.hasNeighborSignal(pos.above());
        boolean isPowered = state.getValue(POWERED);
        if (receivingPower && !isPowered) {
            world.scheduleTick(pos, this, 4);
            world.setBlock(pos, state.setValue(POWERED, true), 2);
        } else if (!receivingPower && isPowered) {
            world.setBlock(pos, state.setValue(POWERED, false), 2);
        }

    }
}
