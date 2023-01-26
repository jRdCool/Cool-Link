package com.cbi.coollink.blocks;

import com.cbi.coollink.Main;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.*;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

import static net.minecraft.state.property.Properties.*;

public class MediumConduit extends Block {

    static BooleanProperty north = BooleanProperty.of("north");
    static BooleanProperty east = BooleanProperty.of("east");
    static BooleanProperty south = BooleanProperty.of("south");
    static BooleanProperty west = BooleanProperty.of("west");
    static BooleanProperty junctionBox = BooleanProperty.of("junctionbox");
    static IntProperty cableShape = IntProperty.of("cableshape",0,7);
    //cableShape is an integer that is used to switch between the models
    //  0 = NS
    //  1 = EW
    //  2 = Junction Box (3 or 4 directions)
    //  3 = Vertical Transition Box
    //  4 = NE
    //  5 = SE
    //  6 = SW
    //  7 = NW




    public static final MediumConduit ENTRY = new MediumConduit(FabricBlockSettings.of(Material.STONE).hardness(0.5f));

    public MediumConduit(Settings settings) {
        super(settings);
        setDefaultState(getDefaultState()
                .with(north,false)
                .with(east,false)
                .with(south,false)
                .with(west,false)
                .with(junctionBox,false)
                .with(cableShape,0)
        );
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> stateManager) {
        north = BooleanProperty.of("north");
        east = BooleanProperty.of("east");
        south = BooleanProperty.of("south");
        west = BooleanProperty.of("west");
        junctionBox = BooleanProperty.of("junctionbox");
            stateManager.add(AXIS);
            stateManager.add(HORIZONTAL_FACING);
            stateManager.add(this.north);
            stateManager.add(this.east);
            stateManager.add(this.south);
            stateManager.add(this.west);
            stateManager.add(this.junctionBox);
            stateManager.add(this.cableShape);
    }


    @SuppressWarnings({"deprecation","all"})
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
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.3125, 0, 0, 0.6875, 0.25, 1));
        return shape;
    }

    public VoxelShape makeShapeEW() {
        VoxelShape shape = VoxelShapes.empty();
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0, 0, 0.3125, 1, 0.25, 0.6875));
        return shape;
    }

    @SuppressWarnings("all")
    public VoxelShape makeShapeS() {
        VoxelShape shape = VoxelShapes.empty();
        return shape;
    }

    @SuppressWarnings("all")
    public VoxelShape makeShapeW() {
        VoxelShape shape = VoxelShapes.empty();
        return shape;
    }


    @Override
    @SuppressWarnings("all")
    public BlockState getPlacementState(ItemPlacementContext ctx) {



        switch (ctx.getPlayerFacing()){
            case NORTH:
            case SOUTH:
                return this.getDefaultState().with(AXIS, Direction.Axis.Z).with(HORIZONTAL_FACING, ctx.getPlayerFacing());
            case EAST:
            default:
                return this.getDefaultState().with(AXIS, Direction.Axis.X).with(HORIZONTAL_FACING, ctx.getPlayerFacing());
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
        state=world.getBlockState(pos);
        world.setBlockState(pos,state.with(junctionBox,junctionBoxCheck(state)),NOTIFY_ALL);

    }

    public void onBroken(WorldAccess world, BlockPos pos, BlockState state){



    }

    public boolean junctionBoxCheck(BlockState state){
      boolean box=false;
      if((state.get(north) && state.get(south) && state.get(east))
      ||(state.get(north) && state.get(south) && state.get(west))
      ||(state.get(north) && state.get(west) && state.get(east))
      ||(state.get(south) && state.get(west) && state.get(east))){
          box=true;
      }
      return box;
    }


}