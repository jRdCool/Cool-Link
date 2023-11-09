package com.cbi.coollink.blocks;

import com.cbi.coollink.net.OpenConduitGuiPacket;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.*;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.*;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;
import static net.minecraft.state.property.Properties.*;

public abstract class Conduit extends BlockWithEntity {


    static BooleanProperty north = BooleanProperty.of("north");
    static BooleanProperty east = BooleanProperty.of("east");
    static BooleanProperty south = BooleanProperty.of("south");
    static BooleanProperty west = BooleanProperty.of("west");
    static BooleanProperty junctionBox = BooleanProperty.of("junctionbox");
    static IntProperty cableShape = IntProperty.of("cableshape",0,7);
    static IntProperty cableLevel = IntProperty.of("cablelevel",1,3);
    static BooleanProperty neighborLarger = BooleanProperty.of("largerneighbor");


    //cableShape is an integer that is used to switch between the models
    //  0 = NS
    //  1 = EW
    //  2 = Junction Box (3 or 4 directions)
    //  3 = Vertical Transition Box
    //  4 = NE
    //  5 = SE
    //  6 = SW
    //  7 = NW



    public Conduit(AbstractBlock.Settings settings) {
        super(settings);
        setDefaultState(getDefaultState()
                .with(north,false)
                .with(east,false)
                .with(south,false)
                .with(west,false)
                .with(junctionBox,false)
                .with(cableShape,0)
                .with(neighborLarger,false)
        );
    }

    @SuppressWarnings({"deprecation","all"})
    protected void appendProperties(StateManager.Builder<Block, BlockState> stateManager) {
        north = BooleanProperty.of("north");
        east = BooleanProperty.of("east");
        south = BooleanProperty.of("south");
        west = BooleanProperty.of("west");
        junctionBox = BooleanProperty.of("junctionbox");
        stateManager.add(AXIS);
        //stateManager.add(HORIZONTAL_FACING);
        stateManager.add(north);
        stateManager.add(east);
        stateManager.add(south);
        stateManager.add(west);
        stateManager.add(junctionBox);
        stateManager.add(FACING);

        stateManager.add(cableShape);
        stateManager.add(cableLevel);
        stateManager.add(neighborLarger);
    }


