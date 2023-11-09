package com.cbi.coollink.blocks;

import com.cbi.coollink.Main;
import com.cbi.coollink.blocks.cables.CoaxCable;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.*;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import static com.cbi.coollink.blocks.cables.CoaxCable.*;
import static net.minecraft.state.property.Properties.*;

public class CoaxWallPort extends Block {

    public static final CoaxWallPort ENTRY = new CoaxWallPort(FabricBlockSettings.create().hardness(0.5f));

    public CoaxWallPort(Settings settings){
        super(settings);
        setDefaultState(getDefaultState()
                .with(FACING,Direction.NORTH)
        );
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


    }

    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        switch (state.get(FACING)){
            case NORTH -> {
                if (world.getBlockState(pos.north()).getBlock() instanceof CoaxCable) {//check if the neighbor block is a coax cable
                    //Main.LOGGER.info("Neighbor to north is coax");
                    world.setBlockState(pos.north(), world.getBlockState(pos.north()).with(south, false), NOTIFY_ALL);//set the neighbor block to point to this block
                }
            }
            case SOUTH -> {}
        }
    }
}
