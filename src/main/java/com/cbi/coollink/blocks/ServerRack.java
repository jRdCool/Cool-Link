package com.cbi.coollink.blocks;

import com.cbi.coollink.Main;
import net.minecraft.block.*;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Property;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;


public class ServerRack extends Block {
    //All property definitions MUST be declared before the entry
    static final EnumProperty<ServerRack.Half> half = EnumProperty.of("half", ServerRack.Half.class);
    static final EnumProperty<ServerRack.Direction> direction = EnumProperty.of("direction", ServerRack.Direction.class);

    public static final ServerRack ENTRY = new ServerRack(AbstractBlock.Settings.create().hardness(0.5f));

    public enum Half implements StringIdentifiable{
        TOP("top"),
        BOTTOM("bottom");

        private final String name;

        Half(String name) {
            this.name = name;
        }

        @Override
        public String asString() {
            return name;
        }
    }

    public enum Direction implements  StringIdentifiable{

        NORTH_SOUTH("north_south"),
        EAST_WEST("east_west");
        private final String name;
        Direction(String name){
            this.name=name;
        }

        public String asString(){
            return name;
        }

    }


    public ServerRack(Settings settings) {
        super(settings);
        setDefaultState(getDefaultState()
                .with(half,Half.BOTTOM)
                .with(direction,Direction.NORTH_SOUTH)
        );

    }



    protected void appendProperties(StateManager.Builder<Block, BlockState> stateManager) {
        //stateManager.add(half);
        //stateManager.add(direction);
        stateManager.add(half,direction);
    }

    @SuppressWarnings("deprecation")
    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, ShapeContext context) {
        return switch (state.get(half)) {
            case TOP -> switch (state.get(direction)) {
                case EAST_WEST -> voxelTEW();
                case NORTH_SOUTH -> voxelTNS();
            };
            case BOTTOM -> switch (state.get(direction)) {
                case EAST_WEST -> voxelBEW();
                case NORTH_SOUTH -> voxelBNS();
            };
        };


    }
