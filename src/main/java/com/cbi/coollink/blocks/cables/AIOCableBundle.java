package com.cbi.coollink.blocks.cables;

import com.cbi.coollink.Main;
import com.cbi.coollink.blocks.wallports.CoaxWallPortSingle;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import static net.minecraft.state.property.Properties.FACING;


public class AIOCableBundle extends Block {




    public static BooleanProperty north=BooleanProperty.of("north");
    public static BooleanProperty south=BooleanProperty.of("south");
    public static BooleanProperty east=BooleanProperty.of("east");
    public static BooleanProperty west=BooleanProperty.of("west");
    public static BooleanProperty up=BooleanProperty.of("up");
    public static BooleanProperty down=BooleanProperty.of("down");
    public static DirectionProperty wallPortDir=DirectionProperty.of("wall_port_dir");

    public static final AIOCableBundle ENTRY = new AIOCableBundle(FabricBlockSettings.create().hardness(0.5f));

    public AIOCableBundle(Settings settings){
        super(settings);
        setDefaultState(getDefaultState()
                .with(north,false)
                .with(south,false)
                .with(east,false)
                .with(west,false)
                .with(up,false)
                .with(down,false)
        );
        Main.LOGGER.info("AIOCableBundle loaded");
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> stateManager) {
        //Main.LOGGER.info(north.toString());
        stateManager.add(north)
                .add(south)
                .add(east)
                .add(west)
                .add(up)
                .add(down);
    }


