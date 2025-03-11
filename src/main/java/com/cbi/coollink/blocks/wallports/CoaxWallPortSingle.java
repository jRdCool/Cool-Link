package com.cbi.coollink.blocks.wallports;

import com.cbi.coollink.blocks.blockentities.ServerRackBlockEntity;
import com.cbi.coollink.blocks.blockentities.wallports.CoaxWallPortSingleBE;
import com.cbi.coollink.blocks.cables.CoaxCable;
import com.cbi.coollink.blocks.networkdevices.AIO_Network;
import com.mojang.serialization.MapCodec;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import static net.minecraft.state.property.Properties.*;

public class CoaxWallPortSingle extends BlockWithEntity implements BlockEntityProvider{
    //public static final CoaxWallPortSingle ENTRY = new CoaxWallPortSingle(AbstractBlock.Settings.create().hardness(0.5f));

    public CoaxWallPortSingle(Settings settings,String woodType){
        super(settings);
        setDefaultState(getDefaultState()
                .with(FACING,Direction.NORTH)
        );
        this.woodType=woodType;
    }

    private final String woodType;

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return getCodec();
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> stateManager) {
        super.appendProperties(stateManager);
        stateManager.add(FACING);
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        switch (ctx.getHorizontalPlayerFacing()){
            case NORTH -> {return this.getDefaultState().with(FACING, Direction.SOUTH);}
            case EAST -> {return this.getDefaultState().with(FACING, Direction.WEST);}
            case WEST -> {return this.getDefaultState().with(FACING, Direction.EAST);}
            default -> {return this.getDefaultState().with(FACING, Direction.NORTH);}
        }
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        super.onPlaced(world, pos, state, placer, itemStack);
        editOtherBlock(world,pos,state,true);
    }

    @Override
    public BlockState onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        editOtherBlock(world,pos,state,false);
        return state;
    }

    private void editOtherBlock(World world, BlockPos pos, BlockState state,Boolean create){
        switch (state.get(FACING)){
            case NORTH -> {
                if (world.getBlockState(pos.north()).getBlock() instanceof CoaxCable) {//check if the neighbor block is a coax cable
                    //Main.LOGGER.info("Neighbor to north is coax");
                    world.setBlockState(pos.north(), world.getBlockState(pos.north()).with(CoaxCable.south, create), NOTIFY_ALL);//set the neighbor block to point to this block
                }
            }
            case SOUTH -> {
                if (world.getBlockState(pos.south()).getBlock() instanceof CoaxCable) {//check if the neighbor block is a coax cable
                    //Main.LOGGER.info("Neighbor to north is coax");
                    world.setBlockState(pos.south(), world.getBlockState(pos.south()).with(CoaxCable.north, create), NOTIFY_ALL);//set the neighbor block to point to this block
                }
            }
            case WEST -> {
                if (world.getBlockState(pos.west()).getBlock() instanceof CoaxCable) {//check if the neighbor block is a coax cable
                    //Main.LOGGER.info("Neighbor to north is coax");
                    world.setBlockState(pos.west(), world.getBlockState(pos.west()).with(CoaxCable.east, create), NOTIFY_ALL);//set the neighbor block to point to this block
                }
            }
            case EAST -> {
                if (world.getBlockState(pos.east()).getBlock() instanceof CoaxCable) {//check if the neighbor block is a coax cable
                    //Main.LOGGER.info("Neighbor to north is coax");
                    world.setBlockState(pos.east(), world.getBlockState(pos.east()).with(CoaxCable.west, create), NOTIFY_ALL);//set the neighbor block to point to this block
                }
            }
        }
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new CoaxWallPortSingleBE(woodType,pos,state);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        // With inheriting from BlockWithEntity this defaults to INVISIBLE, so we need to change that!
        return BlockRenderType.MODEL;
    }
}
