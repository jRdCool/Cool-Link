package com.cbi.coollink.blocks;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.block.ShapeContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;

public class AIO_Network extends Block{
	public static final AIO_Network ENTRY = new AIO_Network(FabricBlockSettings.of(Material.STONE).hardness(0.5f));
	public AIO_Network(Settings settings) {
		super(settings);
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, ShapeContext context) {
		return makeShape();
	}

	public VoxelShape makeShape(){
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
}