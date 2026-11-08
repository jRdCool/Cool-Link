package com.cbi.coollink.blocks.networkdevices;

import com.cbi.coollink.Main;
import com.cbi.coollink.blocks.blockentities.AIOBlockEntity;
import com.cbi.coollink.rendering.IWireNode;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.NonNull;

import java.util.function.BiConsumer;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING;


public class AIO_Network extends BaseEntityBlock {
	public static final Identifier ID = Identifier.fromNamespaceAndPath(Main.namespace,"aio_network");
	public static final ResourceKey<Block> BLOCK_KEY = Main.createBlockRegistryKey(ID);
	public static final ResourceKey<Item> ITEM_KEY = Main.createItemRegistryKey(ID);
	public static final AIO_Network ENTRY = new AIO_Network(BlockBehaviour.Properties.of().strength(0.5f));

	public AIO_Network(BlockBehaviour.Properties settings) {
		super(settings.setId(BLOCK_KEY));

	}



	//this function is used to create the in game hit box of the block.
	@Override
	public @NonNull VoxelShape getShape(BlockState state, @NonNull BlockGetter level, @NonNull BlockPos pos, @NonNull CollisionContext context) {
		Direction dir = state.getValue(HORIZONTAL_FACING);
		//use a different hit box based on the rotation of the block
		return switch (dir) {
			case SOUTH -> makeShapeS();
			case EAST -> makeShapeE();
			case WEST -> makeShapeW();
			default -> makeShapeN();
		};

	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> stateManager) {
		stateManager.add(HORIZONTAL_FACING);
	}

	public VoxelShape makeShapeN(){
		VoxelShape shape = Shapes.empty();
		shape = Shapes.join(shape, Shapes.box(0.125, 0, 0.125, 0.875, 0.125, 0.875), BooleanOp.OR);
		shape = Shapes.join(shape, Shapes.box(0.0625, 0.125, 0.0625, 0.9375, 0.25, 0.9375), BooleanOp.OR);
		shape = Shapes.join(shape, Shapes.box(0.21875, 0.03125, 0.75, 0.34375, 0.03125, 0.875), BooleanOp.OR);
		shape = Shapes.join(shape, Shapes.box(0.21875, 0.125, 0.75, 0.34375, 0.125, 0.875), BooleanOp.OR);
		shape = Shapes.join(shape, Shapes.box(0.34375, 0.03125, 0.75, 0.34375, 0.125, 0.875), BooleanOp.OR);
		shape = Shapes.join(shape, Shapes.box(0.21875, 0.03125, 0.75, 0.21875, 0.125, 0.875), BooleanOp.OR);
		shape = Shapes.join(shape, Shapes.box(0.21875, 0.03125, 0.75, 0.34375, 0.125, 0.75), BooleanOp.OR);
		shape = Shapes.join(shape, Shapes.box(0.40625, 0.03125, 0.75, 0.53125, 0.03125, 0.875), BooleanOp.OR);
		shape = Shapes.join(shape, Shapes.box(0.40625, 0.125, 0.75, 0.53125, 0.125, 0.875), BooleanOp.OR);
		shape = Shapes.join(shape, Shapes.box(0.53125, 0.03125, 0.75, 0.53125, 0.125, 0.875), BooleanOp.OR);
		shape = Shapes.join(shape, Shapes.box(0.40625, 0.03125, 0.75, 0.40625, 0.125, 0.875), BooleanOp.OR);
		shape = Shapes.join(shape, Shapes.box(0.40625, 0.03125, 0.75, 0.53125, 0.125, 0.75), BooleanOp.OR);
		shape = Shapes.join(shape, Shapes.box(0.725, 0.0375, 0.875, 0.775, 0.0875, 0.925), BooleanOp.OR);

		return shape;
	}

