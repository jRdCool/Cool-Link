package com.cbi.coollink.blocks;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.*;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;


public class MediumConduit extends Conduit {

    public static final MediumConduit ENTRY = new MediumConduit(FabricBlockSettings.of(Material.STONE).hardness(0.5f));


    //cableShape is an integer that is used to switch between the models
    //  0 = NS
    //  1 = EW
    //  2 = Junction Box (3 or 4 directions)
    //  3 = Vertical Transition Box
    //  4 = NE
    //  5 = SE
    //  6 = SW
    //  7 = NW


    public MediumConduit(Settings settings) {
        super(settings);
        setDefaultState(getDefaultState().with(cableLevel,2));
    }



    @SuppressWarnings({"deprecation","all"})
    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, ShapeContext context) {
        VoxelShape shape= VoxelShapes.empty();
        //use a different hit box based on the rotation of the block
        if(state.get(cableShape)==0){
            shape=VoxelShapes.union(shape,makeShapeNS());
        }
        if(state.get(cableShape)==1){
            shape=VoxelShapes.union(shape,makeShapeEW());
        }
        if(state.get(cableShape)==2){
            shape=junctionBoxVoxel();
        }
        if(state.get(cableShape)==4){
            shape=VoxelShapes.union(shape,makeShapeNE());
        }
        if(state.get(cableShape)==5){
            shape=VoxelShapes.union(shape,makeShapeSE());
        }
        if(state.get(cableShape)==6){
            shape=VoxelShapes.union(shape,makeShapeSW());
        }
        if(state.get(cableShape)==7){
            shape=VoxelShapes.union(shape,makeShapeNW());
        }
        if (shape.isEmpty())
            shape=makeShapeNS();
        return shape;
    }

    public VoxelShape makeShapeNS() {
        VoxelShape shape = VoxelShapes.empty();
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.3125, 0, 0, 0.6875, 0.25, 1));
        return shape;
    }

    public VoxelShape makeShapeEW() {
        VoxelShape shape = VoxelShapes.empty();
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0, 0, 0.3125, 1, 0.25, 0.6875));
        return shape;
    }


    public VoxelShape makeShapeNE() {
        VoxelShape shape = VoxelShapes.empty();
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.3125, 0, 0, 0.6875, 0.25, 0.6875));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.6875, 0, 0.3125, 1, 0.25, 0.6875));
        return shape;
    }


    public VoxelShape makeShapeSE() {
        VoxelShape shape = VoxelShapes.empty();
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.3125, 0, 0.3125, 1, 0.25, 0.6875));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.3125, 0, 0.6875, 0.6875, 0.25, 1));
        return shape;
    }

    public VoxelShape makeShapeSW() {
        VoxelShape shape = VoxelShapes.empty();
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.3125, 0, 0.3125, 0.6875, 0.25, 1));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0, 0, 0.3125, 0.3125, 0.25, 0.6875));
        return shape;
    }

    public VoxelShape makeShapeNW() {
        VoxelShape shape = VoxelShapes.empty();
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0, 0, 0.3125, 0.6875, 0.25, 0.6875));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.3125, 0, 0, 0.6875, 0.25, 0.3125));
        return shape;
    }

}