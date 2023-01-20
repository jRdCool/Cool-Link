package com.cbi.coollink.blocks;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.*;
//import net.minecraft.block.BlockWithEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;

import static net.minecraft.state.property.Properties.AXIS;
public class MediumConduit extends Block {
    public static final MediumConduit ENTRY = new MediumConduit(FabricBlockSettings.of(Material.STONE).hardness(0.5f));

    public MediumConduit(Settings settings) {
        super(settings);
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> stateManager) {
        stateManager.add(AXIS);
    }


    @SuppressWarnings({"deprecation","all"})
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
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.3125, 0, 0, 0.6875, 0.25, 1));
        return shape;
    }

    public VoxelShape makeShapeEW() {
        VoxelShape shape = VoxelShapes.empty();
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0, 0, 0.3125, 1, 0.25, 0.6875));
        return shape;
    }

    @SuppressWarnings("all")
    public VoxelShape makeShapeS() {
        VoxelShape shape = VoxelShapes.empty();
        return shape;
    }

    @SuppressWarnings("all")
    public VoxelShape makeShapeW() {
        VoxelShape shape = VoxelShapes.empty();
        return shape;
    }


    @Override
    @SuppressWarnings("all")
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
