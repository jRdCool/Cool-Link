public VoxelShape makeShape(){
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