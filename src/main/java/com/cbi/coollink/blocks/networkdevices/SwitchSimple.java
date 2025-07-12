package com.cbi.coollink.blocks.networkdevices;

import com.cbi.coollink.Main;
import com.cbi.coollink.blocks.blockentities.AIOBlockEntity;
import com.cbi.coollink.blocks.blockentities.SwitchSimpleBE;
import com.cbi.coollink.rendering.IWireNode;
import com.mojang.serialization.MapCodec;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
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
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;

import static net.minecraft.state.property.Properties.HORIZONTAL_FACING;

public class SwitchSimple extends BlockWithEntity implements BlockEntityProvider {

    public static final Identifier ID = Identifier.of(Main.namespace,"switch_simple");
    public static final RegistryKey<Block> BLOCK_KEY = Main.createBlockRegistryKey(ID);
    public static final RegistryKey<Item> ITEM_KEY = Main.createItemRegistryKey(ID);
    public static final SwitchSimple ENTRY = new SwitchSimple(AbstractBlock.Settings.create().hardness(0.5f).registryKey(BLOCK_KEY));
    public SwitchSimple(AbstractBlock.Settings settings) {
        super(settings);
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return null;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new SwitchSimpleBE(pos, state);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, ShapeContext context) {
        Direction dir = state.get(HORIZONTAL_FACING);
        //use a different hit box based on the rotation of the block
        switch(dir) {
            case WEST:
            case EAST:
                return makeShapeEW();
            case SOUTH:
            case NORTH:
            default:
                return makeShapeNS();
        }

    }

    public static VoxelShape makeShapeNS() {
        return VoxelShapes.union(
                VoxelShapes.cuboid(0.25, 0, 0.375, 0.75, 0.125, 0.625)
        );
    }
    public static VoxelShape makeShapeEW() {
        return VoxelShapes.union(
                VoxelShapes.cuboid(0.375, 0, 0.25, 0.625, 0.125, 0.75)
        );
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> stateManager) {
        stateManager.add(HORIZONTAL_FACING);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(HORIZONTAL_FACING, ctx.getHorizontalPlayerFacing());
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        // With inheriting from BlockWithEntity this defaults to INVISIBLE, so we need to change that!
        return BlockRenderType.MODEL;
    }

    @Override
    public BlockState onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        //delete both ends of the connection when the block is broken
        BlockEntity be = world.getBlockEntity(pos);
        if (be instanceof IWireNode self) {
            for (int i = 0; i < self.getNodeCount(); i++) {
                if (!self.hasConnection(i)) continue;
                BlockEntity obe = world.getBlockEntity(self.getLocalNode(i).getTargetPos());
                if (obe instanceof IWireNode other) {
                    other.removeNode(self.getOtherNodeIndex(i));
                }
            }
        }
        return super.onBreak(world, pos, state, player);
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
