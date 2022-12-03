package com.cbi.coollink.blocks;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.block.ShapeContext;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;


import static net.minecraft.state.property.Properties.HORIZONTAL_FACING;


public class AIO_Network extends Block{
	public static final AIO_Network ENTRY = new AIO_Network(FabricBlockSettings.of(Material.STONE).hardness(0.5f));
	public AIO_Network(Settings settings) {
		super(settings);
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, ShapeContext context) {
		Direction dir = state.get(HORIZONTAL_FACING);
		switch(dir) {
			case NORTH:
				return makeShapeN();
			case SOUTH:
				return makeShapeS();
			case EAST:
				return makeShapeE();
			case WEST:
				return makeShapeW();
			default:
				return makeShapeN();
		}

	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> stateManager) {
		stateManager.add(HORIZONTAL_FACING);
	}

	public VoxelShape makeShapeN(){
		VoxelShape shape = VoxelShapes.empty();
		shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.125, 0, 0.125, 0.875, 0.125, 0.875));
		shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.0625, 0.125, 0.0625, 0.9375, 0.25, 0.9375));
		shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.21875, 0.03125, 0.75, 0.34375, 0.03125, 0.875));
		shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.21875, 0.125, 0.75, 0.34375, 0.125, 0.875));
		shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.34375, 0.03125, 0.75, 0.34375, 0.125, 0.875));
		shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.21875, 0.03125, 0.75, 0.21875, 0.125, 0.875));
		shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.21875, 0.03125, 0.75, 0.34375, 0.125, 0.75));
		shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.40625, 0.03125, 0.75, 0.53125, 0.03125, 0.875));
		shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.40625, 0.125, 0.75, 0.53125, 0.125, 0.875));
		shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.53125, 0.03125, 0.75, 0.53125, 0.125, 0.875));
		shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.40625, 0.03125, 0.75, 0.40625, 0.125, 0.875));
		shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.40625, 0.03125, 0.75, 0.53125, 0.125, 0.75));
		shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.725, 0.0375, 0.875, 0.775, 0.0875, 0.96875));

		return shape;
	}

	public VoxelShape makeShapeE(){
		VoxelShape shape = VoxelShapes.empty();
		shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.125, 0, 0.125, 0.875, 0.125, 0.875));
		shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.0625, 0.125, 0.0625, 0.9375, 0.25, 0.9375));
		shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.125, 0.03125, 0.21875, 0.25, 0.03125, 0.34375));
		shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.125, 0.125, 0.21875, 0.25, 0.125, 0.34375));
		shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.125, 0.03125, 0.34375, 0.25, 0.125, 0.34375));
		shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.125, 0.03125, 0.21875, 0.25, 0.125, 0.21875));
		shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.25, 0.03125, 0.21875, 0.25, 0.125, 0.34375));
		shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.125, 0.03125, 0.40625, 0.25, 0.03125, 0.53125));
		shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.125, 0.125, 0.40625, 0.25, 0.125, 0.53125));
		shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.125, 0.03125, 0.53125, 0.25, 0.125, 0.53125));
		shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.125, 0.03125, 0.40625, 0.25, 0.125, 0.40625));
		shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.25, 0.03125, 0.40625, 0.25, 0.125, 0.53125));
		shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.07499999999999996, 0.0375, 0.725, 0.125, 0.0875, 0.775));

		return shape;
	}

	public VoxelShape makeShapeS(){
		VoxelShape shape = VoxelShapes.empty();
		shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.125, 0, 0.125, 0.875, 0.125, 0.875));
		shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.0625, 0.125, 0.0625, 0.9375, 0.25, 0.9375));
		shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.65625, 0.03125, 0.125, 0.78125, 0.03125, 0.25));
		shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.65625, 0.125, 0.125, 0.78125, 0.125, 0.25));
		shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.65625, 0.03125, 0.125, 0.65625, 0.125, 0.25));
		shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.78125, 0.03125, 0.125, 0.78125, 0.125, 0.25));
		shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.65625, 0.03125, 0.25, 0.78125, 0.125, 0.25));
		shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.46875, 0.03125, 0.125, 0.59375, 0.03125, 0.25));
		shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.46875, 0.125, 0.125, 0.59375, 0.125, 0.25));
		shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.46875, 0.03125, 0.125, 0.46875, 0.125, 0.25));
		shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.59375, 0.03125, 0.125, 0.59375, 0.125, 0.25));
		shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.46875, 0.03125, 0.25, 0.59375, 0.125, 0.25));
		shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.22499999999999998, 0.0375, 0.07499999999999996, 0.275, 0.0875, 0.125));

		return shape;
	}

	public VoxelShape makeShapeW(){
		VoxelShape shape = VoxelShapes.empty();
		shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.125, 0, 0.125, 0.875, 0.125, 0.875));
		shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.0625, 0.125, 0.0625, 0.9375, 0.25, 0.9375));
		shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.75, 0.03125, 0.65625, 0.875, 0.03125, 0.78125));
		shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.75, 0.125, 0.65625, 0.875, 0.125, 0.78125));
		shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.75, 0.03125, 0.65625, 0.875, 0.125, 0.65625));
		shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.75, 0.03125, 0.78125, 0.875, 0.125, 0.78125));
		shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.75, 0.03125, 0.65625, 0.75, 0.125, 0.78125));
		shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.75, 0.03125, 0.46875, 0.875, 0.03125, 0.59375));
		shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.75, 0.125, 0.46875, 0.875, 0.125, 0.59375));
		shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.75, 0.03125, 0.46875, 0.875, 0.125, 0.46875));
		shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.75, 0.03125, 0.59375, 0.875, 0.125, 0.59375));
		shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.75, 0.03125, 0.46875, 0.75, 0.125, 0.59375));
		shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.875, 0.0375, 0.22499999999999998, 0.925, 0.0875, 0.275));

		return shape;
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		return (BlockState)this.getDefaultState().with(HORIZONTAL_FACING, ctx.getPlayerFacing().getOpposite());
	}
}