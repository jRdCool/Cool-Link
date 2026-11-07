package com.cbi.coollink.blocks.networkdevices;

import com.cbi.coollink.Main;
import com.cbi.coollink.blocks.blockentities.SatelliteDishBlockEntity;
import com.cbi.coollink.rendering.IWireNode;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import java.util.function.BiConsumer;

import static com.cbi.coollink.Main.ASSEMBLED_BOOLEAN_PROPERTY;


public class SatelliteDishBlock extends BaseEntityBlock {
    //All property definitions MUST be declared before the entry
    public static final EnumProperty<MultiBlockPartStates> multiBlockPose = EnumProperty.create("multiblockpart", MultiBlockPartStates.class);
    public SatelliteDishBlock(BlockBehaviour.Properties settings) {
        super(settings);

        registerDefaultState(defaultBlockState()
                .setValue(ASSEMBLED_BOOLEAN_PROPERTY, false)
                .setValue(multiBlockPose, MultiBlockPartStates.NONE)
        );
    }

    //@Override
    //protected MapCodec<? extends BlockWithEntity> getCodec() {
    //    return createCodec(SatelliteDishBlock::new);
    //}

    public static final Identifier ID = Identifier.fromNamespaceAndPath(Main.namespace,"satellite_dish");
    public static final ResourceKey<Block> BLOCK_KEY = Main.createBlockRegistryKey(ID);
    public static final ResourceKey<Item> ITEM_KEY = Main.createItemRegistryKey(ID);