    @Override
    @SuppressWarnings("all")
    public BlockState getPlacementState(ItemPlacementContext ctx) {


        switch (ctx.getHorizontalPlayerFacing()){
            case NORTH:
                return this.getDefaultState().with(FACING, Direction.NORTH);
            case SOUTH:
                return this.getDefaultState().with(FACING, Direction.SOUTH);
            case EAST:
                return this.getDefaultState().with(FACING, Direction.EAST);
            default:
                return this.getDefaultState().with(FACING, Direction.WEST);
        }
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {

        //Main.LOGGER.info("2 ");
        BlockPos neighbor1= new BlockPos(pos.getX()+1,pos.getY(),pos.getZ());//E //the location of the nigher block you want to check this should be reassigned for every block you want to check
        BlockPos neighbor2= new BlockPos(pos.getX()-1,pos.getY(),pos.getZ());//W
        BlockPos neighbor3= new BlockPos(pos.getX(),pos.getY(),pos.getZ()+1);//S
        BlockPos neighbor4= new BlockPos(pos.getX(),pos.getY(),pos.getZ()-1);//N

        neighborSizeCheck(state,world,pos);
        //---------- East Neighbor
        state=world.getBlockState(pos);
        if(world.getBlockState(neighbor1).getBlock() instanceof  Conduit){//check if the neighbor block is a conduit

            world.setBlockState(pos,state.with(east,true),NOTIFY_ALL);//set this block as connecting to that neighbor block
            world.setBlockState(neighbor1,world.getBlockState(neighbor1).with(west,true),NOTIFY_ALL);//set the neighbor block to point to this block
            onUpdate(world.getBlockState(neighbor1),world,neighbor1);
        }

        //---------- West Neighbor
        state=world.getBlockState(pos);
        if(world.getBlockState(neighbor2).getBlock() instanceof  Conduit){//check if the neighbor block is a conduit

            world.setBlockState(pos,state.with(west,true),NOTIFY_ALL);//set this block as connecting to that neighbor block
            world.setBlockState(neighbor2,world.getBlockState(neighbor2).with(east,true),NOTIFY_ALL);//set the neighbor block to point to this block
            onUpdate(world.getBlockState(neighbor2),world,neighbor2);
        }

        //---------- South Neighbor
        state=world.getBlockState(pos);
        if(world.getBlockState(neighbor3).getBlock() instanceof  Conduit){//check if the neighbor block is a conduit

            world.setBlockState(pos,state.with(south,true),NOTIFY_ALL);//set this block as connecting to that neighbor block
            world.setBlockState(neighbor3,world.getBlockState(neighbor3).with(north,true),NOTIFY_ALL);//set the neighbor block to point to this block
            onUpdate(world.getBlockState(neighbor3),world,neighbor3);
        }

        //---------- North Neighbor
        state=world.getBlockState(pos);
        if(world.getBlockState(neighbor4).getBlock() instanceof  Conduit){//check if the neighbor block is a conduit

            world.setBlockState(pos,state.with(north,true),NOTIFY_ALL);//set this block as connecting to that neighbor block
            world.setBlockState(neighbor4,world.getBlockState(neighbor4).with(south,true),NOTIFY_ALL);//set the neighbor block to point to this block
            onUpdate(world.getBlockState(neighbor4),world,neighbor4);
        }

        state=world.getBlockState(pos);
        world.setBlockState(pos,state.with(junctionBox,junctionBoxCheck(state)),NOTIFY_ALL);

        state=world.getBlockState(pos);
        onUpdate(state,world,pos);


    }

    @SuppressWarnings({"deprecation","all"})
    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        BlockPos neighbor1= new BlockPos(pos.getX()+1,pos.getY(),pos.getZ());//E//the location of the nigher block you want to check this should be reassigned for every block you want to check
        BlockPos neighbor2= new BlockPos(pos.getX()-1,pos.getY(),pos.getZ());//W
        BlockPos neighbor3= new BlockPos(pos.getX(),pos.getY(),pos.getZ()+1);//S
        BlockPos neighbor4= new BlockPos(pos.getX(),pos.getY(),pos.getZ()-1);//N

        neighborSizeCheck(state,world,pos);

        onUpdate(state,world,pos);
        state=world.getBlockState(pos);
        super.neighborUpdate(state, world, pos, sourceBlock, sourcePos, notify);
    }

    public void onBroken(WorldAccess world, BlockPos pos, BlockState state){
        BlockPos neighbor1= new BlockPos(pos.getX()+1,pos.getY(),pos.getZ());//E//the location of the nigher block you want to check this should be reassigned for every block you want to check
        BlockPos neighbor2= new BlockPos(pos.getX()-1,pos.getY(),pos.getZ());//W
        BlockPos neighbor3= new BlockPos(pos.getX(),pos.getY(),pos.getZ()+1);//S
        BlockPos neighbor4= new BlockPos(pos.getX(),pos.getY(),pos.getZ()-1);//N
        //Main.LOGGER.info(world.toString());


        if(world.getBlockState(neighbor1).getBlock() instanceof  Conduit){//check if the neighbor block is a conduit
            //Main.LOGGER.info("west neighbor is conduit");
            neighborSizeCheck(state,world,neighbor1);//have neighbor check its neighbors
            world.setBlockState(neighbor1, world.getBlockState(neighbor1).with(west, false), NOTIFY_ALL);//set the neighbor block to point to this block
            onUpdate(world.getBlockState(neighbor1),world,neighbor1);
        }
        //else Main.LOGGER.info("check failed");

        if(world.getBlockState(neighbor2).getBlock() instanceof  Conduit){//check if the neighbor block is a conduit
            neighborSizeCheck(state,world,neighbor2);//have neighbor check its neighbors
            world.setBlockState(neighbor2, world.getBlockState(neighbor2).with(east, false), NOTIFY_ALL);//set the neighbor block to point to this block
            onUpdate(world.getBlockState(neighbor2),world,neighbor2);
        }

        if(world.getBlockState(neighbor3).getBlock() instanceof  Conduit){//check if the neighbor block is a conduit
            neighborSizeCheck(state,world,neighbor3);//have neighbor check its neighbors
            world.setBlockState(neighbor3, world.getBlockState(neighbor3).with(north, false), NOTIFY_ALL);//set the neighbor block to point to this block
            onUpdate(world.getBlockState(neighbor3),world,neighbor3);
        }

        if(world.getBlockState(neighbor4).getBlock() instanceof  Conduit){//check if the neighbor block is a conduit
            neighborSizeCheck(state,world,neighbor4);//have neighbor check its neighbors
            world.setBlockState(neighbor4, world.getBlockState(neighbor4).with(south, false), NOTIFY_ALL);//set the neighbor block to point to this block
            onUpdate(world.getBlockState(neighbor4),world,neighbor4);
        }
    }

