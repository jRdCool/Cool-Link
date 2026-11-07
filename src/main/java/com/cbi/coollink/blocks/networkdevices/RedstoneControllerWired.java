package com.cbi.coollink.blocks.networkdevices;

import com.cbi.coollink.Main;
import com.cbi.coollink.blocks.blockentities.RedstoneControllerWiredBE;
import com.cbi.coollink.rendering.IWireNode;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.random.WeightedRandom;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import java.util.function.BiConsumer;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.FACING;

public abstract class RedstoneControllerWired extends BaseEntityBlock {

    public static final IntegerProperty POWER;
    static {
        POWER = BlockStateProperties.POWER;
    }
    private boolean wiresGivePower = true;
    protected static final BooleanProperty POWERED = BlockStateProperties.POWERED;


    //private static final int REGULAR_POWER_DELAY = 8;

    //private static final int RECOVERABLE_POWER_DELAY = 20;

    protected RedstoneControllerWired(BlockBehaviour.Properties settings) {
        super(settings);
        registerDefaultState(defaultBlockState()
                .setValue(POWER, 0)
                .setValue(FACING, Direction.SOUTH)
                .setValue(POWERED,false));
    }//Constructor

    @Nullable
    @Override
    public abstract BlockEntity newBlockEntity(@NonNull BlockPos pos, @NonNull BlockState state);

    public void setPower(LevelAccessor world, BlockState state, int power, BlockPos pos, int delay) {
        world.setBlock(pos, state.setValue(POWER, power), 3);
        world.scheduleBlockTick(pos, state.getBlock(), delay);
    }


    protected void scheduledTick(BlockState state, ServerLevel world, BlockPos pos, WeightedRandom random) {
        if (state.getValue(POWER) != 0) {
            //world.setBlockState(pos, state.setValue(POWER, 0), 3);
            state.setValue(POWER,world.getDirectSignalTo(pos));
        }
    }


    protected int getWeakRedstonePower(BlockState state, view world, BlockPos pos, Direction direction) {
        return state.getValue(POWER);
    }

    protected int getStrongRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        return !this.wiresGivePower ? 0 : state.getWeakRedstonePower(world, pos, direction);
    }

    protected abstract boolean emitsRedstonePower(BlockState state);

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(POWER)
                .add(POWERED);
    }

    protected void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        if (!world.isClient() && !state.isOf(oldState.getBlock())) {
            if (state.get(POWER) > 0 && !world.getBlockTickScheduler().isQueued(pos, this)) {
                world.setBlockState(pos, state.setValue(POWER, 0), 18);
            }

        }
    }
    @Override
    public BlockState onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        //delete both ends of the connection when the block is broken
        BlockEntity be = world.getBlockEntity(pos);
        if(be instanceof IWireNode self){
            for(int i=0;i<self.getNodeCount();i++){
                if(!self.hasConnection(i)) continue;
                BlockEntity obe =  world.getBlockEntity(self.getLocalNode(i).getTargetPos());
                if(obe instanceof IWireNode other) {
                    other.removeNode(self.getOtherNodeIndex(i));
                }
            }
        }
        return super.onBreak(world,pos,state,player);
    }

    @Override
    protected void onExploded(BlockState state, ServerWorld world, BlockPos pos, Explosion explosion, BiConsumer<ItemStack, BlockPos> stackMerger) {
        //delete both ends of the connection when the block is broken
        BlockEntity be = world.getBlockEntity(pos);
        if(be instanceof IWireNode self){
            for(int i=0;i<self.getNodeCount();i++){
                if(!self.hasConnection(i)) continue;
                BlockEntity obe =  world.getBlockEntity(self.getLocalNode(i).getTargetPos());
                if(obe instanceof IWireNode other) {
                    other.removeNode(self.getOtherNodeIndex(i));
                }
            }
        }
        super.onExploded(state, world, pos, explosion, stackMerger);
    }

    public int getStrongPower(World world, BlockPos pos) {
        this.wiresGivePower = false;
        int i = world.getReceivedRedstonePower(pos);
        this.wiresGivePower = true;
        return i;
    }


    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        if(state.getBlock() instanceof RSReceiverWired){
            return validateTicker(type, Main.RS_RECEIVER_WIRED_BLOCK_ENTITY, RedstoneControllerWiredBE::tick);
        }
        if(state.getBlock() instanceof RSSenderWired){
            return validateTicker(type, Main.RS_SENDER_WIRED_BLOCK_ENTITY, RedstoneControllerWiredBE::tick);
        }
        return null;
    }
}
