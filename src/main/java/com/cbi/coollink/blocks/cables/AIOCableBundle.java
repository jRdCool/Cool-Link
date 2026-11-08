package com.cbi.coollink.blocks.cables;

import com.cbi.coollink.Main;
import com.cbi.coollink.blocks.wallports.CoaxWallPortSingle;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.NonNull;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.*;


public class AIOCableBundle extends Block {

    public static BooleanProperty north=BooleanProperty.create("north");
    public static BooleanProperty south=BooleanProperty.create("south");
    public static BooleanProperty east=BooleanProperty.create("east");
    public static BooleanProperty west=BooleanProperty.create("west");
    public static BooleanProperty up=BooleanProperty.create("up");
    public static BooleanProperty down=BooleanProperty.create("down");

    public static final Identifier ID = Identifier.fromNamespaceAndPath(Main.namespace,"aio_cable_bundle");
    public static final ResourceKey<Block> BLOCK_KEY = Main.createBlockRegistryKey(ID);
    public static final ResourceKey<Item> ITEM_KEY = Main.createItemRegistryKey(ID);

    public static final AIOCableBundle ENTRY = new AIOCableBundle(Properties.of().strength(0.5f)/*.registryKey(BLOCK_KEY)*/);

    public AIOCableBundle(Properties settings){
        super(settings.setId(BLOCK_KEY));
        registerDefaultState(defaultBlockState()
                .setValue(north,false)
                .setValue(south,false)
                .setValue(east,false)
                .setValue(west,false)
                .setValue(up,false)
                .setValue(down,false)
        );
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> stateManager) {
        //Main.LOGGER.info(north.toString());
        stateManager.add(north)
                .add(south)
                .add(east)
                .add(west)
                .add(up)
                .add(down);
    }


    public boolean isFacingSingleDirection(Level world, BlockPos pos) {

        BlockState state=world.getBlockState(pos);
        if(state.getBlock() instanceof AIOCableBundle){
            return  !( ((state.getValue(north) && (state.getValue(south) || state.getValue(east)  || state.getValue(west) || state.getValue(up)   || state.getValue(down) )))
                    || ((state.getValue(south) && (state.getValue(north) || state.getValue(east)  || state.getValue(west) || state.getValue(up)   || state.getValue(down) )))
                    || ((state.getValue(east)  && (state.getValue(north) || state.getValue(south) || state.getValue(west) || state.getValue(up)   || state.getValue(down) )))
                    || ((state.getValue(west)  && (state.getValue(north) || state.getValue(south) || state.getValue(east) || state.getValue(up)   || state.getValue(down) )))
                    || ((state.getValue(up)    && (state.getValue(north) || state.getValue(south) || state.getValue(east) || state.getValue(west) || state.getValue(down) )))
                    || ((state.getValue(down)  && (state.getValue(north) || state.getValue(south) || state.getValue(east) || state.getValue(west) || state.getValue(up)   ))));
        }
        return true;
    }

