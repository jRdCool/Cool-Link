public VoxelShape makeShape(){
	VoxelShape shape = VoxelShapes.empty();
	shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.0625, 0, 0, 0.9375, 0.25, 1));

	return shape;
}