public VoxelShape makeShape(){
	VoxelShape shape = VoxelShapes.empty();
	shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.05625000000000002, 0, 0.9375, 0.0625, 0.25, 1));
	shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0, 0, 0.05625000000000002, 0.9375, 0.25, 0.0625));
	shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0, 0, 0.9375, 0.0625, 0.25, 0.94375));
	shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.9375, 0, 0.0625, 0.94375, 0.25, 1));
	shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.0625, 0, 0.0625, 0.9375, 0.25, 1));
	shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0, 0, 0.0625, 0.0625, 0.25, 0.9375));

	return shape;
}