	public VoxelShape makeShapeE(){
		VoxelShape shape = Shapes.empty();
		shape = Shapes.join(shape, Shapes.box(0.125, 0, 0.125, 0.875, 0.125, 0.875), BooleanOp.OR);
		shape = Shapes.join(shape, Shapes.box(0.0625, 0.125, 0.0625, 0.9375, 0.25, 0.9375), BooleanOp.OR);
		shape = Shapes.join(shape, Shapes.box(0.125, 0.03125, 0.21875, 0.25, 0.03125, 0.34375), BooleanOp.OR);
		shape = Shapes.join(shape, Shapes.box(0.125, 0.125, 0.21875, 0.25, 0.125, 0.34375), BooleanOp.OR);
		shape = Shapes.join(shape, Shapes.box(0.125, 0.03125, 0.34375, 0.25, 0.125, 0.34375), BooleanOp.OR);
		shape = Shapes.join(shape, Shapes.box(0.125, 0.03125, 0.21875, 0.25, 0.125, 0.21875), BooleanOp.OR);
		shape = Shapes.join(shape, Shapes.box(0.25, 0.03125, 0.21875, 0.25, 0.125, 0.34375), BooleanOp.OR);
		shape = Shapes.join(shape, Shapes.box(0.125, 0.03125, 0.40625, 0.25, 0.03125, 0.53125), BooleanOp.OR);
		shape = Shapes.join(shape, Shapes.box(0.125, 0.125, 0.40625, 0.25, 0.125, 0.53125), BooleanOp.OR);
		shape = Shapes.join(shape, Shapes.box(0.125, 0.03125, 0.53125, 0.25, 0.125, 0.53125), BooleanOp.OR);
		shape = Shapes.join(shape, Shapes.box(0.125, 0.03125, 0.40625, 0.25, 0.125, 0.40625), BooleanOp.OR);
		shape = Shapes.join(shape, Shapes.box(0.25, 0.03125, 0.40625, 0.25, 0.125, 0.53125), BooleanOp.OR);
		shape = Shapes.join(shape, Shapes.box(0.075, 0.0375, 0.725, 0.125, 0.0875, 0.775), BooleanOp.OR);

		return shape;
	}

	public VoxelShape makeShapeS(){
		VoxelShape shape = Shapes.empty();
		shape = Shapes.join(shape, Shapes.box(0.125, 0, 0.125, 0.875, 0.125, 0.875), BooleanOp.OR);
		shape = Shapes.join(shape, Shapes.box(0.0625, 0.125, 0.0625, 0.9375, 0.25, 0.9375), BooleanOp.OR);
		shape = Shapes.join(shape, Shapes.box(0.65625, 0.03125, 0.125, 0.78125, 0.03125, 0.25), BooleanOp.OR);
		shape = Shapes.join(shape, Shapes.box(0.65625, 0.125, 0.125, 0.78125, 0.125, 0.25), BooleanOp.OR);
		shape = Shapes.join(shape, Shapes.box(0.65625, 0.03125, 0.125, 0.65625, 0.125, 0.25), BooleanOp.OR);
		shape = Shapes.join(shape, Shapes.box(0.78125, 0.03125, 0.125, 0.78125, 0.125, 0.25), BooleanOp.OR);
		shape = Shapes.join(shape, Shapes.box(0.65625, 0.03125, 0.25, 0.78125, 0.125, 0.25), BooleanOp.OR);
		shape = Shapes.join(shape, Shapes.box(0.46875, 0.03125, 0.125, 0.59375, 0.03125, 0.25), BooleanOp.OR);
		shape = Shapes.join(shape, Shapes.box(0.46875, 0.125, 0.125, 0.59375, 0.125, 0.25), BooleanOp.OR);
		shape = Shapes.join(shape, Shapes.box(0.46875, 0.03125, 0.125, 0.46875, 0.125, 0.25), BooleanOp.OR);
		shape = Shapes.join(shape, Shapes.box(0.59375, 0.03125, 0.125, 0.59375, 0.125, 0.25), BooleanOp.OR);
		shape = Shapes.join(shape, Shapes.box(0.46875, 0.03125, 0.25, 0.59375, 0.125, 0.25), BooleanOp.OR);
		shape = Shapes.join(shape, Shapes.box(0.225, 0.0375, 0.075, 0.275, 0.0875, 0.125), BooleanOp.OR);

		return shape;
	}