    @SuppressWarnings({"all"})
    public boolean junctionBoxCheck(BlockState state){
        boolean box=false;
        if((state.get(north) && state.get(south) && state.get(east))
                ||(state.get(north) && state.get(south) && state.get(west))
                ||(state.get(north) && state.get(west) && state.get(east))
                ||(state.get(south) && state.get(west) && state.get(east))
                ||state.get(neighborLarger))
        {
            box=true;
        }
        else box=false;
        return box;
    }

    public VoxelShape junctionBoxVoxel(){
        VoxelShape shape = VoxelShapes.empty();
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0, 0, 0, 1, 0.25, 1));

        return shape;
    }

    public void onUpdate (BlockState state, WorldAccess world, BlockPos pos){
        world.setBlockState(pos,state.with(junctionBox,junctionBoxCheck(state)),NOTIFY_ALL);

        if(junctionBoxCheck(state))
        {
            state=world.getBlockState(pos);
            world.setBlockState(pos,state.with(cableShape,2),NOTIFY_ALL);
        }
        else if(state.get(north) && state.get(east)){
            state=world.getBlockState(pos);
            world.setBlockState(pos,state.with(cableShape,4),NOTIFY_ALL);
        }
        else if(state.get(south) && state.get(east)){
            state=world.getBlockState(pos);
            world.setBlockState(pos,state.with(cableShape,5),NOTIFY_ALL);
        }
        else if(state.get(south) && state.get(west)){
            state=world.getBlockState(pos);
            world.setBlockState(pos,state.with(cableShape,6),NOTIFY_ALL);
        }
        else if(state.get(north) && state.get(west)){
            state=world.getBlockState(pos);
            world.setBlockState(pos,state.with(cableShape,7),NOTIFY_ALL);
        }
        else if(state.get(north) || state.get(south)){
            state=world.getBlockState(pos);
            world.setBlockState(pos,state.with(cableShape,0),NOTIFY_ALL);
        }
        else if(state.get(west) || state.get(east)){
            state=world.getBlockState(pos);
            world.setBlockState(pos,state.with(cableShape,1),NOTIFY_ALL);
        }
        else{
            switch(state.get(FACING)){
                case NORTH,SOUTH -> {
                    state=world.getBlockState(pos);
                    world.setBlockState(pos,state.with(cableShape,0),NOTIFY_ALL);

                }
                case EAST,WEST -> {
                    state=world.getBlockState(pos);
                    world.setBlockState(pos,state.with(cableShape,1),NOTIFY_ALL);
                }
            }
        }
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        // With inheriting from BlockWithEntity this defaults to INVISIBLE, so we need to change that!
        return BlockRenderType.MODEL;
    }


    @SuppressWarnings({"all"})
    public void neighborSizeCheck(BlockState state, WorldAccess world, BlockPos pos){

        boolean nL=false, sL=false, eL=false, wL=false;

        BlockPos east= new BlockPos(pos.getX()+1,pos.getY(),pos.getZ());//E//the location of the nigher block you want to check this should be reassigned for every block you want to check
        BlockPos west= new BlockPos(pos.getX()-1,pos.getY(),pos.getZ());//W
        BlockPos south= new BlockPos(pos.getX(),pos.getY(),pos.getZ()+1);//S
        BlockPos north= new BlockPos(pos.getX(),pos.getY(),pos.getZ()-1);//N

        //state=world.getBlockState(pos);
        if(world.getBlockState(north).getBlock() instanceof  Conduit) {//check if the neighbor block is a conduit
            if (state.get(cableLevel) > world.getBlockState(north).get(cableLevel)) {
                world.setBlockState(north, world.getBlockState(north).with(neighborLarger, true), NOTIFY_ALL);//set the neighbor block to point to this block
                //System.out.println("placed block is larger");
            } else if (state.get(cableLevel) < world.getBlockState(north).get(cableLevel)) {
                //System.out.println(state.get(neighborLarger));
                state = state.with(neighborLarger, true);
                nL=true;
                //Main.LOGGER.info("north is larger " + state.get(neighborLarger));
            }
        }

        //state=world.getBlockState(pos);
        if(world.getBlockState(south).getBlock() instanceof  Conduit) {//check if the neighbor block is a conduit
            if (state.get(cableLevel) > world.getBlockState(south).get(cableLevel)) {
                world.setBlockState(south, world.getBlockState(south).with(neighborLarger, true), NOTIFY_ALL);//set the neighbor block to point to this block
                //System.out.println("placed block is larger");
            } else if (state.get(cableLevel) < world.getBlockState(south).get(cableLevel)) {
                //System.out.println(state.get(neighborLarger));
                state = state.with(neighborLarger, true);
                sL=true;
                //Main.LOGGER.info("north is larger " + state.get(neighborLarger));
            }
        }

        //state=world.getBlockState(pos);
        if(world.getBlockState(east).getBlock() instanceof  Conduit) {//check if the neighbor block is a conduit
            //Main.LOGGER.info(state.get(cableLevel)+"");
            if (state.get(cableLevel) > world.getBlockState(east).get(cableLevel)) {
                world.setBlockState(east, world.getBlockState(east).with(neighborLarger, true), NOTIFY_ALL);//set the neighbor block to point to this block
                //System.out.println("placed block is larger");
            } else if (state.get(cableLevel) < world.getBlockState(east).get(cableLevel)) {
                //System.out.println(state.get(neighborLarger));
                state = state.with(neighborLarger, true);
                eL=true;
                //Main.LOGGER.info("north is larger " + state.get(neighborLarger));
            }
        }

        //state=world.getBlockState(pos);
        //Main.LOGGER.info((world.getBlockState(west).getBlock() instanceof  Conduit) +"");
        if(world.getBlockState(west).getBlock() instanceof  Conduit) {//check if the neighbor block is a conduit
            //Main.LOGGER.info("west neighbor is conduit");
            if (state.get(cableLevel) > world.getBlockState(west).get(cableLevel)) {
                world.setBlockState(west, world.getBlockState(west).with(neighborLarger, true), NOTIFY_ALL);//set the neighbor block to point to this block
                //System.out.println("placed block is larger");
            } else if (state.get(cableLevel) < world.getBlockState(west).get(cableLevel)) {
                //System.out.println(state.get(neighborLarger));
                state = state.with(neighborLarger, true);
                wL=true;
                //Main.LOGGER.info("north is larger " + state.get(neighborLarger));
            }
        }
        //Main.LOGGER.info("any larger " + !(nL||sL||eL||wL));
        //state=world.getBlockState(pos);
        if(!(nL||sL||eL||wL))
        {
            state=state.with(neighborLarger, false);
        }

    }

    @SuppressWarnings("deprecation")
    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand , BlockHitResult bhr){
        String currentThread = Thread.currentThread().getName();
        //check witch thread the code is being executed on the server
        if(currentThread.equals("Server thread")) {//if the code is being executed on the server thread

            OpenConduitGuiPacket packet = new OpenConduitGuiPacket();//add data to send to GUI here
            ServerPlayNetworking.send((ServerPlayerEntity)player,packet);
        }
        return super.onUse(state,world,pos,player,hand,bhr);
    }

}
