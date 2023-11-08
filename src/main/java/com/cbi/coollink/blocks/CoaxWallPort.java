package com.cbi.coollink.blocks;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import static net.minecraft.state.property.Properties.FACING;

public class CoaxWallPort extends Block {

    public static final CoaxWallPort ENTRY = new CoaxWallPort(FabricBlockSettings.create().hardness(0.5f));


    static BooleanProperty north = BooleanProperty.of("north");
    static BooleanProperty east = BooleanProperty.of("east");
    static BooleanProperty south = BooleanProperty.of("south");
    static BooleanProperty west = BooleanProperty.of("west");
    public CoaxWallPort(Settings settings){
        super(settings);
        setDefaultState(getDefaultState()
                .with(north,true)
                .with(south,false)
                .with(east,false)
                .with(west,false)
        );
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        north=BooleanProperty.of("north");

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
}
