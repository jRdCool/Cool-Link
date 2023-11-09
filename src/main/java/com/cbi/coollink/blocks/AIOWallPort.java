package com.cbi.coollink.blocks;

import com.cbi.coollink.blocks.cables.AIOCableBundle;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import static net.minecraft.state.property.Properties.FACING;

public class AIOWallPort extends Block {

    public static final AIOWallPort ENTRY = new AIOWallPort(FabricBlockSettings.create().hardness(0.5f));

    public AIOWallPort(Settings settings){
        super(settings);
    }
    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> stateManager) {
        super.appendProperties(stateManager);
        stateManager.add(FACING);
    }

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
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        editOtherBlock(world,pos,state,false);
    }

    private void editOtherBlock(World world, BlockPos pos, BlockState state,Boolean create){
        switch (state.get(FACING)){
            case NORTH -> {
                if (world.getBlockState(pos.north()).getBlock() instanceof AIOCableBundle) {//check if the neighbor block is a coax cable
                    //Main.LOGGER.info("Neighbor to north is coax");
                    world.setBlockState(pos.north(), world.getBlockState(pos.north()).with(AIOCableBundle.south, create), NOTIFY_ALL);//set the neighbor block to point to this block
                }
            }
            case SOUTH -> {
                if (world.getBlockState(pos.south()).getBlock() instanceof AIOCableBundle) {//check if the neighbor block is a coax cable
                    //Main.LOGGER.info("Neighbor to north is coax");
                    world.setBlockState(pos.south(), world.getBlockState(pos.south()).with(AIOCableBundle.north, create), NOTIFY_ALL);//set the neighbor block to point to this block
                }
            }
            case WEST -> {
                if (world.getBlockState(pos.west()).getBlock() instanceof AIOCableBundle) {//check if the neighbor block is a coax cable
                    //Main.LOGGER.info("Neighbor to north is coax");
                    world.setBlockState(pos.west(), world.getBlockState(pos.west()).with(AIOCableBundle.east, create), NOTIFY_ALL);//set the neighbor block to point to this block
                }
            }
            case EAST -> {
                if (world.getBlockState(pos.east()).getBlock() instanceof AIOCableBundle) {//check if the neighbor block is a coax cable
                    //Main.LOGGER.info("Neighbor to north is coax");
                    world.setBlockState(pos.east(), world.getBlockState(pos.east()).with(AIOCableBundle.west, create), NOTIFY_ALL);//set the neighbor block to point to this block

                }
            }
        }
    }
}
