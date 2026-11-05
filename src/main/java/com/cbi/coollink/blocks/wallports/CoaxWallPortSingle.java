package com.cbi.coollink.blocks.wallports;

import com.cbi.coollink.blocks.blockentities.wallports.CoaxWallPortSingleBE;
import com.cbi.coollink.blocks.cables.CoaxCable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import org.jspecify.annotations.NonNull;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.*;

public class CoaxWallPortSingle extends BaseEntityBlock {
    //public static final CoaxWallPortSingle ENTRY = new CoaxWallPortSingle(AbstractBlock.Settings.create().hardness(0.5f));

    public CoaxWallPortSingle(Properties settings,String woodType){
        super(settings);
        registerDefaultState(defaultBlockState()
                .setValue(FACING, Direction.NORTH)
        );
        this.woodType=woodType;
    }

    private final String woodType;


    @Override
    protected void createBlockStateDefinition(StateDefinition.@NonNull Builder<Block, BlockState> stateManager) {
        super.createBlockStateDefinition(stateManager);
        stateManager.add(FACING);
    }

    @Override
    public @org.jspecify.annotations.Nullable BlockState getStateForPlacement(BlockPlaceContext ctx) {

        switch (ctx.getHorizontalDirection()){
            case NORTH -> {return this.defaultBlockState().setValue(FACING, Direction.SOUTH);}
            case EAST -> {return this.defaultBlockState().setValue(FACING, Direction.WEST);}
            case WEST -> {return this.defaultBlockState().setValue(FACING, Direction.EAST);}
            default -> {return this.defaultBlockState().setValue(FACING, Direction.NORTH);}
        }
    }

    @Override
    public void setPlacedBy(@NonNull Level world, @NonNull BlockPos pos, @NonNull BlockState state, @org.jspecify.annotations.Nullable LivingEntity placer, @NonNull ItemStack itemStack) {
        super.setPlacedBy(world, pos, state, placer, itemStack);
        editOtherBlock(world,pos,state,true);
    }

    @Override
    public void destroy(@NonNull LevelAccessor world, @NonNull BlockPos pos, @NonNull BlockState state) {
        editOtherBlock((Level) world,pos,state,false);
    }

    private void editOtherBlock(Level world, BlockPos pos, BlockState state,Boolean create){
        switch (state.getValue(FACING)){
            case NORTH -> {
                if (world.getBlockState(pos.north()).getBlock() instanceof CoaxCable) {//check if the neighbor block is a coax cable
                    //Main.LOGGER.info("Neighbor to north is coax");
                    world.setBlock(pos.north(), world.getBlockState(pos.north()).setValue(CoaxCable.south, create), UPDATE_ALL);//set the neighbor block to point to this block
                }
            }
            case SOUTH -> {
                if (world.getBlockState(pos.south()).getBlock() instanceof CoaxCable) {//check if the neighbor block is a coax cable
                    //Main.LOGGER.info("Neighbor to north is coax");
                    world.setBlock(pos.south(), world.getBlockState(pos.south()).setValue(CoaxCable.north, create), UPDATE_ALL);//set the neighbor block to point to this block
                }
            }
            case WEST -> {
                if (world.getBlockState(pos.west()).getBlock() instanceof CoaxCable) {//check if the neighbor block is a coax cable
                    //Main.LOGGER.info("Neighbor to north is coax");
                    world.setBlock(pos.west(), world.getBlockState(pos.west()).setValue(CoaxCable.east, create), UPDATE_ALL);//set the neighbor block to point to this block
                }
            }
            case EAST -> {
                if (world.getBlockState(pos.east()).getBlock() instanceof CoaxCable) {//check if the neighbor block is a coax cable
                    //Main.LOGGER.info("Neighbor to north is coax");
                    world.setBlock(pos.east(), world.getBlockState(pos.east()).setValue(CoaxCable.west, create), UPDATE_ALL);//set the neighbor block to point to this block
                }
            }
        }
    }

    @Override
    public @org.jspecify.annotations.Nullable BlockEntity newBlockEntity(@NonNull BlockPos pos, @NonNull BlockState state) {
        return new CoaxWallPortSingleBE(woodType,pos,state);
    }

    @Override
    protected @NonNull RenderShape getRenderShape(@NonNull BlockState state) {
        // With inheriting from BlockWithEntity this defaults to INVISIBLE, so we need to change that!
        return RenderShape.MODEL;
    }
}
