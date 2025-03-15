package com.cbi.coollink.blocks.conduits;

import com.cbi.coollink.Main;
import com.cbi.coollink.blocks.blockentities.ConduitBlockEntity;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import org.jetbrains.annotations.Nullable;

public class SmallConduit extends Conduit {
    public static final SmallConduit ENTRY = new SmallConduit(FabricBlockSettings.create().hardness(0.5f));


    //cableShape is an integer that is used to switch between the models
    //  0 = NS
    //  1 = EW
    //  2 = Junction Box (3 or 4 directions)
    //  3 = Vertical Transition Box
    //  4 = NE
    //  5 = SE
    //  6 = SW
    //  7 = NW

    public SmallConduit(Settings settings) {
        super(settings);
        setDefaultState(getDefaultState()
                .with(cableLevel,1)
        );
    }

    @SuppressWarnings("deprecation")
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
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.4375, 0, 0, 0.4375, 0.125, 1));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.5625, 0, 0, 0.5625, 0.125, 1));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.4375, 0, 0, 0.5625, 0, 1));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.44375, 0.000625, 0, 0.55625, 0.113125, 1));
        return shape;
    }

    public VoxelShape makeShapeEW() {
        VoxelShape shape = VoxelShapes.empty();
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0, 0, 0.5625, 1, 0.125, 0.5625));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0, 0, 0.4375, 1, 0.125, 0.4375));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0, 0, 0.4375, 1, 0, 0.5625));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0, 0.000625, 0.44375, 1, 0.113125, 0.55625));
        return shape;
    }

    public VoxelShape makeShapeNE() {
        VoxelShape shape = VoxelShapes.empty();
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.4375, 0, 0, 0.5625, 0.125, 0.5625));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.5625, 0, 0.4375, 1, 0.125, 0.5625));
        return shape;
    }

    public VoxelShape makeShapeSE() {
        VoxelShape shape = VoxelShapes.empty();
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.4375, 0, 0.4375, 1, 0.125, 0.5625));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.4375, 0, 0.5625, 0.5625, 0.125, 1));
        return shape;
    }

    public VoxelShape makeShapeSW() {
        VoxelShape shape = VoxelShapes.empty();
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.4375, 0, 0.4375, 0.5625, 0.125, 1));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0, 0, 0.4375, 0.4375, 0.125, 0.5625));
        return shape;
    }
    public VoxelShape makeShapeNW() {
        VoxelShape shape = VoxelShapes.empty();
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0, 0, 0.4375, 0.5625, 0.125, 0.5625));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.4375, 0, 0, 0.5625, 0.125, 0.4375));
        return shape;
    }



    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new ConduitBlockEntity(pos, state, Main.SMALL_CONDUIT_BLOCK_ENTITY);
    }
}