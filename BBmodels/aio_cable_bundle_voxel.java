public VoxelShape makeShape(){
	VoxelShape shape = VoxelShapes.empty();
	shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.4375, 0.4375, 0, 0.5625, 0.5625, 0.5));
	shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.375, 0.375, 0, 0.4375, 0.4375, 0.5));
	shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.5625, 0.375, 0, 0.625, 0.4375, 0.5));

	return shape;
}