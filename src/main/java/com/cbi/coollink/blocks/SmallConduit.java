package com.cbi.coollink.blocks;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
//import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.block.ShapeContext;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;

import static net.minecraft.state.property.Properties.AXIS;
import static net.minecraft.state.property.Properties.HORIZONTAL_FACING;


public class SmallConduit extends Block {
    public static final SmallConduit ENTRY = new SmallConduit(FabricBlockSettings.of(Material.STONE).hardness(0.5f));

    public SmallConduit(Settings settings) {
        super(settings);
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> stateManager) {
        stateManager.add(AXIS);
    }

    @SuppressWarnings("deprecation")
    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, ShapeContext context) {
        Direction.Axis dir = state.get(AXIS);
        //use a different hit box based on the rotation of the block
        switch (dir) {
            case Z :
                return makeShapeNS();
            default:
                return makeShapeEW();

            /*case SOUTH:
                return makeShapeS();
            case EAST:
                return makeShapeE();
            case WEST:
                return makeShapeW();
            case NORTH:
            default:
                return makeShapeN();*/
        }

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

    public VoxelShape makeShapeS() {
        VoxelShape shape = VoxelShapes.empty();
        return shape;
    }

    public VoxelShape makeShapeW() {
        VoxelShape shape = VoxelShapes.empty();
        return shape;
    }


    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        Direction.Axis facing;
        switch (ctx.getPlayerFacing()){
            case NORTH:
            case SOUTH:
                return this.getDefaultState().with(AXIS, Direction.Axis.Z);
            case EAST:
            default:
                return this.getDefaultState().with(AXIS, Direction.Axis.X);
        }
    }
}