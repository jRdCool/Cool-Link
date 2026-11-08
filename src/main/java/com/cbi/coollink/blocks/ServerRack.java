package com.cbi.coollink.blocks;

import com.cbi.coollink.Main;
import com.cbi.coollink.blocks.blockentities.ServerRackBlockEntity;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Prediction;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import java.util.function.BiConsumer;


public class ServerRack extends BaseEntityBlock/* implements BlockEntityProvider*/ {
    //All property definitions MUST be declared before the entry
    public static final EnumProperty<Half> half = EnumProperty.create("half", ServerRack.Half.class);
    public static final EnumProperty<ServerRack.Direction> direction = EnumProperty.create("direction", ServerRack.Direction.class);

    public static final Identifier ID = Identifier.fromNamespaceAndPath(Main.namespace,"server_rack");
    public static final ResourceKey<Block> BLOCK_KEY = Main.createBlockRegistryKey(ID);
    public static final ResourceKey<Item> ITEM_KEY = Main.createItemRegistryKey(ID);
    public static final ServerRack ENTRY = new ServerRack(BlockBehaviour.Properties.of().strength(0.5f)/*.registryKey(BLOCK_KEY)*/);

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NonNull BlockPos pos, @NonNull BlockState state) {
        return new ServerRackBlockEntity(pos,state);
    }


    public enum Half implements StringRepresentable {
        TOP("top"),
        BOTTOM("bottom");

        private final String name;

        Half(String name) {
            this.name = name;
        }

        @Override
        public @NonNull String getSerializedName() {
            return name;
        }
    }

    public enum Direction implements  StringRepresentable{

        NORTH_SOUTH("north_south"),
        EAST_WEST("east_west");
        private final String name;
        Direction(String name){
            this.name=name;
        }

        public @NonNull String getSerializedName(){
            return name;
        }

    }


    public ServerRack(BlockBehaviour.Properties settings) {
        super(settings.setId(BLOCK_KEY));
        registerDefaultState(defaultBlockState()
                .setValue(half,Half.BOTTOM)
                .setValue(direction,Direction.NORTH_SOUTH)
        );

    }


    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> stateManager) {
        stateManager.add(half,direction);
    }

    @Override
    protected @NonNull VoxelShape getShape(BlockState state, @NonNull BlockGetter level, @NonNull BlockPos pos, @NonNull CollisionContext context) {
        return switch (state.getValue(half)) {
            case TOP -> switch (state.getValue(direction)) {
                case EAST_WEST -> voxelTEW();
                case NORTH_SOUTH -> voxelTNS();
            };
            case BOTTOM -> switch (state.getValue(direction)) {
                case EAST_WEST -> voxelBEW();
                case NORTH_SOUTH -> voxelBNS();
            };
        };
    }

    static boolean presented = false;

    //hopefully this is the correct new funcion
    @Override
    public void setPlacedBy(Level world, BlockPos pos, @NonNull BlockState state, @org.jspecify.annotations.Nullable LivingEntity placer, @NonNull ItemStack itemStack) {
        if(world.getBlockState(pos.above()).isAir()) {
            world.setBlockAndUpdate(new BlockPos(pos.getX(), pos.getY() + 1, pos.getZ()), state.setValue(half, Half.TOP));
        }else{
            if(placer!=null) {
                if(placer instanceof Player player){
                    if(!player.isCreative()) {
                        player.drop(new ItemStack(ServerRack.ENTRY),true, Prediction.PREDICTED);
                    }
                }

            }
            world.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
        }
    }

