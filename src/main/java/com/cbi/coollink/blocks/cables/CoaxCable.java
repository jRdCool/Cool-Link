package com.cbi.coollink.blocks.cables;

import com.cbi.coollink.Main;
import com.cbi.coollink.blocks.wallports.CoaxWallPortSingle;
import com.cbi.coollink.blocks.networkdevices.SatelliteDishBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.NonNull;


import static com.cbi.coollink.Main.ASSEMBLED_BOOLEAN_PROPERTY;
import static com.cbi.coollink.blocks.networkdevices.SatelliteDishBlock.MultiBlockPartStates.D1;
import static net.minecraft.world.level.block.state.properties.BlockStateProperties.*;


public class CoaxCable extends Block {
    //All property definitions MUST be declared before the entry

    public static BooleanProperty north = BooleanProperty.create("north");
    public static BooleanProperty east = BooleanProperty.create("east");
    public static BooleanProperty south = BooleanProperty.create("south");
    public static BooleanProperty west = BooleanProperty.create("west");
    static BooleanProperty up = BooleanProperty.create("up");
    static BooleanProperty down = BooleanProperty.create("down");

    public static final Identifier ID = Identifier.fromNamespaceAndPath(Main.namespace,"coax_cable");
    public static final ResourceKey<Block> BLOCK_KEY = Main.createBlockRegistryKey(ID);
    //public static final RegistryKey<Item> ITEM_KEY = Main.createItemRegistryKey(ID);

    public static final CoaxCable ENTRY = new CoaxCable(Properties.of().strength(0.5f)/*.registryKey(BLOCK_KEY)*/);



