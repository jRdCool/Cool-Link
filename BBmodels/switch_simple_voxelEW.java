public static VoxelShape makeShape() {
	return VoxelShapes.union(
		VoxelShapes.cuboid(0.375, 0, 0.25, 0.625, 0.125, 0.75)
	);
}