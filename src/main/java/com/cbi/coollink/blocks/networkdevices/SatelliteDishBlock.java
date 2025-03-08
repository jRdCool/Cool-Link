package com.cbi.coollink.blocks.networkdevices;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

import static com.cbi.coollink.Main.ASSEMBLED_BOOLEAN_PROPERTY;


public class SatelliteDishBlock extends Block {
    //All property definitions MUST be declared before the entry
    public static final EnumProperty<MultiBlockPartStates> multiBlockPose = EnumProperty.of("multiblockpart", MultiBlockPartStates.class);
    public SatelliteDishBlock(Settings settings) {
        super(settings);

        setDefaultState(getDefaultState()
                .with(ASSEMBLED_BOOLEAN_PROPERTY, false)
                .with(multiBlockPose, MultiBlockPartStates.NONE)
        );
    }
    public static final SatelliteDishBlock ENTRY = new SatelliteDishBlock(AbstractBlock.Settings.create().hardness(0.5f));
    public enum MultiBlockPartStates implements StringIdentifiable {
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
        public String asString() {
            return name;
        }
    }


    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> stateManager) {
        stateManager.add(ASSEMBLED_BOOLEAN_PROPERTY);
        stateManager.add(multiBlockPose);
    }

    @SuppressWarnings("deprecation")
    public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, ShapeContext context) {
        if(state.contains(multiBlockPose)) {
            MultiBlockPartStates s = state.get(multiBlockPose);
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
            return VoxelShapes.union(VoxelShapes.empty(), VoxelShapes.cuboid(0, 0, 0, 1, 1, 1));
        }else{
            return VoxelShapes.cuboid(0,0,0,1,1,1);
        }
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        //world.setBlockState(pos,state.with(multiBlockPose,multiBlockPartStates.D1));//how to set block states

        //find the bottom north-west corner of the structure
        BlockPos cornerBlock=new BlockPos(pos.getX(),pos.getY(),pos.getZ());
        BlockPos testingBlock =new BlockPos(cornerBlock.getX()-1,cornerBlock.getY(),cornerBlock.getZ());
        if(world.getBlockState(testingBlock).getBlock().equals(this) && world.getBlockState(testingBlock).get(ASSEMBLED_BOOLEAN_PROPERTY).equals(false)){
            cornerBlock=testingBlock;
        }
        testingBlock =new BlockPos(cornerBlock.getX(),cornerBlock.getY()-1,cornerBlock.getZ());
        if(world.getBlockState(testingBlock).getBlock().equals(this) && world.getBlockState(testingBlock).get(ASSEMBLED_BOOLEAN_PROPERTY).equals(false)){
            cornerBlock=testingBlock;
        }
        testingBlock =new BlockPos(cornerBlock.getX(),cornerBlock.getY(),cornerBlock.getZ()-1);
        if(world.getBlockState(testingBlock).getBlock().equals(this) && world.getBlockState(testingBlock).get(ASSEMBLED_BOOLEAN_PROPERTY).equals(false)){
            cornerBlock=testingBlock;
        }

        //check all blocks in the structure area to make sure that the whole structure is there
        boolean correctStructureFound=true;
        for(int i=0;i<2;i++){
            for(int j=0;j<2;j++){
                for(int k=0;k<2;k++){
                    testingBlock=new BlockPos(cornerBlock.getX()+i,cornerBlock.getY()+j,cornerBlock.getZ()+k);
                    if(!(world.getBlockState(testingBlock).getBlock().equals(this) && world.getBlockState(testingBlock).get(ASSEMBLED_BOOLEAN_PROPERTY).equals(false))){
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
            world.setBlockState(cornerBlock,state.with(multiBlockPose, MultiBlockPartStates.D1).with(ASSEMBLED_BOOLEAN_PROPERTY,true));
            settingBlock=new BlockPos(cornerBlock.getX()+1,cornerBlock.getY(),cornerBlock.getZ());
            world.setBlockState(settingBlock,state.with(multiBlockPose, MultiBlockPartStates.D2).with(ASSEMBLED_BOOLEAN_PROPERTY,true));
            settingBlock=new BlockPos(cornerBlock.getX()+1,cornerBlock.getY(),cornerBlock.getZ()+1);
            world.setBlockState(settingBlock,state.with(multiBlockPose, MultiBlockPartStates.D3).with(ASSEMBLED_BOOLEAN_PROPERTY,true));
            settingBlock=new BlockPos(cornerBlock.getX(),cornerBlock.getY(),cornerBlock.getZ()+1);
            world.setBlockState(settingBlock,state.with(multiBlockPose, MultiBlockPartStates.D4).with(ASSEMBLED_BOOLEAN_PROPERTY,true));

            //top
            settingBlock=new BlockPos(cornerBlock.getX(),cornerBlock.getY()+1,cornerBlock.getZ());
            world.setBlockState(settingBlock,state.with(multiBlockPose, MultiBlockPartStates.U1).with(ASSEMBLED_BOOLEAN_PROPERTY,true));
            settingBlock=new BlockPos(cornerBlock.getX()+1,cornerBlock.getY()+1,cornerBlock.getZ());
            world.setBlockState(settingBlock,state.with(multiBlockPose, MultiBlockPartStates.U2).with(ASSEMBLED_BOOLEAN_PROPERTY,true));
            settingBlock=new BlockPos(cornerBlock.getX()+1,cornerBlock.getY()+1,cornerBlock.getZ()+1);
            world.setBlockState(settingBlock,state.with(multiBlockPose, MultiBlockPartStates.U3).with(ASSEMBLED_BOOLEAN_PROPERTY,true));
            settingBlock=new BlockPos(cornerBlock.getX(),cornerBlock.getY()+1,cornerBlock.getZ()+1);
            world.setBlockState(settingBlock,state.with(multiBlockPose, MultiBlockPartStates.U4).with(ASSEMBLED_BOOLEAN_PROPERTY,true));
        }


    }

    @Override
    public void onBroken(WorldAccess world, BlockPos pos, BlockState state) {
        if(state.get(ASSEMBLED_BOOLEAN_PROPERTY).equals(true)){//if the structure is assembled
            //find the bottom north-west corner of the structure
            BlockPos cornerBlock=new BlockPos(pos.getX(),pos.getY(),pos.getZ());
            BlockPos testingBlock =new BlockPos(cornerBlock.getX()-1,cornerBlock.getY(),cornerBlock.getZ());
            if(world.getBlockState(testingBlock).getBlock().equals(this) && world.getBlockState(testingBlock).get(ASSEMBLED_BOOLEAN_PROPERTY).equals(true)){
                cornerBlock=testingBlock;
            }
            testingBlock =new BlockPos(cornerBlock.getX(),cornerBlock.getY()-1,cornerBlock.getZ());
            if(world.getBlockState(testingBlock).getBlock().equals(this) && world.getBlockState(testingBlock).get(ASSEMBLED_BOOLEAN_PROPERTY).equals(true)){
                cornerBlock=testingBlock;
            }
            testingBlock =new BlockPos(cornerBlock.getX(),cornerBlock.getY(),cornerBlock.getZ()-1);
            if(world.getBlockState(testingBlock).getBlock().equals(this) && world.getBlockState(testingBlock).get(ASSEMBLED_BOOLEAN_PROPERTY).equals(true)){
                cornerBlock=testingBlock;
            }

            //set all the blocks in the structure to not assembled
            BlockPos settingBlock;
            if(!cornerBlock.equals(pos))
                world.setBlockState(cornerBlock,state.with(multiBlockPose, MultiBlockPartStates.NONE).with(ASSEMBLED_BOOLEAN_PROPERTY,false),NOTIFY_ALL);
            settingBlock=new BlockPos(cornerBlock.getX()+1,cornerBlock.getY(),cornerBlock.getZ());
            if(!settingBlock.equals(pos))
                world.setBlockState(settingBlock,state.with(multiBlockPose, MultiBlockPartStates.NONE).with(ASSEMBLED_BOOLEAN_PROPERTY,false),NOTIFY_ALL);
            settingBlock=new BlockPos(cornerBlock.getX()+1,cornerBlock.getY(),cornerBlock.getZ()+1);
            if(!settingBlock.equals(pos))
                world.setBlockState(settingBlock,state.with(multiBlockPose, MultiBlockPartStates.NONE).with(ASSEMBLED_BOOLEAN_PROPERTY,false),NOTIFY_ALL);
            settingBlock=new BlockPos(cornerBlock.getX(),cornerBlock.getY(),cornerBlock.getZ()+1);
            if(!settingBlock.equals(pos))
                world.setBlockState(settingBlock,state.with(multiBlockPose, MultiBlockPartStates.NONE).with(ASSEMBLED_BOOLEAN_PROPERTY,false),NOTIFY_ALL);

            //top
            settingBlock=new BlockPos(cornerBlock.getX(),cornerBlock.getY()+1,cornerBlock.getZ());
            if(!settingBlock.equals(pos))
                world.setBlockState(settingBlock,state.with(multiBlockPose, MultiBlockPartStates.NONE).with(ASSEMBLED_BOOLEAN_PROPERTY,false),NOTIFY_ALL);
            settingBlock=new BlockPos(cornerBlock.getX()+1,cornerBlock.getY()+1,cornerBlock.getZ());
            if(!settingBlock.equals(pos))
                world.setBlockState(settingBlock,state.with(multiBlockPose, MultiBlockPartStates.NONE).with(ASSEMBLED_BOOLEAN_PROPERTY,false),NOTIFY_ALL);
            settingBlock=new BlockPos(cornerBlock.getX()+1,cornerBlock.getY()+1,cornerBlock.getZ()+1);
            if(!settingBlock.equals(pos))
                world.setBlockState(settingBlock,state.with(multiBlockPose, MultiBlockPartStates.NONE).with(ASSEMBLED_BOOLEAN_PROPERTY,false),NOTIFY_ALL);
            settingBlock=new BlockPos(cornerBlock.getX(),cornerBlock.getY()+1,cornerBlock.getZ()+1);
            if(!settingBlock.equals(pos))
                world.setBlockState(settingBlock,state.with(multiBlockPose, MultiBlockPartStates.NONE).with(ASSEMBLED_BOOLEAN_PROPERTY,false),NOTIFY_ALL);

        }

    }

    public VoxelShape voxelD1(){
        VoxelShape shape = VoxelShapes.empty();
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.9375, 0.4375, 0.9375, 1, 1, 1));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0, 0, 0.9375, 0.125, 0.125, 1));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.9375, 0, 0, 1, 0.125, 0.125));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0, 0, 0, 0.625, 0.625, 0.625));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.9375, 0.0625, 0.0625, 1, 0.125, 0.1875));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.9375, 0.0625, 0.1875, 1, 0.1875, 0.3125));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.9375, 0.125, 0.3125, 1, 0.25, 0.4375));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.9375, 0.1875, 0.4375, 1, 0.3125, 0.5625));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.9375, 0.25, 0.5625, 1, 0.375, 0.6875));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.9375, 0.375, 0.8125, 1, 0.5, 0.9375));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.9375, 0.3125, 0.6875, 1, 0.4375, 0.8125));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.8125, 0.375, 0.9375, 0.9375, 0.5, 1));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.5625, 0.25, 0.9375, 0.6875, 0.375, 1));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.4375, 0.1875, 0.9375, 0.5625, 0.3125, 1));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.3125, 0.125, 0.9375, 0.4375, 0.25, 1));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.1875, 0.0625, 0.9375, 0.3125, 0.1875, 1));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.0625, 0.0625, 0.9375, 0.1875, 0.125, 1));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.6875, 0.3125, 0.9375, 0.8125, 0.4375, 1));

        return shape;
    }
    public VoxelShape voxelD2(){
        VoxelShape shape = VoxelShapes.empty();
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0, 0.4375, 0.9375, 0.0625, 1, 1));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0, 0, 0, 0.0625, 0.125, 0.125));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.875, 0, 0.9375, 1, 0.125, 1));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0, 0.0625, 0.0625, 0.0625, 0.125, 0.1875));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0, 0.0625, 0.1875, 0.0625, 0.1875, 0.3125));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0, 0.125, 0.3125, 0.0625, 0.25, 0.4375));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0, 0.1875, 0.4375, 0.0625, 0.3125, 0.5625));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0, 0.25, 0.5625, 0.0625, 0.375, 0.6875));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0, 0.375, 0.8125, 0.0625, 0.5, 0.9375));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0, 0.3125, 0.6875, 0.0625, 0.4375, 0.8125));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.0625, 0.375, 0.9375, 0.1875, 0.5, 1));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.3125, 0.25, 0.9375, 0.4375, 0.375, 1));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.4375, 0.1875, 0.9375, 0.5625, 0.3125, 1));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.5625, 0.125, 0.9375, 0.6875, 0.25, 1));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.6875, 0.0625, 0.9375, 0.8125, 0.1875, 1));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.8125, 0.0625, 0.9375, 0.9375, 0.125, 1));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.1875, 0.3125, 0.9375, 0.3125, 0.4375, 1));

        return shape;
    }

    public VoxelShape voxelD3(){
        VoxelShape shape = VoxelShapes.empty();
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0, 0.4375, 0, 0.0625, 1, 0.0625));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.875, 0, 0, 1, 0.125, 0.0625));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0, 0, 0.875, 0.0625, 0.125, 1));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.0625, 0.375, 0, 0.1875, 0.5, 0.0625));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.3125, 0.25, 0, 0.4375, 0.375, 0.0625));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.4375, 0.1875, 0, 0.5625, 0.3125, 0.0625));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.5625, 0.125, 0, 0.6875, 0.25, 0.0625));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.6875, 0.0625, 0, 0.8125, 0.1875, 0.0625));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.8125, 0.0625, 0, 0.9375, 0.125, 0.0625));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.1875, 0.3125, 0, 0.3125, 0.4375, 0.0625));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0, 0.375, 0.0625, 0.0625, 0.5, 0.1875));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0, 0.25, 0.3125, 0.0625, 0.375, 0.4375));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0, 0.1875, 0.4375, 0.0625, 0.3125, 0.5625));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0, 0.125, 0.5625, 0.0625, 0.25, 0.6875));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0, 0.0625, 0.6875, 0.0625, 0.1875, 0.8125));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0, 0.0625, 0.8125, 0.0625, 0.125, 0.9375));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0, 0.3125, 0.1875, 0.0625, 0.4375, 0.3125));

        return shape;
    }

    public VoxelShape voxelD4(){
        VoxelShape shape = VoxelShapes.empty();
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.9375, 0.4375, 0, 1, 1, 0.0625));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0, 0, 0, 0.125, 0.125, 0.0625));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.9375, 0, 0.875, 1, 0.125, 1));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.9375, 0.375, 0.0625, 1, 0.5, 0.1875));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.9375, 0.25, 0.3125, 1, 0.375, 0.4375));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.9375, 0.1875, 0.4375, 1, 0.3125, 0.5625));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.9375, 0.125, 0.5625, 1, 0.25, 0.6875));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.9375, 0.0625, 0.6875, 1, 0.1875, 0.8125));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.9375, 0.0625, 0.8125, 1, 0.125, 0.9375));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.9375, 0.3125, 0.1875, 1, 0.4375, 0.3125));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.8125, 0.375, 0, 0.9375, 0.5, 0.0625));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.5625, 0.25, 0, 0.6875, 0.375, 0.0625));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.4375, 0.1875, 0, 0.5625, 0.3125, 0.0625));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.3125, 0.125, 0, 0.4375, 0.25, 0.0625));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.1875, 0.0625, 0, 0.3125, 0.1875, 0.0625));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.0625, 0.0625, 0, 0.1875, 0.125, 0.0625));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.6875, 0.3125, 0, 0.8125, 0.4375, 0.0625));

        return shape;
    }

    public VoxelShape voxelU1(){
        VoxelShape shape = VoxelShapes.empty();
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.3375, 0.12375, 0.3375, 1.0375, 0.31125, 0.4));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.3375, 0.12375, 0.3375, 0.4, 0.31125, 1.0375));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.5625, 0, 0.5625, 1, 0.0625, 1));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.625, 0.0625, 0.5625, 1, 0.125, 0.625));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.5625, 0.0625, 0.5625, 0.625, 0.125, 1));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.5625, 0.125, 0.375, 1, 0.1875, 0.5625));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.375, 0.125, 0.375, 0.5625, 0.1875, 1));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.5, 0.1875, 0.375, 1, 0.25, 0.5));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.375, 0.1875, 0.375, 0.5, 0.25, 1));

        return shape;
    }

    public VoxelShape voxelU2(){
        VoxelShape shape = VoxelShapes.empty();
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.6, 0.12375, 0.3375, 0.6625, 0.31125, 1.0375));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(-0.0375, 0.12375, 0.3375, 0.6625, 0.31125, 0.4));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0, 0, 0.5625, 0.4375, 0.0625, 1));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.375, 0.0625, 0.625, 0.4375, 0.125, 1));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0, 0.0625, 0.5625, 0.4375, 0.125, 0.625));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.4375, 0.125, 0.5625, 0.625, 0.1875, 1));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0, 0.125, 0.375, 0.625, 0.1875, 0.5625));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.5, 0.1875, 0.5, 0.625, 0.25, 1));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0, 0.1875, 0.375, 0.625, 0.25, 0.5));

        return shape;
    }

    public VoxelShape voxelU3(){
        VoxelShape shape = VoxelShapes.empty();
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(-0.0375, 0.12375, 0.6, 0.6625, 0.31125, 0.6625));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.6, 0.12375, -0.0375, 0.6625, 0.31125, 0.6625));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0, 0, 0, 0.4375, 0.0625, 0.4375));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0, 0.0625, 0.375, 0.375, 0.125, 0.4375));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.375, 0.0625, 0, 0.4375, 0.125, 0.4375));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0, 0.125, 0.4375, 0.4375, 0.1875, 0.625));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.4375, 0.125, 0, 0.625, 0.1875, 0.625));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0, 0.1875, 0.5, 0.5, 0.25, 0.625));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.5, 0.1875, 0, 0.625, 0.25, 0.625));

        return shape;
    }

    public VoxelShape voxelU4(){
        VoxelShape shape = VoxelShapes.empty();
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.3375, 0.12375, -0.0375, 0.4, 0.31125, 0.6625));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.3375, 0.12375, 0.6, 1.0375, 0.31125, 0.6625));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.5625, 0, 0, 1, 0.0625, 0.4375));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.5625, 0.0625, 0, 0.625, 0.125, 0.375));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.5625, 0.0625, 0.375, 1, 0.125, 0.4375));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.375, 0.125, 0, 0.5625, 0.1875, 0.4375));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.375, 0.125, 0.4375, 1, 0.1875, 0.625));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.375, 0.1875, 0, 0.5, 0.25, 0.5));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.375, 0.1875, 0.5, 1, 0.25, 0.625));

        return shape;
    }



}
