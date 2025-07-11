package com.cbi.coollink.blocks.networkdevices;

import com.cbi.coollink.Main;
import com.cbi.coollink.blocks.blockentities.AIOBlockEntity;
import com.cbi.coollink.rendering.IWireNode;
import com.mojang.serialization.MapCodec;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;


import java.util.function.BiConsumer;

import static net.minecraft.state.property.Properties.HORIZONTAL_FACING;


public class AIO_Network extends BlockWithEntity implements BlockEntityProvider {
	public static final Identifier ID = Identifier.of(Main.namespace,"medium_conduit");
	public static final RegistryKey<Block> BLOCK_KEY = Main.createBlockRegistryKey(ID);
	public static final RegistryKey<Item> ITEM_KEY = Main.createItemRegistryKey(ID);
	public static final AIO_Network ENTRY = new AIO_Network(AbstractBlock.Settings.create().hardness(0.5f).registryKey(BLOCK_KEY));
	public AIO_Network(Settings settings) {
		super(settings);
	}

	@Override
	protected MapCodec<? extends BlockWithEntity> getCodec() {
		return createCodec(AIO_Network::new);
	}


	//this function is used to create the in game hit box of the block. despite the fact that this function is deprecated it still works for now
	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, ShapeContext context) {
		Direction dir = state.get(HORIZONTAL_FACING);
		//use a different hit box based on the rotation of the block
		switch(dir) {
			case SOUTH:
				return makeShapeS();
			case EAST:
				return makeShapeE();
			case WEST:
				return makeShapeW();
			case NORTH:
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
		shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.725, 0.0375, 0.875, 0.775, 0.0875, 0.925));

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
		shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.075, 0.0375, 0.725, 0.125, 0.0875, 0.775));

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
		shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.225, 0.0375, 0.075, 0.275, 0.0875, 0.125));

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
		shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.875, 0.0375, 0.225, 0.925, 0.0875, 0.275));

		return shape;
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		return this.getDefaultState().with(HORIZONTAL_FACING, ctx.getHorizontalPlayerFacing());
	}

	@Override
	public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
		return new AIOBlockEntity(pos, state);
	}

	@Override
	public BlockRenderType getRenderType(BlockState state) {
		// With inheriting from BlockWithEntity this defaults to INVISIBLE, so we need to change that!
		return BlockRenderType.MODEL;
	}
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
		return validateTicker(type, Main.AIO_BLOCK_ENTITY, AIOBlockEntity::tick);
	}

	@Override
	public BlockState onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
		//delete both ends of the connection when the block is broken
		BlockEntity be = world.getBlockEntity(pos);
		if(be instanceof IWireNode self){
			for(int i=0;i<self.getNodeCount();i++){
				if(!self.hasConnection(i)) continue;
				BlockEntity obe =  world.getBlockEntity(self.getLocalNode(i).getTargetPos());
				if(obe instanceof IWireNode other) {
					other.removeNode(self.getOtherNodeIndex(i));
				}
			}
		}
		return super.onBreak(world,pos,state,player);
	}

	@Override
	protected void onExploded(BlockState state, ServerWorld world, BlockPos pos, Explosion explosion, BiConsumer<ItemStack, BlockPos> stackMerger) {
		//delete both ends of the connection when the block is broken
		BlockEntity be = world.getBlockEntity(pos);
		if(be instanceof IWireNode self){
			for(int i=0;i<self.getNodeCount();i++){
				if(!self.hasConnection(i)) continue;
				BlockEntity obe =  world.getBlockEntity(self.getLocalNode(i).getTargetPos());
				if(obe instanceof IWireNode other) {
					other.removeNode(self.getOtherNodeIndex(i));
				}
			}
		}
		super.onExploded(state, world, pos, explosion, stackMerger);
	}
}