public static VoxelShape makeShape() {
	return VoxelShapes.union(
		VoxelShapes.cuboid(0.25, 0, 0.375, 0.75, 0.125, 0.625)
	);
}