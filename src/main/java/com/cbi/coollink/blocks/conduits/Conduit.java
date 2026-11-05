package com.cbi.coollink.blocks.conduits;

import com.cbi.coollink.net.OpenConduitGuiPacket;
import com.cbi.coollink.rendering.IWireNode;
import com.mojang.serialization.MapCodec;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.FACING;

public abstract class Conduit extends BaseEntityBlock {


    public static final BooleanProperty north = BooleanProperty.create("north");
    public static final BooleanProperty east = BooleanProperty.create("east");
    public static final BooleanProperty south = BooleanProperty.create("south");
    public static final BooleanProperty west = BooleanProperty.create("west");
    public static final BooleanProperty HIDDEN = BooleanProperty.create("hidden");
    public static final BooleanProperty junctionBox = BooleanProperty.create("junctionbox");
    public static final IntegerProperty cableShape = IntegerProperty.create("cableshape",0,7);
    public static final IntegerProperty cableLevel = IntegerProperty.create("cablelevel",1,3);
    public static final BooleanProperty neighborLarger = BooleanProperty.create("largerneighbor");


    //cableShape is an integer that is used to switch between the models
    //  0 = NS
    //  1 = EW
    //  2 = Junction Box (3 or 4 directions)
    //  3 = Vertical Transition Box
    //  4 = NE
    //  5 = SE
    //  6 = SW
    //  7 = NW



    public Conduit(BlockBehaviour.Properties settings) {
        super(settings);
        registerDefaultState(defaultBlockState()
                .setValue(north,false)
                .setValue(east,false)
                .setValue(south,false)
                .setValue(west,false)
                .setValue(junctionBox,false)
                .setValue(cableShape,0)
                .setValue(neighborLarger,false)
                .setValue(HIDDEN,false)
        );
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> stateManager) {
        //north = BooleanProperty.of("north");
        //east = BooleanProperty.of("east");
        //south = BooleanProperty.of("south");
        //west = BooleanProperty.of("west");
        //junctionBox = BooleanProperty.of("junctionbox");
        //stateManager.add(AXIS);
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
        stateManager.add(HIDDEN);
    }



    @Override
    @SuppressWarnings("all")
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {


        switch (ctx.getHorizontalDirection()){
            case NORTH:
                return this.defaultBlockState().setValue(FACING, Direction.NORTH);
            case SOUTH:
                return this.defaultBlockState().setValue(FACING, Direction.SOUTH);
            case EAST:
                return this.defaultBlockState().setValue(FACING, Direction.EAST);
            default:
                return this.defaultBlockState().setValue(FACING, Direction.WEST);
        }
    }