    @Override
    public void setPlacedBy(@NonNull Level world, @NonNull BlockPos pos, @NonNull BlockState state, @org.jspecify.annotations.Nullable LivingEntity placer, @NonNull ItemStack itemStack) {
        {
            state = world.getBlockState(pos);
            if (world.getBlockState(pos.east()).getBlock() instanceof AIOCableBundle) {//check if the neighbor block is a coax cable
                //Main.LOGGER.info("Neighbor to east is coax");
                if(isFacingSingleDirection(world,pos.east())) {
                    world.setBlock(pos, state.setValue(east, true), UPDATE_ALL);//set this block as connecting to that neighbor block
                    world.setBlock(pos.east(), world.getBlockState(pos.east()).setValue(west, true), UPDATE_ALL);//set the neighbor block to point to this block
                }
            }
            BlockState s = world.getBlockState(pos.east());
            if (world.getBlockState(pos.east()).getBlock() instanceof CoaxWallPortSingle &&(s.getValue(FACING).equals(Direction.WEST))) {//check if the neighbor block is a coax cable
                //Main.LOGGER.info("Neighbor to east is coax");
                world.setBlock(pos, state.setValue(east, true), UPDATE_ALL);//set this block as connecting to that neighbor block
            }
        }

        //---------- West Neighbor
        {
            state = world.getBlockState(pos);
            if (world.getBlockState(pos.west()).getBlock() instanceof AIOCableBundle) {//check if the neighbor block is a coax wall port
                //Main.LOGGER.info("Neighbor to west is coax");
                if(isFacingSingleDirection(world,pos.west())) {
                    world.setBlock(pos, state.setValue(west, true), UPDATE_ALL);//set this block as connecting to that neighbor block
                    world.setBlock(pos.west(), world.getBlockState(pos.west()).setValue(east, true), UPDATE_ALL);//set the neighbor block to point to this block
                }
            }
            BlockState s = world.getBlockState(pos.west());
            if (world.getBlockState(pos.west()).getBlock() instanceof CoaxWallPortSingle &&(s.getValue(FACING).equals(Direction.EAST))) {//check if the neighbor block is a coax cable
                //Main.LOGGER.info("Neighbor to east is coax");
                world.setBlock(pos, state.setValue(west, true), UPDATE_ALL);//set this block as connecting to that neighbor block
            }
        }

        //---------- South Neighbor
        {
            state = world.getBlockState(pos);
            if (world.getBlockState(pos.south()).getBlock() instanceof AIOCableBundle) {//check if the neighbor block is a coax cable
                //Main.LOGGER.info("Neighbor to south is coax");
                if(isFacingSingleDirection(world,pos.south())) {
                    world.setBlock(pos, state.setValue(south, true), UPDATE_ALL);//set this block as connecting to that neighbor block
                    world.setBlock(pos.south(), world.getBlockState(pos.south()).setValue(north, true), UPDATE_ALL);//set the neighbor block to point to this block
                }
            }
            BlockState s = world.getBlockState(pos.south());
            //Main.LOGGER.info(""+((world.getBlockState(pos.south()).getBlock() instanceof CoaxWallPort)+" | "+(s.get(FACING).equals(Direction.NORTH))));// && Objects.equals(CoaxWallPort.ENTRY.isFacing(), DirectionProperty.of("north"))));
            if (world.getBlockState(pos.south()).getBlock() instanceof CoaxWallPortSingle && (s.getValue(FACING).equals(Direction.NORTH))) {//check if the neighbor block is a coax cable
                //Main.LOGGER.info("Neighbor to east is coax port");
                world.setBlock(pos, state.setValue(south, true), UPDATE_ALL);//set this block as connecting to that neighbor block
            }
        }


        //---------- North Neighbor
        {
            state = world.getBlockState(pos);
            if (world.getBlockState(pos.north()).getBlock() instanceof AIOCableBundle) {//check if the neighbor block is a coax cable
                //Main.LOGGER.info("Neighbor to north is coax");
                if(isFacingSingleDirection(world,pos.north())) {
                    world.setBlock(pos, state.setValue(north, true), UPDATE_ALL);//set this block as connecting to that neighbor block
                    world.setBlock(pos.north(), world.getBlockState(pos.north()).setValue(south, true), UPDATE_ALL);//set the neighbor block to point to this block
                }
            }
            BlockState s = world.getBlockState(pos.north());
            if (world.getBlockState(pos.north()).getBlock() instanceof CoaxWallPortSingle &&(s.getValue(FACING).equals(Direction.SOUTH))) {//check if the neighbor block is a coax cable
                //Main.LOGGER.info("Neighbor to east is coax");
                world.setBlock(pos, state.setValue(north, true), UPDATE_ALL);//set this block as connecting to that neighbor block
            }
        }

        //---------- Top Neighbor
        state=world.getBlockState(pos);
        if(world.getBlockState(pos.above()).getBlock() instanceof  AIOCableBundle){//check if the neighbor block is a coax cable
            //Main.LOGGER.info("Neighbor above is coax");
            if(isFacingSingleDirection(world,pos.above())) {
                world.setBlock(pos, state.setValue(up, true), UPDATE_ALL);//set this block as connecting to that neighbor block
                world.setBlock(pos.above(), world.getBlockState(pos.above()).setValue(down, true), UPDATE_ALL);//set the neighbor block to point to this block
            }
        }

        //---------- Bottom Neighbor
        state=world.getBlockState(pos);
        if(world.getBlockState(pos.below()).getBlock() instanceof  AIOCableBundle){//check if the neighbor block is a coax cable
            //Main.LOGGER.info("Neighbor below is coax");
            if(isFacingSingleDirection(world,pos.below())) {
                world.setBlock(pos, state.setValue(down, true), UPDATE_ALL);//set this block as connecting to that neighbor block
                world.setBlock(pos.below(), world.getBlockState(pos.below()).setValue(up, true), UPDATE_ALL);//set the neighbor block to point to this block
            }
        }
    }