    public CoaxCable(Properties settings){
        super(settings.setId(BLOCK_KEY));
        registerDefaultState(defaultBlockState()
            .setValue(north,false)
            .setValue(south,false)
            .setValue(east,false)
            .setValue(west,false)
            .setValue(up,false)
            .setValue(down,false));
    }


    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> stateManager) {
        north = BooleanProperty.create("north");
        east = BooleanProperty.create("east");
        south = BooleanProperty.create("south");
        west = BooleanProperty.create("west");
        up = BooleanProperty.create("up");
        down = BooleanProperty.create("down");

        stateManager.add(north);
        stateManager.add(east);
        stateManager.add(south);
        stateManager.add(west);
        stateManager.add(up);
        stateManager.add(down);
    }

    @Override
    public @org.jspecify.annotations.Nullable BlockState getStateForPlacement(@NonNull BlockPlaceContext context) {
        return defaultBlockState();
    }

    @Override
    public void setPlacedBy(Level world, @NonNull BlockPos pos, @NonNull BlockState state, @org.jspecify.annotations.Nullable LivingEntity placer, @NonNull ItemStack itemStack) {

        //---------- East Neighbor
        {
            state = world.getBlockState(pos);
            if (world.getBlockState(pos.east()).getBlock() instanceof CoaxCable) {//check if the neighbor block is a coax cable
                //Main.LOGGER.info("Neighbor to east is coax");
                world.setBlock(pos, state.setValue(east, true), UPDATE_ALL);//set this block as connecting to that neighbor block
                world.setBlock(pos.east(), world.getBlockState(pos.east()).setValue(west, true), UPDATE_ALL);//set the neighbor block to point to this block
            }
            BlockState s = world.getBlockState(pos.east());
            if (world.getBlockState(pos.east()).getBlock() instanceof CoaxWallPortSingle && (s.getValue(FACING).equals(Direction.WEST))) {//check if the neighbor block is a coax cable
                //Main.LOGGER.info("Neighbor to east is coax");
                world.setBlock(pos, state.setValue(east, true), UPDATE_ALL);//set this block as connecting to that neighbor block
            }
            if(s.getBlock() instanceof SatelliteDishBlock && s.getValue(ASSEMBLED_BOOLEAN_PROPERTY) && s.getValue(SatelliteDishBlock.multiBlockPose) == D1){
                world.setBlock(pos, state.setValue(east, true), UPDATE_ALL);//set this block as connecting to that neighbor block
            }
        }

        //---------- West Neighbor
        {
            state = world.getBlockState(pos);
            if (world.getBlockState(pos.west()).getBlock() instanceof CoaxCable) {//check if the neighbor block is a coax wall port
                //Main.LOGGER.info("Neighbor to west is coax");
                world.setBlock(pos, state.setValue(west, true), UPDATE_ALL);//set this block as connecting to that neighbor block
                world.setBlock(pos.west(), world.getBlockState(pos.west()).setValue(east, true), UPDATE_ALL);//set the neighbor block to point to this block
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
            if (world.getBlockState(pos.south()).getBlock() instanceof CoaxCable) {//check if the neighbor block is a coax cable
                //Main.LOGGER.info("Neighbor to south is coax");
                world.setBlock(pos, state.setValue(south, true), UPDATE_ALL);//set this block as connecting to that neighbor block
                world.setBlock(pos.south(), world.getBlockState(pos.south()).setValue(north, true), UPDATE_ALL);//set the neighbor block to point to this block
            }
            BlockState s = world.getBlockState(pos.south());
            //Main.LOGGER.info(""+((world.getBlockState(pos.south()).getBlock() instanceof CoaxWallPort)+" | "+(s.get(FACING).equals(Direction.NORTH))));// && Objects.equals(CoaxWallPort.ENTRY.isFacing(), DirectionProperty.of("north"))));
            if (world.getBlockState(pos.south()).getBlock() instanceof CoaxWallPortSingle && (s.getValue(FACING).equals(Direction.NORTH))) {//check if the neighbor block is a coax cable
                //Main.LOGGER.info("Neighbor to east is coax port");
                world.setBlock(pos, state.setValue(south, true), UPDATE_ALL);//set this block as connecting to that neighbor block
            }
            if(s.getBlock() instanceof SatelliteDishBlock && s.getValue(ASSEMBLED_BOOLEAN_PROPERTY) && s.getValue(SatelliteDishBlock.multiBlockPose) == D1){
                world.setBlock(pos, state.setValue(south, true), UPDATE_ALL);//set this block as connecting to that neighbor block
            }
        }


        //---------- North Neighbor
        {
            state = world.getBlockState(pos);
            if (world.getBlockState(pos.north()).getBlock() instanceof CoaxCable) {//check if the neighbor block is a coax cable
                //Main.LOGGER.info("Neighbor to north is coax");
                world.setBlock(pos, state.setValue(north, true), UPDATE_ALL);//set this block as connecting to that neighbor block
                world.setBlock(pos.north(), world.getBlockState(pos.north()).setValue(south, true), UPDATE_ALL);//set the neighbor block to point to this block
            }
            BlockState s = world.getBlockState(pos.north());
            if (world.getBlockState(pos.north()).getBlock() instanceof CoaxWallPortSingle &&(s.getValue(FACING).equals(Direction.SOUTH))) {//check if the neighbor block is a coax cable
                //Main.LOGGER.info("Neighbor to east is coax");
                world.setBlock(pos, state.setValue(north, true), UPDATE_ALL);//set this block as connecting to that neighbor block
            }
        }

        //---------- Top Neighbor
        state=world.getBlockState(pos);
        if(world.getBlockState(pos.above()).getBlock() instanceof  CoaxCable){//check if the neighbor block is a coax cable
             //Main.LOGGER.info("Neighbor above is coax");
            world.setBlock(pos,state.setValue(up,true),UPDATE_ALL);//set this block as connecting to that neighbor block
            world.setBlock(pos.above(),world.getBlockState(pos.above()).setValue(down,true),UPDATE_ALL);//set the neighbor block to point to this block
        }

        //---------- Bottom Neighbor
        state=world.getBlockState(pos);
        if(world.getBlockState(pos.below()).getBlock() instanceof  CoaxCable){//check if the neighbor block is a coax cable
             //Main.LOGGER.info("Neighbor below is coax");
            world.setBlock(pos,state.setValue(down,true),UPDATE_ALL);//set this block as connecting to that neighbor block
            world.setBlock(pos.below(),world.getBlockState(pos.below()).setValue(up,true),UPDATE_ALL);//set the neighbor block to point to this block
        }

        //super.onPlaced(world, pos, state, placer, itemStack);
    }

    @Override
    public void destroy(LevelAccessor world, @NonNull BlockPos pos, @NonNull BlockState state) {

        state=world.getBlockState(pos);
        if(world.getBlockState(pos.east()).getBlock() instanceof CoaxCable){//check if the neighbor block is a coax cable
            //Main.LOGGER.info("Neighbor to east is coax");
            world.setBlock(pos,state.setValue(east,false),UPDATE_ALL);//set this block as connecting to that neighbor block
            world.setBlock(pos.east(),world.getBlockState(pos.east()).setValue(west,false),UPDATE_ALL);//set the neighbor block to point to this block
        }

        //---------- West Neighbor
        state=world.getBlockState(pos);
        if(world.getBlockState(pos.west()).getBlock() instanceof  CoaxCable){//check if the neighbor block is a coax cable
            //Main.LOGGER.info("Neighbor to west is coax");
            world.setBlock(pos,state.setValue(west,false),UPDATE_ALL);//set this block as connecting to that neighbor block
            world.setBlock(pos.west(),world.getBlockState(pos.west()).setValue(east,false),UPDATE_ALL);//set the neighbor block to point to this block
        }

        //---------- South Neighbor
        state=world.getBlockState(pos);
        if(world.getBlockState(pos.south()).getBlock() instanceof  CoaxCable){//check if the neighbor block is a coax cable
            //Main.LOGGER.info("Neighbor to south is coax");
            world.setBlock(pos,state.setValue(south,false),UPDATE_ALL);//set this block as connecting to that neighbor block
            world.setBlock(pos.south(),world.getBlockState(pos.south()).setValue(north,false),UPDATE_ALL);//set the neighbor block to point to this block
        }

        //---------- North Neighbor
        state=world.getBlockState(pos);
        if(world.getBlockState(pos.north()).getBlock() instanceof  CoaxCable){//check if the neighbor block is a coax cable
            //Main.LOGGER.info("Neighbor to north is coax");
            world.setBlock(pos,state.setValue(north,false),UPDATE_ALL);//set this block as connecting to that neighbor block
            world.setBlock(pos.north(),world.getBlockState(pos.north()).setValue(south,false),UPDATE_ALL);//set the neighbor block to point to this block
        }

        //---------- Top Neighbor
        state=world.getBlockState(pos);
        if(world.getBlockState(pos.above()).getBlock() instanceof  CoaxCable){//check if the neighbor block is a coax cable
            //Main.LOGGER.info("Neighbor above is coax");
            world.setBlock(pos,state.setValue(up,false),UPDATE_ALL);//set this block as connecting to that neighbor block
            world.setBlock(pos.above(),world.getBlockState(pos.above()).setValue(down,false),UPDATE_ALL);//set the neighbor block to point to this block
        }

        //---------- Bottom Neighbor
        state=world.getBlockState(pos);
        if(world.getBlockState(pos.below()).getBlock() instanceof  CoaxCable){//check if the neighbor block is a coax cable
            //Main.LOGGER.info("Neighbor below is coax");
            world.setBlock(pos,state.setValue(down,false),UPDATE_ALL);//set this block as connecting to that neighbor block
            world.setBlock(pos.below(),world.getBlockState(pos.below()).setValue(up,false),UPDATE_ALL);//set the neighbor block to point to this block
        }
        super.destroy(world, pos, state);
    }

    @Override
    protected @NonNull VoxelShape getShape(BlockState state, @NonNull BlockGetter view, @NonNull BlockPos pos, @NonNull CollisionContext context) {
        VoxelShape shape = Shapes.empty();
        shape = Shapes.join(shape,Shapes.box(0.4375,0.4375,0.4375,0.5625,0.5625,0.5625), BooleanOp.OR);
        if(state.getValue(north)){
            shape = Shapes.join(shape,Shapes.box(0.4375,0.4375,0,0.5625,0.5625,0.4375), BooleanOp.OR);
        }
        if(state.getValue(south)){
            shape = Shapes.join(shape,Shapes.box(0.4375,0.4375,0.5625,0.5625,0.5625,1), BooleanOp.OR);
        }
        if(state.getValue(east)){
            shape = Shapes.join(shape,Shapes.box(0.5625,0.4375,0.4375,1,0.5625,0.5625), BooleanOp.OR);
        }
        if(state.getValue(west)){
            shape = Shapes.join(shape,Shapes.box(0,0.4375,0.4375,0.4375,0.5625,0.5625), BooleanOp.OR);
        }
        if(state.getValue(up)){
            shape = Shapes.join(shape,Shapes.box(0.4375,0.5625,0.4375,0.5625,1,0.5625), BooleanOp.OR);
        }
        if(state.getValue(down)){
            shape = Shapes.join(shape,Shapes.box(0.4375,0,0.4375,0.5625,0.4375,0.5625), BooleanOp.OR);
        }
        return shape;
    }


    @Override
    protected void neighborChanged(BlockState state, Level world, BlockPos pos, Block sourceBlock, @org.jspecify.annotations.Nullable Orientation wireOrientation, boolean movedByPiston) {
        {
            BlockState s = world.getBlockState(pos.east());
            if (state.getValue(east)) {
                if (!(s.getBlock() instanceof SatelliteDishBlock && s.getValue(ASSEMBLED_BOOLEAN_PROPERTY) && s.getValue(SatelliteDishBlock.multiBlockPose) == D1) && !(s.getBlock() instanceof CoaxCable)) {
                    world.setBlock(pos, state.setValue(east, false), UPDATE_ALL);//set this block as connecting to that neighbor block
                }
            } else {
                if (s.getBlock() instanceof SatelliteDishBlock && s.getValue(ASSEMBLED_BOOLEAN_PROPERTY) && s.getValue(SatelliteDishBlock.multiBlockPose) == D1) {
                    world.setBlock(pos, state.setValue(east, true), UPDATE_ALL);//set this block as connecting to that neighbor block
                }
            }
        }
        {
            BlockState s = world.getBlockState(pos.south());
            if (state.getValue(south)) {
                if (!(s.getBlock() instanceof SatelliteDishBlock && s.getValue(ASSEMBLED_BOOLEAN_PROPERTY) && s.getValue(SatelliteDishBlock.multiBlockPose) == D1) && !(s.getBlock() instanceof CoaxCable)) {
                    world.setBlock(pos, state.setValue(south, false), UPDATE_ALL);//set this block as connecting to that neighbor block
                }
            } else {
                if (s.getBlock() instanceof SatelliteDishBlock && s.getValue(ASSEMBLED_BOOLEAN_PROPERTY) && s.getValue(SatelliteDishBlock.multiBlockPose) == D1) {
                    world.setBlock(pos, state.setValue(south, true), UPDATE_ALL);//set this block as connecting to that neighbor block
                }
            }
        }
        super.neighborChanged(state, world, pos, sourceBlock, wireOrientation , movedByPiston);
    }
}