static boolean presented = false;

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        if(world.getBlockState(pos.up()).isAir()) {
            world.setBlockState(new BlockPos(pos.getX(), pos.getY() + 1, pos.getZ()), state.with(half, Half.TOP));
        }else{
            if(placer!=null) {
                if(placer instanceof PlayerEntity player){
                    if(!player.isCreative()) {
                        placer.dropItem(ServerRack.ENTRY);
                    }
                }

            }
            world.setBlockState(pos,Blocks.AIR.getDefaultState());
        }
    }

    @Override
    public void onBroken(WorldAccess world, BlockPos pos, BlockState state) {
        switch (state.get(half)) {
            case TOP -> world.setBlockState(pos.down(), Blocks.AIR.getDefaultState(), NOTIFY_ALL);
            case BOTTOM -> world.setBlockState(pos.up(), Blocks.AIR.getDefaultState(), NOTIFY_ALL);
        }
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        switch (ctx.getHorizontalPlayerFacing()){
            case NORTH:
            case SOUTH:
                return this.getDefaultState().with(direction, Direction.NORTH_SOUTH);
            case EAST:
            case WEST:
                return this.getDefaultState().with(direction,Direction.EAST_WEST);
            default:
                Main.LOGGER.error("placement with non XZ direction: "+ctx.getHorizontalPlayerFacing());
        }
        return null;
    }

    public VoxelShape voxelBNS(){
        VoxelShape shape = VoxelShapes.empty();
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.984375, 0, 0, 1, 1, 1));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.015625, 0, 0, 0.984375, 0.015625, 1));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0, 0, 0, 0.015625, 1, 1));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.015625, 0.875, 0, 0.046875, 0.9375, 1));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.046875, 0.875, 0, 0.078125, 0.890625, 1));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.046875, 0.921875, 0, 0.078125, 0.9375, 1));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.953125, 0.875, 0, 0.984375, 0.9375, 1));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.921875, 0.875, 0, 0.953125, 0.890625, 1));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.921875, 0.921875, 0, 0.953125, 0.9375, 1));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.015625, 0.5625, 0, 0.046875, 0.625, 1));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.046875, 0.5625, 0, 0.078125, 0.578125, 1));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.046875, 0.609375, 0, 0.078125, 0.625, 1));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.953125, 0.5625, 0, 0.984375, 0.625, 1));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.921875, 0.5625, 0, 0.953125, 0.578125, 1));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.921875, 0.609375, 0, 0.953125, 0.625, 1));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.015625, 0.1875, 0, 0.046875, 0.25, 1));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.046875, 0.1875, 0, 0.078125, 0.203125, 1));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.046875, 0.234375, 0, 0.078125, 0.25, 1));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.953125, 0.1875, 0, 0.984375, 0.25, 1));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.921875, 0.1875, 0, 0.953125, 0.203125, 1));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.921875, 0.234375, 0, 0.953125, 0.25, 1));

        return shape;
    }

    public VoxelShape voxelBEW(){
        VoxelShape shape = VoxelShapes.empty();
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0, 0, 0.984375, 1, 1, 1));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0, 0, 0.015625, 1, 0.015625, 0.984375));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0, 0, 0, 1, 1, 0.015625));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0, 0.875, 0.015625, 1, 0.9375, 0.046875));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0, 0.875, 0.046875, 1, 0.890625, 0.078125));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0, 0.921875, 0.046875, 1, 0.9375, 0.078125));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0, 0.875, 0.953125, 1, 0.9375, 0.984375));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0, 0.875, 0.921875, 1, 0.890625, 0.953125));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0, 0.921875, 0.921875, 1, 0.9375, 0.953125));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0, 0.5625, 0.015625, 1, 0.625, 0.046875));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0, 0.5625, 0.046875, 1, 0.578125, 0.078125));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0, 0.609375, 0.046875, 1, 0.625, 0.078125));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0, 0.5625, 0.953125, 1, 0.625, 0.984375));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0, 0.5625, 0.921875, 1, 0.578125, 0.953125));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0, 0.609375, 0.921875, 1, 0.625, 0.953125));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0, 0.1875, 0.015625, 1, 0.25, 0.046875));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0, 0.1875, 0.046875, 1, 0.203125, 0.078125));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0, 0.234375, 0.046875, 1, 0.25, 0.078125));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0, 0.1875, 0.953125, 1, 0.25, 0.984375));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0, 0.1875, 0.921875, 1, 0.203125, 0.953125));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0, 0.234375, 0.921875, 1, 0.25, 0.953125));

        return shape;
    }

    public VoxelShape voxelTNS(){
        VoxelShape shape = VoxelShapes.empty();
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.015625, 0.984375, 0, 0.984375, 1, 1));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.984375, 0, 0, 1, 1, 1));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0, 0, 0, 0.015625, 1, 1));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.015625, 0.8125, 0, 0.046875, 0.875, 1));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.046875, 0.8125, 0, 0.078125, 0.828125, 1));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.046875, 0.859375, 0, 0.078125, 0.875, 1));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.953125, 0.8125, 0, 0.984375, 0.875, 1));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.921875, 0.8125, 0, 0.953125, 0.828125, 1));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.921875, 0.859375, 0, 0.953125, 0.875, 1));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.015625, 0.5, 0, 0.046875, 0.5625, 1));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.046875, 0.5, 0, 0.078125, 0.515625, 1));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.046875, 0.546875, 0, 0.078125, 0.5625, 1));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.953125, 0.5, 0, 0.984375, 0.5625, 1));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.921875, 0.5, 0, 0.953125, 0.515625, 1));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.921875, 0.546875, 0, 0.953125, 0.5625, 1));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.015625, 0.1875, 0, 0.046875, 0.25, 1));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.046875, 0.1875, 0, 0.078125, 0.203125, 1));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.046875, 0.234375, 0, 0.078125, 0.25, 1));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.953125, 0.1875, 0, 0.984375, 0.25, 1));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.921875, 0.1875, 0, 0.953125, 0.203125, 1));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.921875, 0.234375, 0, 0.953125, 0.25, 1));

        return shape;
    }

    public VoxelShape voxelTEW(){
        VoxelShape shape = VoxelShapes.empty();
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0, 0.984375, 0.015625, 1, 1, 0.984375));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0, 0, 0.984375, 1, 1, 1));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0, 0, 0, 1, 1, 0.015625));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0, 0.8125, 0.015625, 1, 0.875, 0.046875));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0, 0.8125, 0.046875, 1, 0.828125, 0.078125));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0, 0.859375, 0.046875, 1, 0.875, 0.078125));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0, 0.8125, 0.953125, 1, 0.875, 0.984375));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0, 0.8125, 0.921875, 1, 0.828125, 0.953125));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0, 0.859375, 0.921875, 1, 0.875, 0.953125));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0, 0.5, 0.015625, 1, 0.5625, 0.046875));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0, 0.5, 0.046875, 1, 0.515625, 0.078125));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0, 0.546875, 0.046875, 1, 0.5625, 0.078125));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0, 0.5, 0.953125, 1, 0.5625, 0.984375));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0, 0.5, 0.921875, 1, 0.515625, 0.953125));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0, 0.546875, 0.921875, 1, 0.5625, 0.953125));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0, 0.1875, 0.015625, 1, 0.25, 0.046875));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0, 0.1875, 0.046875, 1, 0.203125, 0.078125));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0, 0.234375, 0.046875, 1, 0.25, 0.078125));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0, 0.1875, 0.953125, 1, 0.25, 0.984375));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0, 0.1875, 0.921875, 1, 0.203125, 0.953125));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0, 0.234375, 0.921875, 1, 0.25, 0.953125));

        return shape;
    }
}
