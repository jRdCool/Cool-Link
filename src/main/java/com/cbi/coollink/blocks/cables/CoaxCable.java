package com.cbi.coollink.blocks.cables;

import com.cbi.coollink.items.CoaxialCable;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;

public class CoaxCable extends Block {

    public static final CoaxCable ENTRY =new CoaxCable(FabricBlockSettings.create().hardness(0.5f));

    static BooleanProperty north = BooleanProperty.of("north");
    static BooleanProperty east = BooleanProperty.of("east");
    static BooleanProperty south = BooleanProperty.of("south");
    static BooleanProperty west = BooleanProperty.of("west");
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
        BlockPos neighbor1= new BlockPos(pos.getX()+1,pos.getY(),pos.getZ());//E //the location of the nigher block you want to check this should be reassigned for every block you want to check
        BlockPos neighbor2= new BlockPos(pos.getX()-1,pos.getY(),pos.getZ());//W
        BlockPos neighbor3= new BlockPos(pos.getX(),pos.getY(),pos.getZ()+1);//S
        BlockPos neighbor4= new BlockPos(pos.getX(),pos.getY(),pos.getZ()-1);//N
        BlockPos neighbor5= new BlockPos(pos.getX(),pos.getY()+1,pos.getZ());//U
        BlockPos neighbor6= new BlockPos(pos.getX(),pos.getY()-1,pos.getZ());//D

        //---------- East Neighbor
        state=world.getBlockState(pos);
        if(world.getBlockState(neighbor1).getBlock() instanceof CoaxCable){//check if the neighbor block is a conduit

            world.setBlockState(pos,state.with(east,true),NOTIFY_ALL);//set this block as connecting to that neighbor block
            world.setBlockState(neighbor1,world.getBlockState(neighbor1).with(west,true),NOTIFY_ALL);//set the neighbor block to point to this block
        }

        //---------- West Neighbor
        state=world.getBlockState(pos);
        if(world.getBlockState(neighbor2).getBlock() instanceof  CoaxCable){//check if the neighbor block is a conduit

            world.setBlockState(pos,state.with(west,true),NOTIFY_ALL);//set this block as connecting to that neighbor block
            world.setBlockState(neighbor2,world.getBlockState(neighbor2).with(east,true),NOTIFY_ALL);//set the neighbor block to point to this block
        }

        //---------- South Neighbor
        state=world.getBlockState(pos);
        if(world.getBlockState(neighbor3).getBlock() instanceof  CoaxCable){//check if the neighbor block is a conduit

            world.setBlockState(pos,state.with(south,true),NOTIFY_ALL);//set this block as connecting to that neighbor block
            world.setBlockState(neighbor3,world.getBlockState(neighbor3).with(north,true),NOTIFY_ALL);//set the neighbor block to point to this block
        }

        //---------- North Neighbor
        state=world.getBlockState(pos);
        if(world.getBlockState(neighbor4).getBlock() instanceof  CoaxCable){//check if the neighbor block is a conduit

            world.setBlockState(pos,state.with(north,true),NOTIFY_ALL);//set this block as connecting to that neighbor block
            world.setBlockState(neighbor4,world.getBlockState(neighbor4).with(south,true),NOTIFY_ALL);//set the neighbor block to point to this block
        }

        //---------- Top Neighbor
        state=world.getBlockState(pos);
        if(world.getBlockState(neighbor5).getBlock() instanceof  CoaxCable){//check if the neighbor block is a conduit

            world.setBlockState(pos,state.with(up,true),NOTIFY_ALL);//set this block as connecting to that neighbor block
            world.setBlockState(neighbor5,world.getBlockState(neighbor5).with(down,true),NOTIFY_ALL);//set the neighbor block to point to this block
        }

        //---------- Bottom Neighbor
        state=world.getBlockState(pos);
        if(world.getBlockState(neighbor6).getBlock() instanceof  CoaxCable){//check if the neighbor block is a conduit

            world.setBlockState(pos,state.with(down,true),NOTIFY_ALL);//set this block as connecting to that neighbor block
            world.setBlockState(neighbor6,world.getBlockState(neighbor6).with(up,true),NOTIFY_ALL);//set the neighbor block to point to this block
        }

        //super.onPlaced(world, pos, state, placer, itemStack);
    }

    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {

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
