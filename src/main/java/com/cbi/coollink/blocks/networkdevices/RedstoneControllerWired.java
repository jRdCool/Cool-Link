package com.cbi.coollink.blocks.networkdevices;

import com.cbi.coollink.Main;
import com.cbi.coollink.blocks.blockentities.RedstoneControllerWiredBE;
import com.cbi.coollink.rendering.IWireNode;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.random.WeightedRandom;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
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

    @Override
    protected int getSignal(BlockState state, @NonNull BlockGetter world, @NonNull BlockPos pos, @NonNull Direction direction) {
        return state.getValue(POWER);
    }

    @Override
    protected int getDirectSignal(@NonNull BlockState state, @NonNull BlockGetter world, @NonNull BlockPos pos, @NonNull Direction direction) {
        return !this.wiresGivePower ? 0 : state.getSignal(world, pos, direction);
    }

    protected abstract boolean emitsRedstonePower(BlockState state);

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> stateManager) {
        stateManager.add(POWER)
                .add(POWERED);
    }

    @Override
    protected void onPlace(@NonNull BlockState state, Level world, @NonNull BlockPos pos, @NonNull BlockState oldState, boolean notify) {
        if (!world.isClientSide() && !state.is(oldState.getBlock())) {
            if (state.getValue(POWER) > 0 && !world.getBlockTickScheduler().isQueued(pos, this)) {
                world.setBlock(pos, state.setValue(POWER, 0), 18);
            }

        }
    }
    @Override
    public void destroy(LevelAccessor world, @NonNull BlockPos pos, @NonNull BlockState state) {
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
        super.destroy(world,pos,state);
    }

    @Override
    protected void onExplosionHit(@NonNull BlockState state, ServerLevel level, @NonNull BlockPos pos, @NonNull Explosion explosion, @NonNull BiConsumer<ItemStack, BlockPos> onHit) {
        //delete both ends of the connection when the block is broken
        BlockEntity be = level.getBlockEntity(pos);
        if(be instanceof IWireNode self){
            for(int i=0;i<self.getNodeCount();i++){
                if(!self.hasConnection(i)) continue;
                BlockEntity obe =  level.getBlockEntity(self.getLocalNode(i).getTargetPos());
                if(obe instanceof IWireNode other) {
                    other.removeNode(self.getOtherNodeIndex(i));
                }
            }
        }
        super.onExplosionHit(state, level, pos, explosion, onHit);
    }

    //Former int get Strong Power
    public int getBlockSignal(final Level level, final BlockPos pos) {
        this.wiresGivePower = false;
        int i = level.getBestNeighborSignal(pos);
        this.wiresGivePower = true;
        return i;
    }


    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NonNull Level world, @NonNull BlockState state, @NonNull BlockEntityType<T> type) {
        if(state.getBlock() instanceof RSReceiverWired){
            return createTickerHelper(type, Main.RS_RECEIVER_WIRED_BLOCK_ENTITY, RedstoneControllerWiredBE::tick);
        }
        if(state.getBlock() instanceof RSSenderWired){
            return createTickerHelper(type, Main.RS_SENDER_WIRED_BLOCK_ENTITY, RedstoneControllerWiredBE::tick);
        }
        return null;
    }
}
