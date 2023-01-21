package com.cbi.coollink.blocks;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
//import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import static net.minecraft.state.property.Properties.AXIS;
import static net.minecraft.state.property.Properties.HORIZONTAL_FACING;


public class SmallConduit extends Block {
    public static final SmallConduit ENTRY = new SmallConduit(FabricBlockSettings.of(Material.STONE).hardness(0.5f));
    BooleanProperty north = BooleanProperty.of("north");
    BooleanProperty east = BooleanProperty.of("east");
    BooleanProperty south = BooleanProperty.of("south");
    BooleanProperty west = BooleanProperty.of("west");

    public SmallConduit(Settings settings) {
        super(settings);
        setDefaultState(getDefaultState()
                .with(north,false)
                .with(east,false)
                .with(south,false)
                .with(west,false)
        );
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> stateManager) {
        north = BooleanProperty.of("north");
        east = BooleanProperty.of("east");
        south = BooleanProperty.of("south");
        west = BooleanProperty.of("west");
        stateManager.add(AXIS);
        stateManager.add(this.north);
        stateManager.add(this.east);
        stateManager.add(this.south);
        stateManager.add(this.west);
    }

    @SuppressWarnings("deprecation")
    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, ShapeContext context) {
        VoxelShape shape= VoxelShapes.empty();
        //use a different hit box based on the rotation of the block
        if(state.get(north)||state.get(south)){
            shape=VoxelShapes.union(shape,makeShapeNS());
        }
        if(state.get(east)||state.get(west)){
            shape=VoxelShapes.union(shape,makeShapeEW());
        }
        if (shape.isEmpty())
            shape=makeShapeNS();

        return shape;
    }

    public VoxelShape makeShapeNS() {
        VoxelShape shape = VoxelShapes.empty();
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.4375, 0, 0, 0.4375, 0.125, 1));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.5625, 0, 0, 0.5625, 0.125, 1));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.4375, 0, 0, 0.5625, 0, 1));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.44375, 0.000625, 0, 0.55625, 0.113125, 1));
        return shape;
    }

    public VoxelShape makeShapeEW() {
        VoxelShape shape = VoxelShapes.empty();
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0, 0, 0.5625, 1, 0.125, 0.5625));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0, 0, 0.4375, 1, 0.125, 0.4375));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0, 0, 0.4375, 1, 0, 0.5625));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0, 0.000625, 0.44375, 1, 0.113125, 0.55625));
        return shape;
    }

    public VoxelShape makeShapeS() {
        VoxelShape shape = VoxelShapes.empty();
        return shape;
    }

    public VoxelShape makeShapeW() {
        VoxelShape shape = VoxelShapes.empty();
        return shape;
    }


    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        Direction.Axis facing;
        switch (ctx.getPlayerFacing()){
            case NORTH:
            case SOUTH:
                return this.getDefaultState().with(AXIS, Direction.Axis.Z);
            case EAST:
            default:
                return this.getDefaultState().with(AXIS, Direction.Axis.X);
        }
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {

        BlockPos neighbor1= new BlockPos(pos.getX()+1,pos.getY(),pos.getZ());//the location of the nigher block you want to check this should be reassigned for every block you want to check
        BlockPos neighbor2= new BlockPos(pos.getX()-1,pos.getY(),pos.getZ());
        BlockPos neighbor3= new BlockPos(pos.getX(),pos.getY(),pos.getZ()+1);
        BlockPos neighbor4= new BlockPos(pos.getX(),pos.getY(),pos.getZ()-1);


        if(world.getBlockState(neighbor1).getBlock().equals(this)){//check if the neighbor block is medium conduit
            world.setBlockState(pos,state.with(east,true),NOTIFY_ALL);//set this block as connecting to that neighbor block
            world.setBlockState(neighbor1,world.getBlockState(neighbor1).with(west,true),NOTIFY_ALL);//set the neighbor block to point to this block
        }
        state=world.getBlockState(pos);
        if(world.getBlockState(neighbor2).getBlock().equals(this)){//check if the neighbor block is medium conduit
            world.setBlockState(pos,state.with(west,true),NOTIFY_ALL);//set this block as connecting to that neighbor block
            world.setBlockState(neighbor2,world.getBlockState(neighbor2).with(east,true),NOTIFY_ALL);//set the neighbor block to point to this block
        }
        state=world.getBlockState(pos);
        if(world.getBlockState(neighbor3).getBlock().equals(this)){//check if the neighbor block is medium conduit
            world.setBlockState(pos,state.with(south,true),NOTIFY_ALL);//set this block as connecting to that neighbor block
            world.setBlockState(neighbor3,world.getBlockState(neighbor3).with(north,true),NOTIFY_ALL);//set the neighbor block to point to this block
        }
        state=world.getBlockState(pos);
        if(world.getBlockState(neighbor4).getBlock().equals(this)){//check if the neighbor block is medium conduit
            world.setBlockState(pos,state.with(north,true),NOTIFY_ALL);//set this block as connecting to that neighbor block
            world.setBlockState(neighbor4,world.getBlockState(neighbor4).with(south,true),NOTIFY_ALL);//set the neighbor block to point to this block
        }
    }
}