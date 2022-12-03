public VoxelShape makeShape(){
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