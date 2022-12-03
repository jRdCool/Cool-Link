public VoxelShape makeShape(){
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