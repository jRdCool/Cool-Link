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
import static net.minecraft.state.property.Properties.HORIZONTAL_FACING;


public class MediumConduit extends Block {
    public static final SmallConduit ENTRY = new SmallConduit(FabricBlockSettings.of(Material.STONE).hardness(0.5f));

    public MediumConduit(Settings settings) {
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
            case X :
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
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.625, 0, 0, 0.625, 0.125, 1));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.375, 0, 0, 0.375, 0.125, 1));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.375, 0, 0, 0.625, 0, 1));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.4925, 0.000625, 0, 0.5074, 0.07543, 1));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.4925, 0.000625, 0, 0.5074, 0.07543, 1));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.4925, 0.000625, 0, 0.5074, 0.07543, 1));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.4925, 0.000625, 0, 0.5074, 0.07543, 1));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.4925, 0.000625, 0, 0.5074, 0.07543, 1));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.4625, 0.03059, 0, 0.5374, 0.04547, 1));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.4625, 0.030595, 0, 0.5374, 0.04547, 1));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.4625, 0.030590, 0, 0.5374, 0.04547, 1));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.4177, 0.000625, 0, 0.4326, 0.07543, 1));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.4177, 0.000625, 0, 0.4326, 0.07543, 1));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.4177, 0.000625, 0, 0.4326, 0.07543, 1));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.4177, 0.000625, 0, 0.4326, 0.07543, 1));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.4177, 0.000625, 0, 0.4326, 0.07543, 1));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.3877, 0.03059, 0, 0.4625, 0.04547, 1));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.3877, 0.03059, 0, 0.4625, 0.04547, 1));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.3877, 0.03059, 0, 0.4625, 0.04547, 1));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.5673, 0.000625, 0, 0.5822, 0.07543, 1));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.5673, 0.000625, 0, 0.5822, 0.07543, 1));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.5673, 0.000625, 0, 0.5822, 0.07543, 1));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.5673, 0.000625, 0, 0.5822, 0.07543, 1));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.5673, 0.000625, 0, 0.5822, 0.07543, 1));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.5374, 0.03059, 0, 0.6122, 0.04547, 1));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.5374, 0.03059, 0, 0.6122, 0.04547, 1));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.5374, 0.03059, 0, 0.6122, 0.04547, 1));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.4551, 0.06571, 0, 0.47, 0.14053, 1));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.4551, 0.06571, 0, 0.47, 0.14053, 1));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.4551, 0.06571, 0, 0.47, 0.14053, 1));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.4551, 0.06571, 0, 0.47, 0.14053, 1));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.4551, 0.06571, 0, 0.47, 0.14053, 1));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.4251, 0.09568, 0, 0.5, 0.11056, 1));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.4251, 0.09568, 0, 0.5, 0.11056, 1));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.4251, 0.09568, 0, 0.5, 0.11056, 1));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.5299, 0.06571, 0, 0.5448, 0.14053, 1));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.5299, 0.06571, 0, 0.5448, 0.14053, 1));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.5299, 0.06571, 0, 0.5448, 0.14053, 1));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.5299, 0.06571, 0, 0.5448, 0.14053, 1));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.5299, 0.06571, 0, 0.5448, 0.14053, 1));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.5, 0.09568, 0, 0.5748, 0.11056, 1));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.5, 0.09568, 0, 0.5748, 0.11056, 1));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.5, 0.09568, 0, 0.5748, 0.11056, 1));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.375, 0, 0.75, 0.625, 0, 0.8125));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.375, 0, 0.5625, 0.625, 0, 0.625));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.375, 0, 0.375, 0.625, 0, 0.4375));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.375, 0, 0.1875, 0.625, 0, 0.25));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.375, 0, 0, 0.625, 0, 0.0625));
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