	public VoxelShape makeShapeW(){
		VoxelShape shape = Shapes.empty();
		shape = Shapes.join(shape, Shapes.box(0.125, 0, 0.125, 0.875, 0.125, 0.875), BooleanOp.OR);
		shape = Shapes.join(shape, Shapes.box(0.0625, 0.125, 0.0625, 0.9375, 0.25, 0.9375), BooleanOp.OR);
		shape = Shapes.join(shape, Shapes.box(0.75, 0.03125, 0.65625, 0.875, 0.03125, 0.78125), BooleanOp.OR);
		shape = Shapes.join(shape, Shapes.box(0.75, 0.125, 0.65625, 0.875, 0.125, 0.78125), BooleanOp.OR);
		shape = Shapes.join(shape, Shapes.box(0.75, 0.03125, 0.65625, 0.875, 0.125, 0.65625), BooleanOp.OR);
		shape = Shapes.join(shape, Shapes.box(0.75, 0.03125, 0.78125, 0.875, 0.125, 0.78125), BooleanOp.OR);
		shape = Shapes.join(shape, Shapes.box(0.75, 0.03125, 0.65625, 0.75, 0.125, 0.78125), BooleanOp.OR);
		shape = Shapes.join(shape, Shapes.box(0.75, 0.03125, 0.46875, 0.875, 0.03125, 0.59375), BooleanOp.OR);
		shape = Shapes.join(shape, Shapes.box(0.75, 0.125, 0.46875, 0.875, 0.125, 0.59375), BooleanOp.OR);
		shape = Shapes.join(shape, Shapes.box(0.75, 0.03125, 0.46875, 0.875, 0.125, 0.46875), BooleanOp.OR);
		shape = Shapes.join(shape, Shapes.box(0.75, 0.03125, 0.59375, 0.875, 0.125, 0.59375), BooleanOp.OR);
		shape = Shapes.join(shape, Shapes.box(0.75, 0.03125, 0.46875, 0.75, 0.125, 0.59375), BooleanOp.OR);
		shape = Shapes.join(shape, Shapes.box(0.875, 0.0375, 0.225, 0.925, 0.0875, 0.275), BooleanOp.OR);

		return shape;
	}

	@Override
	public @org.jspecify.annotations.Nullable BlockState getStateForPlacement(BlockPlaceContext ctx) {
		return this.defaultBlockState().setValue(HORIZONTAL_FACING, ctx.getHorizontalDirection());
	}

	//Former Create Block entity
	@Override
	public BlockEntity newBlockEntity(@NonNull BlockPos pos, @NonNull BlockState state) {
		return new AIOBlockEntity(pos, state);
	}

	//former get render type
	@Override
	protected @NonNull RenderShape getRenderShape(@NonNull BlockState state) {
		return RenderShape.MODEL;
	}




	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NonNull Level world, @NonNull BlockState state, @NonNull BlockEntityType<T> type) {
		return createTickerHelper(type, Main.AIO_BLOCK_ENTITY, AIOBlockEntity::tick);
	}


	@Override
	public @NonNull BlockState playerWillDestroy(Level world, @NonNull BlockPos pos, @NonNull BlockState state, @NonNull Player player){
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
		return super.playerWillDestroy(world,pos,state,player);
	}



	@Override
	protected void onExplosionHit(@NonNull BlockState state, ServerLevel level, @NonNull BlockPos pos, @NonNull Explosion explosion, @NonNull BiConsumer<ItemStack, BlockPos> onHit) {
		//delete both ends of the connection when the block is broken
		BlockEntity be = level.getBlockEntity(pos);
		if(be instanceof IWireNode self){
			for(int i=0;i<self.getNodeCount();i++){
				if(!self.hasConnection(i)) continue;
				BlockEntity obe =  level.getBlockEntity(self.getLocalNode(i).getTargetPos());
				if(obe instanceof IWireNode other) {
					other.removeNode(self.getOtherNodeIndex(i));
				}
			}
		}
		super.onExplosionHit(state, level, pos, explosion, onHit);
	}
}