    @Override
    protected @NonNull VoxelShape getShape(BlockState state, @NonNull BlockGetter view, @NonNull BlockPos pos, @NonNull CollisionContext context) {
        VoxelShape shape = Shapes.empty();
        if(state.getValue(north)){
            shape = Shapes.join(shape, Shapes.box(0.4375, 0.4375, 0, 0.5625, 0.5625, 0.5), BooleanOp.OR);
            shape = Shapes.join(shape, Shapes.box(0.375, 0.375, 0, 0.4375, 0.4375, 0.5),BooleanOp.OR);
            shape = Shapes.join(shape, Shapes.box(0.5625, 0.375, 0, 0.625, 0.4375, 0.5),BooleanOp.OR);
        }
        if(state.getValue(south)){
            shape = Shapes.join(shape, Shapes.box(0.4375, 0.4375, 0.5, 0.5625, 0.5625, 1),BooleanOp.OR);
            shape = Shapes.join(shape, Shapes.box(0.5625, 0.375, 0.5, 0.625, 0.4375, 1),BooleanOp.OR);
            shape = Shapes.join(shape, Shapes.box(0.375, 0.375, 0.5, 0.4375, 0.4375, 1),BooleanOp.OR);
        }
        if(state.getValue(east)){
            if(state.getValue(north)||state.getValue(south)||state.getValue(up)||state.getValue(down)){
                shape = Shapes.join(shape, Shapes.box(0.36875, 0.36875, 0.36875, 0.63125, 0.63125, 0.63125),BooleanOp.OR);
            }
            shape = Shapes.join(shape, Shapes.box(0.5, 0.4375, 0.4375, 1, 0.5625, 0.5625),BooleanOp.OR);
            shape = Shapes.join(shape, Shapes.box(0.5, 0.375, 0.375, 1, 0.4375, 0.4375),BooleanOp.OR);
            shape = Shapes.join(shape, Shapes.box(0.5, 0.375, 0.5625, 1, 0.4375, 0.625),BooleanOp.OR);
        }
        if(state.getValue(west)){
            if(state.getValue(north)||state.getValue(south)||state.getValue(up)||state.getValue(down)){
                shape = Shapes.join(shape, Shapes.box(0.36875, 0.36875, 0.36875, 0.63125, 0.63125, 0.63125),BooleanOp.OR);
            }
            shape = Shapes.join(shape, Shapes.box(0, 0.4375, 0.4375, 0.5, 0.5625, 0.5625),BooleanOp.OR);
            shape = Shapes.join(shape, Shapes.box(0, 0.375, 0.5625, 0.5, 0.4375, 0.625),BooleanOp.OR);
            shape = Shapes.join(shape, Shapes.box(0, 0.375, 0.375, 0.5, 0.4375, 0.4375),BooleanOp.OR);
        }
        if(state.getValue(up)){
            if(state.getValue(north)||state.getValue(south)){
                shape = Shapes.join(shape, Shapes.box(0.36875, 0.36875, 0.36875, 0.63125, 0.63125, 0.63125),BooleanOp.OR);
            }
            shape = Shapes.join(shape, Shapes.box(0.4375, 0.5, 0.4375, 0.5625, 1, 0.5625),BooleanOp.OR);
            shape = Shapes.join(shape, Shapes.box(0.375, 0.5, 0.375, 0.4375, 1, 0.4375),BooleanOp.OR);
            shape = Shapes.join(shape, Shapes.box(0.5625, 0.5, 0.375, 0.625, 1, 0.4375),BooleanOp.OR);
        }
        if(state.getValue(down)){
            if(state.getValue(north)||state.getValue(south)){
                shape = Shapes.join(shape, Shapes.box(0.36875, 0.36875, 0.36875, 0.63125, 0.63125, 0.63125),BooleanOp.OR);
            }
            shape = Shapes.join(shape, Shapes.box(0.4375, 0, 0.4375, 0.5625, 0.5, 0.5625),BooleanOp.OR);
            shape = Shapes.join(shape, Shapes.box(0.5625, 0, 0.375, 0.625, 0.5, 0.4375),BooleanOp.OR);
            shape = Shapes.join(shape, Shapes.box(0.375, 0, 0.375, 0.4375, 0.5, 0.4375),BooleanOp.OR);
        }
        if(shape==Shapes.empty()){
            shape = Shapes.join(shape, Shapes.box(0.36875, 0.36875, 0.36875, 0.63125, 0.63125, 0.63125),BooleanOp.OR);
        }
        return shape;
    }
}
