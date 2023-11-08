package com.cbi.coollink.blocks.cables;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;

public class CoaxCable extends Block {

    public static final CoaxCable ENTRY =new CoaxCable(FabricBlockSettings.create().hardness(0.5f));

    static BooleanProperty north = BooleanProperty.of("north");
    static BooleanProperty east = BooleanProperty.of("east");
    static BooleanProperty south = BooleanProperty.of("south");
    static BooleanProperty west = BooleanProperty.of("west");
    static BooleanProperty up = BooleanProperty.of("up");
    static BooleanProperty down = BooleanProperty.of("down");

    public CoaxCable(Settings settings){
        super(settings);
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> stateManager){
        north = BooleanProperty.of("north");
        east = BooleanProperty.of("east");
        south = BooleanProperty.of("south");
        west = BooleanProperty.of("west");
        up = BooleanProperty.of("up");
        down = BooleanProperty.of("down");

        stateManager.add(north);
        stateManager.add(east);
        stateManager.add(south);
        stateManager.add(west);
        stateManager.add(up);
        stateManager.add(down);

    }
    @SuppressWarnings("deprecation")
    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, ShapeContext context) {
        VoxelShape shape = VoxelShapes.empty();
        shape = VoxelShapes.union(shape,VoxelShapes.cuboid(0.4375,0.4375,0.4375,0.5625,0.5625,0.5625));
        if(state.get(north)){
            shape = VoxelShapes.union(shape,VoxelShapes.cuboid(0.4375,0.4375,0,0.5625,0.5625,0.4375));
        }
        if(state.get(south)){
            shape = VoxelShapes.union(shape,VoxelShapes.cuboid(0.4375,0.4375,0.5625,0.5625,0.5625,1));
        }
        if(state.get(east)){
            shape = VoxelShapes.union(shape,VoxelShapes.cuboid(0.5625,0.4375,0.4375,1,0.5625,0.5625));
        }
        if(state.get(west)){
            shape = VoxelShapes.union(shape,VoxelShapes.cuboid(0,0.4375,0.4375,0.4375,0.5625,0.5625));
        }
        if(state.get(up)){
            shape = VoxelShapes.union(shape,VoxelShapes.cuboid(0.4375,0.5625,0.4375,0.5625,1,0.5625));
        }
        if(state.get(down)){
            shape = VoxelShapes.union(shape,VoxelShapes.cuboid(0.4375,0,0.4375,0.5625,0.4375,0.5625));
        }
        return shape;
    }
}
