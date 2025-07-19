package com.cbi.coollink.blocks.networkdevices;

import com.cbi.coollink.Main;
import com.cbi.coollink.rendering.IWireNode;
import com.mojang.serialization.MapCodec;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
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
import net.minecraft.world.explosion.Explosion;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;

import static net.minecraft.state.property.Properties.FACING;

public abstract class RedstoneControllerWired extends BlockWithEntity implements BlockEntityProvider {

    public static final IntProperty POWER;
    static {
        POWER = Properties.POWER;
    }


    //private static final int REGULAR_POWER_DELAY = 8;

    //private static final int RECOVERABLE_POWER_DELAY = 20;

    protected RedstoneControllerWired(AbstractBlock.Settings settings) {
        super(settings);
        this.setDefaultState((this.stateManager.getDefaultState()).with(POWER, 0).with(FACING, Direction.SOUTH));
    }//Constructor

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return null;
    }

    @Nullable
    @Override
    public abstract BlockEntity createBlockEntity(BlockPos pos, BlockState state);

    public void setPower(WorldAccess world, BlockState state, int power, BlockPos pos, int delay) {
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


}