    public boolean isFacingSingleDirection(World world, BlockPos pos) {
        //Main.LOGGER.info("Entered isFacingMultiDirection");
        BlockState state=world.getBlockState(pos);
        //Main.LOGGER.info("North: "+state.get(north)+" South:"+state.get(south)+" East:"+state.get(east)+" West:"+state.get(west)+" Up:"+state.get(up)+" Down:"+state.get(down));
        //Main.LOGGER.info("exit isFacingMultiDirection");
        if(state.getBlock() instanceof AIOCableBundle){
            return  !( ((state.get(north) && (state.get(south) || state.get(east)  || state.get(west) || state.get(up)   || state.get(down) )))
                    || ((state.get(south) && (state.get(north) || state.get(east)  || state.get(west) || state.get(up)   || state.get(down) )))
                    || ((state.get(east)  && (state.get(north) || state.get(south) || state.get(west) || state.get(up)   || state.get(down) )))
                    || ((state.get(west)  && (state.get(north) || state.get(south) || state.get(east) || state.get(up)   || state.get(down) )))
                    || ((state.get(up)    && (state.get(north) || state.get(south) || state.get(east) || state.get(west) || state.get(down) )))
                    || ((state.get(down)  && (state.get(north) || state.get(south) || state.get(east) || state.get(west) || state.get(up)   ))));
        }
        return true;
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        {
            state = world.getBlockState(pos);
            if (world.getBlockState(pos.east()).getBlock() instanceof AIOCableBundle) {//check if the neighbor block is a coax cable
                //Main.LOGGER.info("Neighbor to east is coax");
                if(isFacingSingleDirection(world,pos.east())) {
                    world.setBlockState(pos, state.with(east, true), NOTIFY_ALL);//set this block as connecting to that neighbor block
                    world.setBlockState(pos.east(), world.getBlockState(pos.east()).with(west, true), NOTIFY_ALL);//set the neighbor block to point to this block
                }
            }
            BlockState s = world.getBlockState(pos.east());
            if (world.getBlockState(pos.east()).getBlock() instanceof CoaxWallPortSingle &&(s.get(FACING).equals(Direction.WEST))) {//check if the neighbor block is a coax cable
                //Main.LOGGER.info("Neighbor to east is coax");
                world.setBlockState(pos, state.with(east, true), NOTIFY_ALL);//set this block as connecting to that neighbor block
            }
        }

        //---------- West Neighbor
        {
            state = world.getBlockState(pos);
            if (world.getBlockState(pos.west()).getBlock() instanceof AIOCableBundle) {//check if the neighbor block is a coax wall port
                //Main.LOGGER.info("Neighbor to west is coax");
                if(isFacingSingleDirection(world,pos.west())) {
                    world.setBlockState(pos, state.with(west, true), NOTIFY_ALL);//set this block as connecting to that neighbor block
                    world.setBlockState(pos.west(), world.getBlockState(pos.west()).with(east, true), NOTIFY_ALL);//set the neighbor block to point to this block
                }
            }
            BlockState s = world.getBlockState(pos.west());
            if (world.getBlockState(pos.west()).getBlock() instanceof CoaxWallPortSingle &&(s.get(FACING).equals(Direction.EAST))) {//check if the neighbor block is a coax cable
                //Main.LOGGER.info("Neighbor to east is coax");
                world.setBlockState(pos, state.with(west, true), NOTIFY_ALL);//set this block as connecting to that neighbor block
            }
        }

        //---------- South Neighbor
        {
            state = world.getBlockState(pos);
            if (world.getBlockState(pos.south()).getBlock() instanceof AIOCableBundle) {//check if the neighbor block is a coax cable
                //Main.LOGGER.info("Neighbor to south is coax");
                if(isFacingSingleDirection(world,pos.south())) {
                    world.setBlockState(pos, state.with(south, true), NOTIFY_ALL);//set this block as connecting to that neighbor block
                    world.setBlockState(pos.south(), world.getBlockState(pos.south()).with(north, true), NOTIFY_ALL);//set the neighbor block to point to this block
                }
            }
            BlockState s = world.getBlockState(pos.south());
            //Main.LOGGER.info(""+((world.getBlockState(pos.south()).getBlock() instanceof CoaxWallPort)+" | "+(s.get(FACING).equals(Direction.NORTH))));// && Objects.equals(CoaxWallPort.ENTRY.isFacing(), DirectionProperty.of("north"))));
            if (world.getBlockState(pos.south()).getBlock() instanceof CoaxWallPortSingle && (s.get(FACING).equals(Direction.NORTH))) {//check if the neighbor block is a coax cable
                //Main.LOGGER.info("Neighbor to east is coax port");
                world.setBlockState(pos, state.with(south, true), NOTIFY_ALL);//set this block as connecting to that neighbor block
            }
        }


        //---------- North Neighbor
        {
            state = world.getBlockState(pos);
            if (world.getBlockState(pos.north()).getBlock() instanceof AIOCableBundle) {//check if the neighbor block is a coax cable
                //Main.LOGGER.info("Neighbor to north is coax");
                if(isFacingSingleDirection(world,pos.north())) {
                    world.setBlockState(pos, state.with(north, true), NOTIFY_ALL);//set this block as connecting to that neighbor block
                    world.setBlockState(pos.north(), world.getBlockState(pos.north()).with(south, true), NOTIFY_ALL);//set the neighbor block to point to this block
                }
            }
            BlockState s = world.getBlockState(pos.north());
            if (world.getBlockState(pos.north()).getBlock() instanceof CoaxWallPortSingle &&(s.get(FACING).equals(Direction.SOUTH))) {//check if the neighbor block is a coax cable
                //Main.LOGGER.info("Neighbor to east is coax");
                world.setBlockState(pos, state.with(north, true), NOTIFY_ALL);//set this block as connecting to that neighbor block
            }
        }

        //---------- Top Neighbor
        state=world.getBlockState(pos);
        if(world.getBlockState(pos.up()).getBlock() instanceof  AIOCableBundle){//check if the neighbor block is a coax cable
            //Main.LOGGER.info("Neighbor above is coax");
            if(isFacingSingleDirection(world,pos.up())) {
                world.setBlockState(pos, state.with(up, true), NOTIFY_ALL);//set this block as connecting to that neighbor block
                world.setBlockState(pos.up(), world.getBlockState(pos.up()).with(down, true), NOTIFY_ALL);//set the neighbor block to point to this block
            }
        }

        //---------- Bottom Neighbor
        state=world.getBlockState(pos);
        if(world.getBlockState(pos.down()).getBlock() instanceof  AIOCableBundle){//check if the neighbor block is a coax cable
            //Main.LOGGER.info("Neighbor below is coax");
            if(isFacingSingleDirection(world,pos.down())) {
                world.setBlockState(pos, state.with(down, true), NOTIFY_ALL);//set this block as connecting to that neighbor block
                world.setBlockState(pos.down(), world.getBlockState(pos.down()).with(up, true), NOTIFY_ALL);//set the neighbor block to point to this block
            }
        }
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, ShapeContext context) {
        VoxelShape shape = VoxelShapes.empty();
        if(state.get(north)){
            shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.4375, 0.4375, 0, 0.5625, 0.5625, 0.5));
            shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.375, 0.375, 0, 0.4375, 0.4375, 0.5));
            shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.5625, 0.375, 0, 0.625, 0.4375, 0.5));
        }
        if(state.get(south)){
            shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.4375, 0.4375, 0.5, 0.5625, 0.5625, 1));
            shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.5625, 0.375, 0.5, 0.625, 0.4375, 1));
            shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.375, 0.375, 0.5, 0.4375, 0.4375, 1));
        }
        if(state.get(east)){
            if(state.get(north)||state.get(south)||state.get(up)||state.get(down)){
                shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.36875, 0.36875, 0.36875, 0.63125, 0.63125, 0.63125));
            }
            shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.5, 0.4375, 0.4375, 1, 0.5625, 0.5625));
            shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.5, 0.375, 0.375, 1, 0.4375, 0.4375));
            shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.5, 0.375, 0.5625, 1, 0.4375, 0.625));
        }
        if(state.get(west)){
            if(state.get(north)||state.get(south)||state.get(up)||state.get(down)){
                shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.36875, 0.36875, 0.36875, 0.63125, 0.63125, 0.63125));
            }
            shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0, 0.4375, 0.4375, 0.5, 0.5625, 0.5625));
            shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0, 0.375, 0.5625, 0.5, 0.4375, 0.625));
            shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0, 0.375, 0.375, 0.5, 0.4375, 0.4375));
        }
        if(state.get(up)){
            if(state.get(north)||state.get(south)){
                shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.36875, 0.36875, 0.36875, 0.63125, 0.63125, 0.63125));
            }
            shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.4375, 0.5, 0.4375, 0.5625, 1, 0.5625));
            shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.375, 0.5, 0.375, 0.4375, 1, 0.4375));
            shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.5625, 0.5, 0.375, 0.625, 1, 0.4375));
        }
        if(state.get(down)){
            if(state.get(north)||state.get(south)){
                shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.36875, 0.36875, 0.36875, 0.63125, 0.63125, 0.63125));
            }
            shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.4375, 0, 0.4375, 0.5625, 0.5, 0.5625));
            shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.5625, 0, 0.375, 0.625, 0.5, 0.4375));
            shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.375, 0, 0.375, 0.4375, 0.5, 0.4375));
        }
        if(shape==VoxelShapes.empty()){
            shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.36875, 0.36875, 0.36875, 0.63125, 0.63125, 0.63125));
        }
        return shape;
    }
}
