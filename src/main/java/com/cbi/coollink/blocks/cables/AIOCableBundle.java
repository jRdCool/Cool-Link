package com.cbi.coollink.blocks.cables;

import com.cbi.coollink.Main;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;


public class AIOCableBundle extends Block {




    public static BooleanProperty north=BooleanProperty.of("north");
    public static BooleanProperty south=BooleanProperty.of("south");
    public static BooleanProperty east=BooleanProperty.of("east");
    public static BooleanProperty west=BooleanProperty.of("west");
    public static BooleanProperty up=BooleanProperty.of("up");
    public static BooleanProperty down=BooleanProperty.of("down");
    public static IntProperty shape=IntProperty.of("shape",1,16);

    public static final AIOCableBundle ENTRY = new AIOCableBundle(FabricBlockSettings.create().hardness(0.5f));

    public AIOCableBundle(Settings settings){
        super(settings);
        setDefaultState(getDefaultState()
                .with(north,false)
                .with(south,false)
                .with(east,false)
                .with(west,false)
                .with(up,false)
                .with(down,false)
        );
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> stateManager) {
        Main.LOGGER.info(north.toString());
        stateManager.add(north)
                .add(south)
                .add(east)
                .add(west)
                .add(up)
                .add(down);
    }


    public boolean isFacingMultiDirection(World world, BlockPos pos) {
        BlockState state=world.getBlockState(pos);
        if(state.getBlock() instanceof AIOCableBundle){
            return     ((state.get(north) && (state.get(south) || state.get(east)  || state.get(west) || state.get(up)   || state.get(down) )))
                    || ((state.get(south) && (state.get(north) || state.get(east)  || state.get(west) || state.get(up)   || state.get(down) )))
                    || ((state.get(east)  && (state.get(north) || state.get(south) || state.get(west) || state.get(up)   || state.get(down) )))
                    || ((state.get(west)  && (state.get(north) || state.get(south) || state.get(east) || state.get(up)   || state.get(down) )))
                    || ((state.get(up)    && (state.get(north) || state.get(south) || state.get(east) || state.get(west) || state.get(down) )))
                    || ((state.get(down)  && (state.get(north) || state.get(south) || state.get(east) || state.get(west) || state.get(up)   )));
        }
        return false;
    }




    @SuppressWarnings("deprecation")
    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, ShapeContext context) {
        VoxelShape shape = VoxelShapes.empty();
        if(state.get(north)){
            shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.4375, 0.4375, 0, 0.5625, 0.5625, 0.5));
            shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.375, 0.375, 0, 0.4375, 0.4375, 0.5));
            shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.5625, 0.375, 0, 0.625, 0.4375, 0.5));
        }
        if(state.get(south)){
            shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.4375, 0.4375, 0.5, 0.5625, 0.5625, 1));
            shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.5625, 0.375, 0.5, 0.625, 0.4375, 1));
            shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.375, 0.375, 0.5, 0.4375, 0.4375, 1));
        }
        if(state.get(east)){
            if(state.get(north)||state.get(south)||state.get(up)||state.get(down)){
                shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.36875, 0.36875, 0.36875, 0.63125, 0.63125, 0.63125));
            }
            shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.5, 0.4375, 0.4375, 1, 0.5625, 0.5625));
            shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.5, 0.375, 0.375, 1, 0.4375, 0.4375));
            shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.5, 0.375, 0.5625, 1, 0.4375, 0.625));
        }
        if(state.get(west)){
            if(state.get(north)||state.get(south)||state.get(up)||state.get(down)){
                shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.36875, 0.36875, 0.36875, 0.63125, 0.63125, 0.63125));
            }
            shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0, 0.4375, 0.4375, 0.5, 0.5625, 0.5625));
            shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0, 0.375, 0.5625, 0.5, 0.4375, 0.625));
            shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0, 0.375, 0.375, 0.5, 0.4375, 0.4375));
        }
        if(state.get(up)){
            if(state.get(north)||state.get(south)){
                shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.36875, 0.36875, 0.36875, 0.63125, 0.63125, 0.63125));
            }
            shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.4375, 0.5, 0.4375, 0.5625, 1, 0.5625));
            shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.375, 0.5, 0.375, 0.4375, 1, 0.4375));
            shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.5625, 0.5, 0.375, 0.625, 1, 0.4375));
        }
        if(state.get(down)){
            if(state.get(north)||state.get(south)){
                shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.36875, 0.36875, 0.36875, 0.63125, 0.63125, 0.63125));
            }
            shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.4375, 0, 0.4375, 0.5625, 0.5, 0.5625));
            shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.5625, 0, 0.375, 0.625, 0.5, 0.4375));
            shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.375, 0, 0.375, 0.4375, 0.5, 0.4375));
        }
        if(shape==VoxelShapes.empty()){
            shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.36875, 0.36875, 0.36875, 0.63125, 0.63125, 0.63125));
        }
        return shape;
    }
}