//    @Override
//    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
//        if(world.getBlockState(pos.up()).isAir()) {
//            world.setBlockState(new BlockPos(pos.getX(), pos.getY() + 1, pos.getZ()), state.with(half, Half.TOP));
//        }else{
//            if(placer!=null) {
//                if(placer instanceof PlayerEntity player){
//                    if(!player.isCreative()) {
//                        player.dropItem(new ItemStack(ServerRack.ENTRY),true);
//                    }
//                }
//
//            }
//            world.setBlockState(pos,Blocks.AIR.getDefaultState());
//        }
//    }


    @Override
    public void destroy(LevelAccessor world, BlockPos pos, BlockState state) {
        switch (state.getValue(half)) {
            case TOP -> world.setBlock(pos.below(), Blocks.AIR.defaultBlockState(), UPDATE_ALL);
            case BOTTOM -> world.setBlock(pos.above(), Blocks.AIR.defaultBlockState(), UPDATE_ALL);
        }
    }

    @Override
    protected void onExplosionHit(BlockState state, ServerLevel level, BlockPos pos, Explosion explosion, BiConsumer<ItemStack, BlockPos> onHit) {
        super.onExplosionHit(state, level, pos, explosion, onHit);
        destroy(level,pos,state);
    }


    @Override
    public @org.jspecify.annotations.Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        switch (context.getHorizontalDirection()){
            case NORTH:
            case SOUTH:
                return this.defaultBlockState().setValue(direction, Direction.NORTH_SOUTH);
            case EAST:
            case WEST:
                return this.defaultBlockState().setValue(direction,Direction.EAST_WEST);
            default:
                Main.LOGGER.error("placement with non XZ direction: "+context.getHorizontalDirection());
        }
        return null;
    }


    public VoxelShape voxelBNS(){
        VoxelShape shape = Shapes.empty();
        shape = Shapes.join(shape, Shapes.box(0.984375, 0, 0, 1, 1, 1), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.015625, 0, 0, 0.984375, 0.015625, 1), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0, 0, 0, 0.015625, 1, 1), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.015625, 0.875, 0, 0.046875, 0.9375, 1), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.046875, 0.875, 0, 0.078125, 0.890625, 1), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.046875, 0.921875, 0, 0.078125, 0.9375, 1), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.953125, 0.875, 0, 0.984375, 0.9375, 1), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.921875, 0.875, 0, 0.953125, 0.890625, 1), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.921875, 0.921875, 0, 0.953125, 0.9375, 1), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.015625, 0.5625, 0, 0.046875, 0.625, 1), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.046875, 0.5625, 0, 0.078125, 0.578125, 1), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.046875, 0.609375, 0, 0.078125, 0.625, 1), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.953125, 0.5625, 0, 0.984375, 0.625, 1), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.921875, 0.5625, 0, 0.953125, 0.578125, 1), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.921875, 0.609375, 0, 0.953125, 0.625, 1), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.015625, 0.1875, 0, 0.046875, 0.25, 1), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.046875, 0.1875, 0, 0.078125, 0.203125, 1), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.046875, 0.234375, 0, 0.078125, 0.25, 1), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.953125, 0.1875, 0, 0.984375, 0.25, 1), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.921875, 0.1875, 0, 0.953125, 0.203125, 1), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.921875, 0.234375, 0, 0.953125, 0.25, 1), BooleanOp.OR);

        return shape;
    }

    public VoxelShape voxelBEW(){
        VoxelShape shape = Shapes.empty();
        shape = Shapes.join(shape, Shapes.box(0, 0, 0.984375, 1, 1, 1),BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0, 0, 0.015625, 1, 0.015625, 0.984375),BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0, 0, 0, 1, 1, 0.015625),BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0, 0.875, 0.015625, 1, 0.9375, 0.046875),BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0, 0.875, 0.046875, 1, 0.890625, 0.078125),BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0, 0.921875, 0.046875, 1, 0.9375, 0.078125),BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0, 0.875, 0.953125, 1, 0.9375, 0.984375),BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0, 0.875, 0.921875, 1, 0.890625, 0.953125),BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0, 0.921875, 0.921875, 1, 0.9375, 0.953125),BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0, 0.5625, 0.015625, 1, 0.625, 0.046875),BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0, 0.5625, 0.046875, 1, 0.578125, 0.078125),BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0, 0.609375, 0.046875, 1, 0.625, 0.078125),BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0, 0.5625, 0.953125, 1, 0.625, 0.984375),BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0, 0.5625, 0.921875, 1, 0.578125, 0.953125),BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0, 0.609375, 0.921875, 1, 0.625, 0.953125),BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0, 0.1875, 0.015625, 1, 0.25, 0.046875),BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0, 0.1875, 0.046875, 1, 0.203125, 0.078125),BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0, 0.234375, 0.046875, 1, 0.25, 0.078125),BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0, 0.1875, 0.953125, 1, 0.25, 0.984375),BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0, 0.1875, 0.921875, 1, 0.203125, 0.953125),BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0, 0.234375, 0.921875, 1, 0.25, 0.953125),BooleanOp.OR);

        return shape;
    }

    public VoxelShape voxelTNS(){
        VoxelShape shape = Shapes.empty();
        shape = Shapes.join(shape, Shapes.box(0.015625, 0.984375, 0, 0.984375, 1, 1),BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.984375, 0, 0, 1, 1, 1),BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0, 0, 0, 0.015625, 1, 1),BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.015625, 0.8125, 0, 0.046875, 0.875, 1),BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.046875, 0.8125, 0, 0.078125, 0.828125, 1),BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.046875, 0.859375, 0, 0.078125, 0.875, 1),BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.953125, 0.8125, 0, 0.984375, 0.875, 1),BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.921875, 0.8125, 0, 0.953125, 0.828125, 1),BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.921875, 0.859375, 0, 0.953125, 0.875, 1),BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.015625, 0.5, 0, 0.046875, 0.5625, 1),BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.046875, 0.5, 0, 0.078125, 0.515625, 1),BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.046875, 0.546875, 0, 0.078125, 0.5625, 1),BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.953125, 0.5, 0, 0.984375, 0.5625, 1),BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.921875, 0.5, 0, 0.953125, 0.515625, 1),BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.921875, 0.546875, 0, 0.953125, 0.5625, 1),BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.015625, 0.1875, 0, 0.046875, 0.25, 1),BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.046875, 0.1875, 0, 0.078125, 0.203125, 1),BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.046875, 0.234375, 0, 0.078125, 0.25, 1),BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.953125, 0.1875, 0, 0.984375, 0.25, 1),BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.921875, 0.1875, 0, 0.953125, 0.203125, 1),BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.921875, 0.234375, 0, 0.953125, 0.25, 1),BooleanOp.OR);

        return shape;
    }

    public VoxelShape voxelTEW(){
        VoxelShape shape = Shapes.empty();
        shape = Shapes.join(shape, Shapes.box(0, 0.984375, 0.015625, 1, 1, 0.984375),BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0, 0, 0.984375, 1, 1, 1),BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0, 0, 0, 1, 1, 0.015625),BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0, 0.8125, 0.015625, 1, 0.875, 0.046875),BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0, 0.8125, 0.046875, 1, 0.828125, 0.078125),BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0, 0.859375, 0.046875, 1, 0.875, 0.078125),BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0, 0.8125, 0.953125, 1, 0.875, 0.984375),BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0, 0.8125, 0.921875, 1, 0.828125, 0.953125),BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0, 0.859375, 0.921875, 1, 0.875, 0.953125),BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0, 0.5, 0.015625, 1, 0.5625, 0.046875),BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0, 0.5, 0.046875, 1, 0.515625, 0.078125),BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0, 0.546875, 0.046875, 1, 0.5625, 0.078125),BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0, 0.5, 0.953125, 1, 0.5625, 0.984375),BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0, 0.5, 0.921875, 1, 0.515625, 0.953125),BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0, 0.546875, 0.921875, 1, 0.5625, 0.953125),BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0, 0.1875, 0.015625, 1, 0.25, 0.046875),BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0, 0.1875, 0.046875, 1, 0.203125, 0.078125),BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0, 0.234375, 0.046875, 1, 0.25, 0.078125),BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0, 0.1875, 0.953125, 1, 0.25, 0.984375),BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0, 0.1875, 0.921875, 1, 0.203125, 0.953125),BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0, 0.234375, 0.921875, 1, 0.25, 0.953125),BooleanOp.OR);

        return shape;
    }

    @Override
    protected @NonNull RenderShape getRenderShape(@NonNull BlockState state) {
        return RenderShape.MODEL;
    }
}
