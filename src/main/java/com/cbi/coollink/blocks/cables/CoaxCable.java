package com.cbi.coollink.blocks.cables;

import com.cbi.coollink.Main;
import com.cbi.coollink.blocks.CoaxWallPort;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.*;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;


import static net.minecraft.state.property.Properties.FACING;

public class CoaxCable extends Block {

    public static final CoaxCable ENTRY =new CoaxCable(FabricBlockSettings.create().hardness(0.5f));

    public static BooleanProperty north = BooleanProperty.of("north");
    public static BooleanProperty east = BooleanProperty.of("east");
    public static BooleanProperty south = BooleanProperty.of("south");
    public static BooleanProperty west = BooleanProperty.of("west");
    static BooleanProperty up = BooleanProperty.of("up");
    static BooleanProperty down = BooleanProperty.of("down");

    public CoaxCable(Settings settings){
        super(settings);
        setDefaultState(getDefaultState()
                .with(north,false)
                .with(south,false)
                .with(east,false)
                .with(west,false)
                .with(up,false)
                .with(down,false));
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> stateManager){
        north = BooleanProperty.of("north");
        east = BooleanProperty.of("east");
        south = BooleanProperty.of("south");
        west = BooleanProperty.of("west");
        up = BooleanProperty.of("up");
        down = BooleanProperty.of("down");

        stateManager.add(north);
        stateManager.add(east);
        stateManager.add(south);
        stateManager.add(west);
        stateManager.add(up);
        stateManager.add(down);
    }

    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState();
    }


    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        //Main.LOGGER.info("Start of on Place");
        //Main.LOGGER.info();
        //Main.LOGGER.info();

        //---------- East Neighbor
        {
            state = world.getBlockState(pos);
            if (world.getBlockState(pos.east()).getBlock() instanceof CoaxCable) {//check if the neighbor block is a coax cable
                //Main.LOGGER.info("Neighbor to east is coax");
                world.setBlockState(pos, state.with(east, true), NOTIFY_ALL);//set this block as connecting to that neighbor block
                world.setBlockState(pos.east(), world.getBlockState(pos.east()).with(west, true), NOTIFY_ALL);//set the neighbor block to point to this block
            }
            BlockState s = world.getBlockState(pos.east());
            if (world.getBlockState(pos.east()).getBlock() instanceof CoaxWallPort&&(s.get(FACING).equals(Direction.WEST))) {//check if the neighbor block is a coax cable
                //Main.LOGGER.info("Neighbor to east is coax");
                world.setBlockState(pos, state.with(east, true), NOTIFY_ALL);//set this block as connecting to that neighbor block
            }
        }

        //---------- West Neighbor
        {
            state = world.getBlockState(pos);
            if (world.getBlockState(pos.west()).getBlock() instanceof CoaxCable) {//check if the neighbor block is a coax wall port
                //Main.LOGGER.info("Neighbor to west is coax");
                world.setBlockState(pos, state.with(west, true), NOTIFY_ALL);//set this block as connecting to that neighbor block
                world.setBlockState(pos.west(), world.getBlockState(pos.west()).with(east, true), NOTIFY_ALL);//set the neighbor block to point to this block
            }
            BlockState s = world.getBlockState(pos.west());
            if (world.getBlockState(pos.west()).getBlock() instanceof CoaxWallPort&&(s.get(FACING).equals(Direction.EAST))) {//check if the neighbor block is a coax cable
                //Main.LOGGER.info("Neighbor to east is coax");
                world.setBlockState(pos, state.with(west, true), NOTIFY_ALL);//set this block as connecting to that neighbor block
            }
        }

        //---------- South Neighbor
        {
            state = world.getBlockState(pos);
            if (world.getBlockState(pos.south()).getBlock() instanceof CoaxCable) {//check if the neighbor block is a coax cable
                //Main.LOGGER.info("Neighbor to south is coax");
                world.setBlockState(pos, state.with(south, true), NOTIFY_ALL);//set this block as connecting to that neighbor block
                world.setBlockState(pos.south(), world.getBlockState(pos.south()).with(north, true), NOTIFY_ALL);//set the neighbor block to point to this block
            }
            BlockState s = world.getBlockState(pos.south());
            //Main.LOGGER.info(""+((world.getBlockState(pos.south()).getBlock() instanceof CoaxWallPort)+" | "+(s.get(FACING).equals(Direction.NORTH))));// && Objects.equals(CoaxWallPort.ENTRY.isFacing(), DirectionProperty.of("north"))));
            if (world.getBlockState(pos.south()).getBlock() instanceof CoaxWallPort && (s.get(FACING).equals(Direction.NORTH))) {//check if the neighbor block is a coax cable
                //Main.LOGGER.info("Neighbor to east is coax port");
                world.setBlockState(pos, state.with(south, true), NOTIFY_ALL);//set this block as connecting to that neighbor block
            }
        }


        //---------- North Neighbor
        {
            state = world.getBlockState(pos);
            if (world.getBlockState(pos.north()).getBlock() instanceof CoaxCable) {//check if the neighbor block is a coax cable
                //Main.LOGGER.info("Neighbor to north is coax");
                world.setBlockState(pos, state.with(north, true), NOTIFY_ALL);//set this block as connecting to that neighbor block
                world.setBlockState(pos.north(), world.getBlockState(pos.north()).with(south, true), NOTIFY_ALL);//set the neighbor block to point to this block
            }
            BlockState s = world.getBlockState(pos.north());
            if (world.getBlockState(pos.north()).getBlock() instanceof CoaxWallPort&&(s.get(FACING).equals(Direction.SOUTH))) {//check if the neighbor block is a coax cable
                //Main.LOGGER.info("Neighbor to east is coax");
                world.setBlockState(pos, state.with(north, true), NOTIFY_ALL);//set this block as connecting to that neighbor block
            }
        }

        //---------- Top Neighbor
        state=world.getBlockState(pos);
        if(world.getBlockState(pos.up()).getBlock() instanceof  CoaxCable){//check if the neighbor block is a coax cable
             //Main.LOGGER.info("Neighbor above is coax");
            world.setBlockState(pos,state.with(up,true),NOTIFY_ALL);//set this block as connecting to that neighbor block
            world.setBlockState(pos.up(),world.getBlockState(pos.up()).with(down,true),NOTIFY_ALL);//set the neighbor block to point to this block
        }

        //---------- Bottom Neighbor
        state=world.getBlockState(pos);
        if(world.getBlockState(pos.down()).getBlock() instanceof  CoaxCable){//check if the neighbor block is a coax cable
             //Main.LOGGER.info("Neighbor below is coax");
            world.setBlockState(pos,state.with(down,true),NOTIFY_ALL);//set this block as connecting to that neighbor block
            world.setBlockState(pos.down(),world.getBlockState(pos.down()).with(up,true),NOTIFY_ALL);//set the neighbor block to point to this block
        }

        //super.onPlaced(world, pos, state, placer, itemStack);
    }

    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {


        state=world.getBlockState(pos);
        if(world.getBlockState(pos.east()).getBlock() instanceof CoaxCable){//check if the neighbor block is a coax cable
            //Main.LOGGER.info("Neighbor to east is coax");
            world.setBlockState(pos,state.with(east,false),NOTIFY_ALL);//set this block as connecting to that neighbor block
            world.setBlockState(pos.east(),world.getBlockState(pos.east()).with(west,false),NOTIFY_ALL);//set the neighbor block to point to this block
        }

        //---------- West Neighbor
        state=world.getBlockState(pos);
        if(world.getBlockState(pos.west()).getBlock() instanceof  CoaxCable){//check if the neighbor block is a coax cable
            //Main.LOGGER.info("Neighbor to west is coax");
            world.setBlockState(pos,state.with(west,false),NOTIFY_ALL);//set this block as connecting to that neighbor block
            world.setBlockState(pos.west(),world.getBlockState(pos.west()).with(east,false),NOTIFY_ALL);//set the neighbor block to point to this block
        }

        //---------- South Neighbor
        state=world.getBlockState(pos);
        if(world.getBlockState(pos.south()).getBlock() instanceof  CoaxCable){//check if the neighbor block is a coax cable
            //Main.LOGGER.info("Neighbor to south is coax");
            world.setBlockState(pos,state.with(south,false),NOTIFY_ALL);//set this block as connecting to that neighbor block
            world.setBlockState(pos.south(),world.getBlockState(pos.south()).with(north,false),NOTIFY_ALL);//set the neighbor block to point to this block
        }

        //---------- North Neighbor
        state=world.getBlockState(pos);
        if(world.getBlockState(pos.north()).getBlock() instanceof  CoaxCable){//check if the neighbor block is a coax cable
            //Main.LOGGER.info("Neighbor to north is coax");
            world.setBlockState(pos,state.with(north,false),NOTIFY_ALL);//set this block as connecting to that neighbor block
            world.setBlockState(pos.north(),world.getBlockState(pos.north()).with(south,false),NOTIFY_ALL);//set the neighbor block to point to this block
        }

        //---------- Top Neighbor
        state=world.getBlockState(pos);
        if(world.getBlockState(pos.up()).getBlock() instanceof  CoaxCable){//check if the neighbor block is a coax cable
            //Main.LOGGER.info("Neighbor above is coax");
            world.setBlockState(pos,state.with(up,false),NOTIFY_ALL);//set this block as connecting to that neighbor block
            world.setBlockState(pos.up(),world.getBlockState(pos.up()).with(down,false),NOTIFY_ALL);//set the neighbor block to point to this block
        }

        //---------- Bottom Neighbor
        state=world.getBlockState(pos);
        if(world.getBlockState(pos.down()).getBlock() instanceof  CoaxCable){//check if the neighbor block is a coax cable
            //Main.LOGGER.info("Neighbor below is coax");
            world.setBlockState(pos,state.with(down,false),NOTIFY_ALL);//set this block as connecting to that neighbor block
            world.setBlockState(pos.down(),world.getBlockState(pos.down()).with(up,false),NOTIFY_ALL);//set the neighbor block to point to this block
        }
        super.onBreak(world, pos, state, player);
    }
    @SuppressWarnings("deprecation")
    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, ShapeContext context) {
        VoxelShape shape = VoxelShapes.empty();
        shape = VoxelShapes.union(shape,VoxelShapes.cuboid(0.4375,0.4375,0.4375,0.5625,0.5625,0.5625));
        if(state.get(north)){
            shape = VoxelShapes.union(shape,VoxelShapes.cuboid(0.4375,0.4375,0,0.5625,0.5625,0.4375));
        }
        if(state.get(south)){
            shape = VoxelShapes.union(shape,VoxelShapes.cuboid(0.4375,0.4375,0.5625,0.5625,0.5625,1));
        }
        if(state.get(east)){
            shape = VoxelShapes.union(shape,VoxelShapes.cuboid(0.5625,0.4375,0.4375,1,0.5625,0.5625));
        }
        if(state.get(west)){
            shape = VoxelShapes.union(shape,VoxelShapes.cuboid(0,0.4375,0.4375,0.4375,0.5625,0.5625));
        }
        if(state.get(up)){
            shape = VoxelShapes.union(shape,VoxelShapes.cuboid(0.4375,0.5625,0.4375,0.5625,1,0.5625));
        }
        if(state.get(down)){
            shape = VoxelShapes.union(shape,VoxelShapes.cuboid(0.4375,0,0.4375,0.5625,0.4375,0.5625));
        }
        return shape;
    }
}