    public static final SatelliteDishBlock ENTRY = new SatelliteDishBlock(BlockBehaviour.Properties.of().strength(0.5f));

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NonNull BlockPos pos, @NonNull BlockState state) {
        return new SatelliteDishBlockEntity(pos,state);
    }

    public enum MultiBlockPartStates implements StringRepresentable {
        D1("d1"),
        D2("d2"),
        D3("d3"),
        D4("d4"),
        U1("u1"),
        U2("u2"),
        U3("u3"),
        U4("u4"),
        NONE("none");
        /* top then bottom arrangements
                U3 U4  west |   D3 D4 west
                U2 U1  west |   D2 D1 west
                north           north
         */
        private final String name;

        MultiBlockPartStates(String name) {
            this.name = name;
        }

        @Override
        public @NonNull String getSerializedName() {
            return name;
        }
    }


    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> stateManager) {
        stateManager.add(ASSEMBLED_BOOLEAN_PROPERTY);
        stateManager.add(multiBlockPose);
    }

    protected @NonNull VoxelShape getShape(BlockState state, @NonNull BlockGetter level, @NonNull BlockPos pos, @NonNull CollisionContext context) {
        if(state.hasProperty(multiBlockPose)) {
            MultiBlockPartStates s = state.getValue(multiBlockPose);
            switch (s) {
                case D1 -> {
                    return voxelD1();
                }
                case D2 -> {
                    return voxelD2();
                }
                case D3 -> {
                    return voxelD3();
                }
                case D4 -> {
                    return voxelD4();
                }

                case U1 -> {
                    return voxelU1();
                }
                case U2 -> {
                    return voxelU2();
                }
                case U3 -> {
                    return voxelU3();
                }
                case U4 -> {
                    return voxelU4();
                }

            }
            return Shapes.join(Shapes.empty(), Shapes.box(0, 0, 0, 1, 1, 1), BooleanOp.OR);
        }else{
            return Shapes.box(0,0,0,1,1,1);
        }
    }

    @Override
    public void setPlacedBy(Level world, BlockPos pos, @NonNull BlockState state, @org.jspecify.annotations.Nullable LivingEntity placer, @NonNull ItemStack itemStack) {
        //world.setBlock(pos,state.setValue(multiBlockPose,multiBlockPartStates.D1));//how to set block states

        //find the bottom north-west corner of the structure
        BlockPos cornerBlock=new BlockPos(pos.getX(),pos.getY(),pos.getZ());
        BlockPos testingBlock =new BlockPos(cornerBlock.getX()-1,cornerBlock.getY(),cornerBlock.getZ());
        if(world.getBlockState(testingBlock).getBlock().equals(this) && world.getBlockState(testingBlock).getValue(ASSEMBLED_BOOLEAN_PROPERTY).equals(false)){
            cornerBlock=testingBlock;
        }
        testingBlock =new BlockPos(cornerBlock.getX(),cornerBlock.getY()-1,cornerBlock.getZ());
        if(world.getBlockState(testingBlock).getBlock().equals(this) && world.getBlockState(testingBlock).getValue(ASSEMBLED_BOOLEAN_PROPERTY).equals(false)){
            cornerBlock=testingBlock;
        }
        testingBlock =new BlockPos(cornerBlock.getX(),cornerBlock.getY(),cornerBlock.getZ()-1);
        if(world.getBlockState(testingBlock).getBlock().equals(this) && world.getBlockState(testingBlock).getValue(ASSEMBLED_BOOLEAN_PROPERTY).equals(false)){
            cornerBlock=testingBlock;
        }

        //check all blocks in the structure area to make sure that the whole structure is there
        boolean correctStructureFound=true;
        for(int i=0;i<2;i++){
            for(int j=0;j<2;j++){
                for(int k=0;k<2;k++){
                    testingBlock=new BlockPos(cornerBlock.getX()+i,cornerBlock.getY()+j,cornerBlock.getZ()+k);
                    if(!(world.getBlockState(testingBlock).getBlock().equals(this) && world.getBlockState(testingBlock).getValue(ASSEMBLED_BOOLEAN_PROPERTY).equals(false))){
                        correctStructureFound=false;
                        break;
                    }
                }
                if(!correctStructureFound)
                    break;
            }
            if(!correctStructureFound)
                break;
        }

        if(correctStructureFound){//if the whole multi block structure is there then set the appropriate block states of the structure
            //bottom
            BlockPos settingBlock;
            world.setBlock(cornerBlock,state.setValue(multiBlockPose, MultiBlockPartStates.D1).setValue(ASSEMBLED_BOOLEAN_PROPERTY,true),0);
            settingBlock=new BlockPos(cornerBlock.getX()+1,cornerBlock.getY(),cornerBlock.getZ());
            world.setBlock(settingBlock,state.setValue(multiBlockPose, MultiBlockPartStates.D2).setValue(ASSEMBLED_BOOLEAN_PROPERTY,true),0);
            settingBlock=new BlockPos(cornerBlock.getX()+1,cornerBlock.getY(),cornerBlock.getZ()+1);
            world.setBlock(settingBlock,state.setValue(multiBlockPose, MultiBlockPartStates.D3).setValue(ASSEMBLED_BOOLEAN_PROPERTY,true),0);
            settingBlock=new BlockPos(cornerBlock.getX(),cornerBlock.getY(),cornerBlock.getZ()+1);
            world.setBlock(settingBlock,state.setValue(multiBlockPose, MultiBlockPartStates.D4).setValue(ASSEMBLED_BOOLEAN_PROPERTY,true),0);

            //top
            settingBlock=new BlockPos(cornerBlock.getX(),cornerBlock.getY()+1,cornerBlock.getZ());
            world.setBlock(settingBlock,state.setValue(multiBlockPose, MultiBlockPartStates.U1).setValue(ASSEMBLED_BOOLEAN_PROPERTY,true),0);
            settingBlock=new BlockPos(cornerBlock.getX()+1,cornerBlock.getY()+1,cornerBlock.getZ());
            world.setBlock(settingBlock,state.setValue(multiBlockPose, MultiBlockPartStates.U2).setValue(ASSEMBLED_BOOLEAN_PROPERTY,true),0);
            settingBlock=new BlockPos(cornerBlock.getX()+1,cornerBlock.getY()+1,cornerBlock.getZ()+1);
            world.setBlock(settingBlock,state.setValue(multiBlockPose, MultiBlockPartStates.U3).setValue(ASSEMBLED_BOOLEAN_PROPERTY,true),0);
            settingBlock=new BlockPos(cornerBlock.getX(),cornerBlock.getY()+1,cornerBlock.getZ()+1);
            world.setBlock(settingBlock,state.setValue(multiBlockPose, MultiBlockPartStates.U4).setValue(ASSEMBLED_BOOLEAN_PROPERTY,true),0);
        }


    }

    @Override
    public void destroy(@NonNull LevelAccessor world, @NonNull BlockPos pos, BlockState state) {
        if(state.getValue(ASSEMBLED_BOOLEAN_PROPERTY).equals(true)){//if the structure is assembled
            //find the bottom north-west corner of the structure
            BlockPos cornerBlock=new BlockPos(pos.getX(),pos.getY(),pos.getZ());
            BlockPos testingBlock =new BlockPos(cornerBlock.getX()-1,cornerBlock.getY(),cornerBlock.getZ());
            if(world.getBlockState(testingBlock).getBlock().equals(this) && world.getBlockState(testingBlock).getValue(ASSEMBLED_BOOLEAN_PROPERTY).equals(true)){
                cornerBlock=testingBlock;
            }
            testingBlock =new BlockPos(cornerBlock.getX(),cornerBlock.getY()-1,cornerBlock.getZ());
            if(world.getBlockState(testingBlock).getBlock().equals(this) && world.getBlockState(testingBlock).getValue(ASSEMBLED_BOOLEAN_PROPERTY).equals(true)){
                cornerBlock=testingBlock;
            }
            testingBlock =new BlockPos(cornerBlock.getX(),cornerBlock.getY(),cornerBlock.getZ()-1);
            if(world.getBlockState(testingBlock).getBlock().equals(this) && world.getBlockState(testingBlock).getValue(ASSEMBLED_BOOLEAN_PROPERTY).equals(true)){
                cornerBlock=testingBlock;
            }

            //set all the blocks in the structure to not assembled
            BlockPos settingBlock;
            if(!cornerBlock.equals(pos))
                world.setBlock(cornerBlock,state.setValue(multiBlockPose, MultiBlockPartStates.NONE).setValue(ASSEMBLED_BOOLEAN_PROPERTY,false),UPDATE_ALL);
            settingBlock=new BlockPos(cornerBlock.getX()+1,cornerBlock.getY(),cornerBlock.getZ());
            if(!settingBlock.equals(pos))
                world.setBlock(settingBlock,state.setValue(multiBlockPose, MultiBlockPartStates.NONE).setValue(ASSEMBLED_BOOLEAN_PROPERTY,false),UPDATE_ALL);
            settingBlock=new BlockPos(cornerBlock.getX()+1,cornerBlock.getY(),cornerBlock.getZ()+1);
            if(!settingBlock.equals(pos))
                world.setBlock(settingBlock,state.setValue(multiBlockPose, MultiBlockPartStates.NONE).setValue(ASSEMBLED_BOOLEAN_PROPERTY,false),UPDATE_ALL);
            settingBlock=new BlockPos(cornerBlock.getX(),cornerBlock.getY(),cornerBlock.getZ()+1);
            if(!settingBlock.equals(pos))
                world.setBlock(settingBlock,state.setValue(multiBlockPose, MultiBlockPartStates.NONE).setValue(ASSEMBLED_BOOLEAN_PROPERTY,false),UPDATE_ALL);

            //top
            settingBlock=new BlockPos(cornerBlock.getX(),cornerBlock.getY()+1,cornerBlock.getZ());
            if(!settingBlock.equals(pos))
                world.setBlock(settingBlock,state.setValue(multiBlockPose, MultiBlockPartStates.NONE).setValue(ASSEMBLED_BOOLEAN_PROPERTY,false),UPDATE_ALL);
            settingBlock=new BlockPos(cornerBlock.getX()+1,cornerBlock.getY()+1,cornerBlock.getZ());
            if(!settingBlock.equals(pos))
                world.setBlock(settingBlock,state.setValue(multiBlockPose, MultiBlockPartStates.NONE).setValue(ASSEMBLED_BOOLEAN_PROPERTY,false),UPDATE_ALL);
            settingBlock=new BlockPos(cornerBlock.getX()+1,cornerBlock.getY()+1,cornerBlock.getZ()+1);
            if(!settingBlock.equals(pos))
                world.setBlock(settingBlock,state.setValue(multiBlockPose, MultiBlockPartStates.NONE).setValue(ASSEMBLED_BOOLEAN_PROPERTY,false),UPDATE_ALL);
            settingBlock=new BlockPos(cornerBlock.getX(),cornerBlock.getY()+1,cornerBlock.getZ()+1);
            if(!settingBlock.equals(pos))
                world.setBlock(settingBlock,state.setValue(multiBlockPose, MultiBlockPartStates.NONE).setValue(ASSEMBLED_BOOLEAN_PROPERTY,false),UPDATE_ALL);

        }

    }


    @Override
    public @NonNull BlockState playerWillDestroy(Level world, @NonNull BlockPos pos, @NonNull BlockState state, @NonNull Player player) {
        //delete both ends of the connection when the block is broken
        BlockEntity be = world.getBlockEntity(pos);
        if(be instanceof SatelliteDishBlockEntity sbe) {
            BlockEntity be2 = sbe.getActiveBlock();
            if (be2 instanceof IWireNode self) {
                for (int i = 0; i < self.getNodeCount(); i++) {
                    if (!self.hasConnection(i)) {
                        //Main.LOGGER.info("NO conncetion at "+i+" "+be2.getPos());
                        continue;
                    }
                    //Main.LOGGER.info("Conncection at "+1);
                    BlockEntity obe = world.getBlockEntity(self.getLocalNode(i).getTargetPos());
                    //Main.LOGGER.info(obe.toString()+" "+self.getLocalNode(i).getTargetPos());
                    if (obe instanceof IWireNode other) {
                        other.removeNode(self.getOtherNodeIndex(i));
                    }
                }
                self.removeNode(0);
            }
        }
        return super.playerWillDestroy(world, pos, state, player);
    }

    @Override
    protected void onExplosionHit(@NonNull BlockState state, ServerLevel level, @NonNull BlockPos pos, @NonNull Explosion explosion, @NonNull BiConsumer<ItemStack, BlockPos> onHit) {
        //delete both ends of the connection when the block is broken
        BlockEntity be = level.getBlockEntity(pos);
        if(be instanceof SatelliteDishBlockEntity sbe) {
            BlockEntity be2 = sbe.getActiveBlock();
            if (be2 instanceof IWireNode self) {
                self.removeNode(0);
                for (int i = 0; i < self.getNodeCount(); i++) {
                    if (!self.hasConnection(i)) continue;
                    BlockEntity obe = level.getBlockEntity(self.getLocalNode(i).getTargetPos());
                    if (obe instanceof IWireNode other) {
                        other.removeNode(self.getOtherNodeIndex(i));
                    }
                }
            }
        }
        super.onExplosionHit(state, level, pos, explosion, onHit);
        destroy(level,pos,state);
    }

    public VoxelShape voxelD1(){
        VoxelShape shape = Shapes.empty();
        shape = Shapes.join(shape, Shapes.box(0.9375, 0.4375, 0.9375, 1, 1, 1),BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0, 0, 0.9375, 0.125, 0.125, 1),BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.9375, 0, 0, 1, 0.125, 0.125),BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0, 0, 0, 0.625, 0.625, 0.625),BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.9375, 0.0625, 0.0625, 1, 0.125, 0.1875),BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.9375, 0.0625, 0.1875, 1, 0.1875, 0.3125),BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.9375, 0.125, 0.3125, 1, 0.25, 0.4375),BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.9375, 0.1875, 0.4375, 1, 0.3125, 0.5625),BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.9375, 0.25, 0.5625, 1, 0.375, 0.6875),BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.9375, 0.375, 0.8125, 1, 0.5, 0.9375),BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.9375, 0.3125, 0.6875, 1, 0.4375, 0.8125),BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.8125, 0.375, 0.9375, 0.9375, 0.5, 1),BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.5625, 0.25, 0.9375, 0.6875, 0.375, 1),BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.4375, 0.1875, 0.9375, 0.5625, 0.3125, 1),BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.3125, 0.125, 0.9375, 0.4375, 0.25, 1),BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.1875, 0.0625, 0.9375, 0.3125, 0.1875, 1),BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.0625, 0.0625, 0.9375, 0.1875, 0.125, 1),BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.6875, 0.3125, 0.9375, 0.8125, 0.4375, 1),BooleanOp.OR);

        return shape;
    }
    public VoxelShape voxelD2(){
        VoxelShape shape = Shapes.empty();
        shape = Shapes.join(shape, Shapes.box(0, 0.4375, 0.9375, 0.0625, 1, 1),BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0, 0, 0, 0.0625, 0.125, 0.125),BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.875, 0, 0.9375, 1, 0.125, 1),BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0, 0.0625, 0.0625, 0.0625, 0.125, 0.1875),BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0, 0.0625, 0.1875, 0.0625, 0.1875, 0.3125),BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0, 0.125, 0.3125, 0.0625, 0.25, 0.4375),BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0, 0.1875, 0.4375, 0.0625, 0.3125, 0.5625),BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0, 0.25, 0.5625, 0.0625, 0.375, 0.6875),BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0, 0.375, 0.8125, 0.0625, 0.5, 0.9375),BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0, 0.3125, 0.6875, 0.0625, 0.4375, 0.8125),BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.0625, 0.375, 0.9375, 0.1875, 0.5, 1),BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.3125, 0.25, 0.9375, 0.4375, 0.375, 1),BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.4375, 0.1875, 0.9375, 0.5625, 0.3125, 1),BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.5625, 0.125, 0.9375, 0.6875, 0.25, 1),BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.6875, 0.0625, 0.9375, 0.8125, 0.1875, 1),BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.8125, 0.0625, 0.9375, 0.9375, 0.125, 1),BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.1875, 0.3125, 0.9375, 0.3125, 0.4375, 1),BooleanOp.OR);

        return shape;
    }

    public VoxelShape voxelD3(){
        VoxelShape shape = Shapes.empty();
        shape = Shapes.join(shape, Shapes.box(0, 0.4375, 0, 0.0625, 1, 0.0625),BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.875, 0, 0, 1, 0.125, 0.0625),BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0, 0, 0.875, 0.0625, 0.125, 1),BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.0625, 0.375, 0, 0.1875, 0.5, 0.0625),BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.3125, 0.25, 0, 0.4375, 0.375, 0.0625),BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.4375, 0.1875, 0, 0.5625, 0.3125, 0.0625),BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.5625, 0.125, 0, 0.6875, 0.25, 0.0625),BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.6875, 0.0625, 0, 0.8125, 0.1875, 0.0625),BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.8125, 0.0625, 0, 0.9375, 0.125, 0.0625),BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.1875, 0.3125, 0, 0.3125, 0.4375, 0.0625),BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0, 0.375, 0.0625, 0.0625, 0.5, 0.1875),BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0, 0.25, 0.3125, 0.0625, 0.375, 0.4375),BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0, 0.1875, 0.4375, 0.0625, 0.3125, 0.5625),BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0, 0.125, 0.5625, 0.0625, 0.25, 0.6875),BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0, 0.0625, 0.6875, 0.0625, 0.1875, 0.8125),BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0, 0.0625, 0.8125, 0.0625, 0.125, 0.9375),BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0, 0.3125, 0.1875, 0.0625, 0.4375, 0.3125),BooleanOp.OR);

        return shape;
    }

    public VoxelShape voxelD4(){
        VoxelShape shape = Shapes.empty();
        shape = Shapes.join(shape, Shapes.box(0.9375, 0.4375, 0, 1, 1, 0.0625),BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0, 0, 0, 0.125, 0.125, 0.0625),BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.9375, 0, 0.875, 1, 0.125, 1),BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.9375, 0.375, 0.0625, 1, 0.5, 0.1875),BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.9375, 0.25, 0.3125, 1, 0.375, 0.4375),BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.9375, 0.1875, 0.4375, 1, 0.3125, 0.5625),BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.9375, 0.125, 0.5625, 1, 0.25, 0.6875),BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.9375, 0.0625, 0.6875, 1, 0.1875, 0.8125),BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.9375, 0.0625, 0.8125, 1, 0.125, 0.9375),BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.9375, 0.3125, 0.1875, 1, 0.4375, 0.3125),BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.8125, 0.375, 0, 0.9375, 0.5, 0.0625),BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.5625, 0.25, 0, 0.6875, 0.375, 0.0625),BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.4375, 0.1875, 0, 0.5625, 0.3125, 0.0625),BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.3125, 0.125, 0, 0.4375, 0.25, 0.0625),BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.1875, 0.0625, 0, 0.3125, 0.1875, 0.0625),BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.0625, 0.0625, 0, 0.1875, 0.125, 0.0625),BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.6875, 0.3125, 0, 0.8125, 0.4375, 0.0625),BooleanOp.OR);

        return shape;
    }

    public VoxelShape voxelU1(){
        VoxelShape shape = Shapes.empty();
        shape = Shapes.join(shape, Shapes.box(0.3375, 0.12375, 0.3375, 1.0375, 0.31125, 0.4),BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.3375, 0.12375, 0.3375, 0.4, 0.31125, 1.0375),BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.5625, 0, 0.5625, 1, 0.0625, 1),BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.625, 0.0625, 0.5625, 1, 0.125, 0.625),BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.5625, 0.0625, 0.5625, 0.625, 0.125, 1),BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.5625, 0.125, 0.375, 1, 0.1875, 0.5625),BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.375, 0.125, 0.375, 0.5625, 0.1875, 1),BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.5, 0.1875, 0.375, 1, 0.25, 0.5),BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.375, 0.1875, 0.375, 0.5, 0.25, 1),BooleanOp.OR);

        return shape;
    }

    public VoxelShape voxelU2(){
        VoxelShape shape = Shapes.empty();
        shape = Shapes.join(shape, Shapes.box(0.6, 0.12375, 0.3375, 0.6625, 0.31125, 1.0375),BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(-0.0375, 0.12375, 0.3375, 0.6625, 0.31125, 0.4),BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0, 0, 0.5625, 0.4375, 0.0625, 1),BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.375, 0.0625, 0.625, 0.4375, 0.125, 1),BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0, 0.0625, 0.5625, 0.4375, 0.125, 0.625),BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.4375, 0.125, 0.5625, 0.625, 0.1875, 1),BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0, 0.125, 0.375, 0.625, 0.1875, 0.5625),BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.5, 0.1875, 0.5, 0.625, 0.25, 1),BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0, 0.1875, 0.375, 0.625, 0.25, 0.5),BooleanOp.OR);

        return shape;
    }

    public VoxelShape voxelU3(){
        VoxelShape shape = Shapes.empty();
        shape = Shapes.join(shape, Shapes.box(-0.0375, 0.12375, 0.6, 0.6625, 0.31125, 0.6625),BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.6, 0.12375, -0.0375, 0.6625, 0.31125, 0.6625),BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0, 0, 0, 0.4375, 0.0625, 0.4375),BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0, 0.0625, 0.375, 0.375, 0.125, 0.4375),BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.375, 0.0625, 0, 0.4375, 0.125, 0.4375),BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0, 0.125, 0.4375, 0.4375, 0.1875, 0.625),BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.4375, 0.125, 0, 0.625, 0.1875, 0.625),BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0, 0.1875, 0.5, 0.5, 0.25, 0.625),BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.5, 0.1875, 0, 0.625, 0.25, 0.625),BooleanOp.OR);

        return shape;
    }

    public VoxelShape voxelU4(){
        VoxelShape shape = Shapes.empty();
        shape = Shapes.join(shape, Shapes.box(0.3375, 0.12375, -0.0375, 0.4, 0.31125, 0.6625),BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.3375, 0.12375, 0.6, 1.0375, 0.31125, 0.6625),BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.5625, 0, 0, 1, 0.0625, 0.4375),BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.5625, 0.0625, 0, 0.625, 0.125, 0.375),BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.5625, 0.0625, 0.375, 1, 0.125, 0.4375),BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.375, 0.125, 0, 0.5625, 0.1875, 0.4375),BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.375, 0.125, 0.4375, 1, 0.1875, 0.625),BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.375, 0.1875, 0, 0.5, 0.25, 0.5),BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.375, 0.1875, 0.5, 1, 0.25, 0.625),BooleanOp.OR);

        return shape;
    }

    @Override
    protected @NonNull RenderShape getRenderShape(@NonNull BlockState state) {
        return RenderShape.MODEL;
    }
}