    @Override
    public void setPlacedBy(Level world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {

        //Main.LOGGER.info("2 ");
        BlockPos neighbor1= new BlockPos(pos.getX()+1,pos.getY(),pos.getZ());//E //the location of the nigher block you want to check this should be reassigned for every block you want to check
        BlockPos neighbor2= new BlockPos(pos.getX()-1,pos.getY(),pos.getZ());//W
        BlockPos neighbor3= new BlockPos(pos.getX(),pos.getY(),pos.getZ()+1);//S
        BlockPos neighbor4= new BlockPos(pos.getX(),pos.getY(),pos.getZ()-1);//N

        neighborSizeCheck(state,world,pos);
        //---------- East Neighbor
        state=world.getBlockState(pos);
        if(world.getBlockState(neighbor1).getBlock() instanceof  Conduit){//check if the neighbor block is a conduit

            world.setBlock(pos,state.setValue(east,true),UPDATE_ALL);//set this block as connecting to that neighbor block
            world.setBlock(neighbor1,world.getBlockState(neighbor1).setValue(west,true),UPDATE_ALL);//set the neighbor block to point to this block
            onUpdate(world.getBlockState(neighbor1),world,neighbor1);
        }

        //---------- West Neighbor
        state=world.getBlockState(pos);
        if(world.getBlockState(neighbor2).getBlock() instanceof  Conduit){//check if the neighbor block is a conduit

            world.setBlock(pos,state.setValue(west,true),UPDATE_ALL);//set this block as connecting to that neighbor block
            world.setBlock(neighbor2,world.getBlockState(neighbor2).setValue(east,true),UPDATE_ALL);//set the neighbor block to point to this block
            onUpdate(world.getBlockState(neighbor2),world,neighbor2);
        }

        //---------- South Neighbor
        state=world.getBlockState(pos);
        if(world.getBlockState(neighbor3).getBlock() instanceof  Conduit){//check if the neighbor block is a conduit

            world.setBlock(pos,state.setValue(south,true),UPDATE_ALL);//set this block as connecting to that neighbor block
            world.setBlock(neighbor3,world.getBlockState(neighbor3).setValue(north,true),UPDATE_ALL);//set the neighbor block to point to this block
            onUpdate(world.getBlockState(neighbor3),world,neighbor3);
        }

        //---------- North Neighbor
        state=world.getBlockState(pos);
        if(world.getBlockState(neighbor4).getBlock() instanceof  Conduit){//check if the neighbor block is a conduit

            world.setBlock(pos,state.setValue(north,true),UPDATE_ALL);//set this block as connecting to that neighbor block
            world.setBlock(neighbor4,world.getBlockState(neighbor4).setValue(south,true),UPDATE_ALL);//set the neighbor block to point to this block
            onUpdate(world.getBlockState(neighbor4),world,neighbor4);
        }

        state=world.getBlockState(pos);
        world.setBlock(pos,state.setValue(junctionBox,junctionBoxCheck(state)),UPDATE_ALL);

        state=world.getBlockState(pos);
        onUpdate(state,world,pos);


    }

    public BlockState onBreak(Level world, BlockPos pos, BlockState state, Player player){
        BlockEntity be = world.getBlockEntity(pos);
        if(be instanceof IWireNode self){
            for(int i=0;i<self.getNodeCount();i++){
                if(!self.hasConnection(i)) continue;
                BlockEntity obe =  world.getBlockEntity(self.getLocalNode(i).getTargetPos());
                if(obe instanceof IWireNode other) {
                    other.removeNode(self.getOtherNodeIndex(i));
                }
            }
        }
        return super.playerWillDestroy(world,pos,state,player);
    }


    @SuppressWarnings({"deprecation","all"})
    @Override
    public void neighborChanged(BlockState state, Level world, BlockPos pos, Block sourceBlock, @Nullable Orientation wireOrientation, boolean notify) {
        BlockPos neighbor1= new BlockPos(pos.getX()+1,pos.getY(),pos.getZ());//E//the location of the nigher block you want to check this should be reassigned for every block you want to check
        BlockPos neighbor2= new BlockPos(pos.getX()-1,pos.getY(),pos.getZ());//W
        BlockPos neighbor3= new BlockPos(pos.getX(),pos.getY(),pos.getZ()+1);//S
        BlockPos neighbor4= new BlockPos(pos.getX(),pos.getY(),pos.getZ()-1);//N

        neighborSizeCheck(state,world,pos);

        onUpdate(state,world,pos);
        state=world.getBlockState(pos);
        super.neighborChanged(state, world, pos, sourceBlock, wireOrientation, notify);
    }

    @Override
    public void destroy(LevelAccessor world, BlockPos pos, BlockState state) {
    
        BlockPos neighbor1= new BlockPos(pos.getX()+1,pos.getY(),pos.getZ());//E//the location of the nigher block you want to check this should be reassigned for every block you want to check
        BlockPos neighbor2= new BlockPos(pos.getX()-1,pos.getY(),pos.getZ());//W
        BlockPos neighbor3= new BlockPos(pos.getX(),pos.getY(),pos.getZ()+1);//S
        BlockPos neighbor4= new BlockPos(pos.getX(),pos.getY(),pos.getZ()-1);//N
        //Main.LOGGER.info(world.toString());

        if(world.getBlockState(neighbor1).getBlock() instanceof  Conduit){//check if the neighbor block is a conduit
            //Main.LOGGER.info("west neighbor is conduit");
            neighborSizeCheck(state,world,neighbor1);//have neighbor check its neighbors
            world.setBlock(neighbor1, world.getBlockState(neighbor1).setValue(west, false), UPDATE_ALL);//set the neighbor block to point to this block
            onUpdate(world.getBlockState(neighbor1),world,neighbor1);
        }
        //else Main.LOGGER.info("check failed");

        if(world.getBlockState(neighbor2).getBlock() instanceof  Conduit){//check if the neighbor block is a conduit
            neighborSizeCheck(state,world,neighbor2);//have neighbor check its neighbors
            world.setBlock(neighbor2, world.getBlockState(neighbor2).setValue(east, false), UPDATE_ALL);//set the neighbor block to point to this block
            onUpdate(world.getBlockState(neighbor2),world,neighbor2);
        }

        if(world.getBlockState(neighbor3).getBlock() instanceof  Conduit){//check if the neighbor block is a conduit
            neighborSizeCheck(state,world,neighbor3);//have neighbor check its neighbors
            world.setBlock(neighbor3, world.getBlockState(neighbor3).setValue(north, false), UPDATE_ALL);//set the neighbor block to point to this block
            onUpdate(world.getBlockState(neighbor3),world,neighbor3);
        }

        if(world.getBlockState(neighbor4).getBlock() instanceof  Conduit){//check if the neighbor block is a conduit
            neighborSizeCheck(state,world,neighbor4);//have neighbor check its neighbors
            world.setBlock(neighbor4, world.getBlockState(neighbor4).setValue(south, false), UPDATE_ALL);//set the neighbor block to point to this block
            onUpdate(world.getBlockState(neighbor4),world,neighbor4);
        }

    }

    @SuppressWarnings({"all"})
    public boolean junctionBoxCheck(BlockState state){
        boolean box=false;
        if((state.getValue(north) && state.getValue(south) && state.getValue(east))
                ||(state.getValue(north) && state.getValue(south) && state.getValue(west))
                ||(state.getValue(north) && state.getValue(west) && state.getValue(east))
                ||(state.getValue(south) && state.getValue(west) && state.getValue(east))
                ||state.getValue(neighborLarger))
        {
            box=true;
        }
        else box=false;
        return box;
    }

    public VoxelShape junctionBoxVoxel(){
        VoxelShape shape = Shapes.empty();
        shape = Shapes.join(shape, Shapes.box(0, 0, 0, 1, 0.25, 1), BooleanOp.OR);

        return shape;
    }

    
    public void onUpdate (BlockState state, LevelAccessor world, BlockPos pos){
        world.setBlock(pos,state.setValue(junctionBox,junctionBoxCheck(state)),UPDATE_ALL);

        if(junctionBoxCheck(state))
        {
            state=world.getBlockState(pos);
            world.setBlock(pos,state.setValue(cableShape,2),UPDATE_ALL);
        }
        else if(state.getValue(north) && state.getValue(east)){
            state=world.getBlockState(pos);
            world.setBlock(pos,state.setValue(cableShape,4),UPDATE_ALL);
        }
        else if(state.getValue(south) && state.getValue(east)){
            state=world.getBlockState(pos);
            world.setBlock(pos,state.setValue(cableShape,5),UPDATE_ALL);
        }
        else if(state.getValue(south) && state.getValue(west)){
            state=world.getBlockState(pos);
            world.setBlock(pos,state.setValue(cableShape,6),UPDATE_ALL);
        }
        else if(state.getValue(north) && state.getValue(west)){
            state=world.getBlockState(pos);
            world.setBlock(pos,state.setValue(cableShape,7),UPDATE_ALL);
        }
        else if(state.getValue(north) || state.getValue(south)){
            state=world.getBlockState(pos);
            world.setBlock(pos,state.setValue(cableShape,0),UPDATE_ALL);
        }
        else if(state.getValue(west) || state.getValue(east)){
            state=world.getBlockState(pos);
            world.setBlock(pos,state.setValue(cableShape,1),UPDATE_ALL);
        }
        else{
            switch(state.getValue(FACING)){
                case NORTH,SOUTH -> {
                    state=world.getBlockState(pos);
                    world.setBlock(pos,state.setValue(cableShape,0),UPDATE_ALL);

                }
                case EAST,WEST -> {
                    state=world.getBlockState(pos);
                    world.setBlock(pos,state.setValue(cableShape,1),UPDATE_ALL);
                }
            }
        }
    }


    @Override
    public RenderShape getRenderShape(BlockState state) {
        // With inheriting from BlockWithEntity this defaults to INVISIBLE, so we need to change that!
        boolean hidden = state.getValueOrElse(HIDDEN,false);
        if(!hidden) {
            return RenderShape.MODEL;
        }else{
            return RenderShape.INVISIBLE;
        }
    }


    @SuppressWarnings({"all"})
    public void neighborSizeCheck(BlockState state, LevelAccessor world, BlockPos pos){

        boolean nL=false, sL=false, eL=false, wL=false;

        BlockPos east= new BlockPos(pos.getX()+1,pos.getY(),pos.getZ());//E//the location of the nigher block you want to check this should be reassigned for every block you want to check
        BlockPos west= new BlockPos(pos.getX()-1,pos.getY(),pos.getZ());//W
        BlockPos south= new BlockPos(pos.getX(),pos.getY(),pos.getZ()+1);//S
        BlockPos north= new BlockPos(pos.getX(),pos.getY(),pos.getZ()-1);//N

        //state=world.getBlockState(pos);
        if(world.getBlockState(north).getBlock() instanceof  Conduit) {//check if the neighbor block is a conduit
            if (state.getValue(cableLevel) > world.getBlockState(north).getValue(cableLevel)) {
                world.setBlock(north, world.getBlockState(north).setValue(neighborLarger, true), UPDATE_ALL);//set the neighbor block to point to this block
                //System.out.println("placed block is larger");
            } else if (state.getValue(cableLevel) < world.getBlockState(north).getValue(cableLevel)) {
                //System.out.println(state.getValue(neighborLarger));
                state = state.setValue(neighborLarger, true);
                nL=true;
                //Main.LOGGER.info("north is larger " + state.getValue(neighborLarger));
            }
        }

        //state=world.getBlockState(pos);
        if(world.getBlockState(south).getBlock() instanceof  Conduit) {//check if the neighbor block is a conduit
            if (state.getValue(cableLevel) > world.getBlockState(south).getValue(cableLevel)) {
                world.setBlock(south, world.getBlockState(south).setValue(neighborLarger, true), UPDATE_ALL);//set the neighbor block to point to this block
                //System.out.println("placed block is larger");
            } else if (state.getValue(cableLevel) < world.getBlockState(south).getValue(cableLevel)) {
                //System.out.println(state.getValue(neighborLarger));
                state = state.setValue(neighborLarger, true);
                sL=true;
                //Main.LOGGER.info("north is larger " + state.getValue(neighborLarger));
            }
        }

        //state=world.getBlockState(pos);
        if(world.getBlockState(east).getBlock() instanceof  Conduit) {//check if the neighbor block is a conduit
            //Main.LOGGER.info(state.getValue(cableLevel)+"");
            if (state.getValue(cableLevel) > world.getBlockState(east).getValue(cableLevel)) {
                world.setBlock(east, world.getBlockState(east).setValue(neighborLarger, true), UPDATE_ALL);//set the neighbor block to point to this block
                //System.out.println("placed block is larger");
            } else if (state.getValue(cableLevel) < world.getBlockState(east).getValue(cableLevel)) {
                //System.out.println(state.getValue(neighborLarger));
                state = state.setValue(neighborLarger, true);
                eL=true;
                //Main.LOGGER.info("north is larger " + state.getValue(neighborLarger));
            }
        }

        //state=world.getBlockState(pos);
        //Main.LOGGER.info((world.getBlockState(west).getBlock() instanceof  Conduit) +"");
        if(world.getBlockState(west).getBlock() instanceof  Conduit) {//check if the neighbor block is a conduit
            //Main.LOGGER.info("west neighbor is conduit");
            if (state.getValue(cableLevel) > world.getBlockState(west).getValue(cableLevel)) {
                world.setBlock(west, world.getBlockState(west).setValue(neighborLarger, true), UPDATE_ALL);//set the neighbor block to point to this block
                //System.out.println("placed block is larger");
            } else if (state.getValue(cableLevel) < world.getBlockState(west).getValue(cableLevel)) {
                //System.out.println(state.getValue(neighborLarger));
                state = state.setValue(neighborLarger, true);
                wL=true;
                //Main.LOGGER.info("north is larger " + state.getValue(neighborLarger));
            }
        }
        //Main.LOGGER.info("any larger " + !(nL||sL||eL||wL));
        //state=world.getBlockState(pos);
        if(!(nL||sL||eL||wL))
        {
            state=state.setValue(neighborLarger, false);
        }

    }

    @Override
    protected InteractionResult useItemOn(ItemStack itemStack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {

        if (state.getValue(junctionBox)) {
            String currentThread = Thread.currentThread().getName();
            //check witch thread the code is being executed on the server
            if (currentThread.equals("Server thread")) {//if the code is being executed on the server thread

                OpenConduitGuiPacket packet = new OpenConduitGuiPacket(/*world.getBlockEntity(pos).writeNbt()*/new CompoundTag(),pos);//add data to send to GUI here
                ServerPlayNetworking.send((ServerPlayer) player, packet);
            }

        }

        return super.useItemOn(itemStack, state, level, pos, player, hand, hitResult);
    }


    protected MapCodec<? extends BaseEntityBlock> getCodec() {
        //TODO figure out what goes here
        return null;
    }
}
