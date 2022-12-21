package com.cbi.coollink.blocks;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
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
    public SatelliteDishBlock(Settings settings) {
        super(settings);
        setDefaultState(getDefaultState()
                .with(ASSEMBLED_BOOLEAN_PROPERTY, false)
                .with(multiBlockPose, MultiBlockPartStates.NONE)
        );
    }
    public static final SatelliteDishBlock ENTRY = new SatelliteDishBlock(FabricBlockSettings.of(Material.CARPET).hardness(0.5f));
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
    static EnumProperty multiBlockPose = EnumProperty.of("multiblockpart", MultiBlockPartStates.class);
    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> stateManager) {
        assignStates();
        stateManager.add(ASSEMBLED_BOOLEAN_PROPERTY);
        stateManager.add(multiBlockPose);
    }

    static void assignStates(){
        multiBlockPose = EnumProperty.of("multiblockpart", MultiBlockPartStates.class);
    }

    public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, ShapeContext context) {
        MultiBlockPartStates s= (MultiBlockPartStates) state.get(multiBlockPose);
        switch (s){
            case D1 -> {return voxelD1();}
            case D2 -> {return voxelD2();}
            case D3 -> {return voxelD3();}
            case D4 -> {return voxelD4();}

            case U1 -> {return voxelU1();}
            case U2 -> {return voxelU2();}
            case U3 -> {return voxelU3();}
            case U4 -> {return voxelU4();}

        }
        return VoxelShapes.union(VoxelShapes.empty(), VoxelShapes.cuboid(0, 0, 0, 1, 1, 1));
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
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.0625, 0, 0.9375, 1.0625, 0.125, 1));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.9375, 0, 0, 1, 0.125, 0.125));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.9375, 0, 0.0625, 1, 0.125, 1.0625));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0, 0, 0, 0.625, 0.625, 0.625));

        return shape;
    }
    public VoxelShape voxelD2(){
        VoxelShape shape = VoxelShapes.empty();
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0, 0.4375, 0.9375, 0.0625, 1, 1));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0, 0, 0, 0.0625, 0.125, 0.125));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0, 0, 0.0625, 0.0625, 0.125, 1.0625));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.875, 0, 0.9375, 1, 0.125, 1));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(-0.0625, 0, 0.9375, 0.9375, 0.125, 1));

        return shape;
    }

    public VoxelShape voxelD3(){
        VoxelShape shape = VoxelShapes.empty();
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0, 0.4375, 0, 0.0625, 1, 0.0625));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.875, 0, 0, 1, 0.125, 0.0625));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(-0.0625, 0, 0, 0.9375, 0.125, 0.0625));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0, 0, 0.875, 0.0625, 0.125, 1));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0, 0, -0.0625, 0.0625, 0.125, 0.9375));

        return shape;
    }

    public VoxelShape voxelD4(){
        VoxelShape shape = VoxelShapes.empty();
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.9375, 0.4375, 0, 1, 1, 0.0625));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0, 0, 0, 0.125, 0.125, 0.0625));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.0625, 0, 0, 1.0625, 0.125, 0.0625));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.9375, 0, 0.875, 1, 0.125, 1));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.9375, 0, -0.0625, 1, 0.125, 0.9375));

        return shape;
    }

    public VoxelShape voxelU1(){
        VoxelShape shape = VoxelShapes.empty();
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.75, 0.0625, 0.75, 1, 0.0625, 1));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.5625, 0.0625, 0.5625, 1, 0.0625, 0.75));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.5625, 0.0625, 0.5625, 0.75, 0.0625, 1));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.375, 0.1343749999999999, 0.326875, 1, 0.1343749999999999, 0.576875));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.326875, 0.1343749999999999, 0.389375, 0.576875, 0.1343749999999999, 1.014375));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.3375, 0.12375000000000003, 0.3375, 1.0374999999999996, 0.31125, 0.4));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.3374999999999999, 0.12375000000000003, 0.3375, 0.3999999999999999, 0.31125, 1.0375));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.40000000000000013, 0.12375000000000003, 0.4, 1.0374999999999999, 0.12375000000000003, 0.5875));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.3999999999999999, 0.12375000000000003, 0.5875000000000001, 0.5874999999999999, 0.12375000000000003, 1.0374999999999999));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.5625, 0, 0.5625, 0.5625, 0.125, 1));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.5625, 0, 0.5625, 1, 0.125, 0.5625));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.5625, 0, 0.5625, 1, 0, 1));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.96875, 0.3125, 0.375, 1.03125, 1, 0.4375));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.375, 0.3125, 0.96875, 0.4375, 1, 1.03125));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.859375, 0.735625, 0.96875, 1.015625, 0.798125, 1.03125));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.96875, 0.735625, 0.859375, 1.03125, 0.798125, 1.015625));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.96875, 0.6575, 0.96875, 1.03125, 0.75125, 1.03125));

        return shape;
    }

    public VoxelShape voxelU2(){
        VoxelShape shape = VoxelShapes.empty();
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0, 0.0625, 0.75, 0.25, 0.0625, 1));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.25, 0.0625, 0.5625, 0.4375, 0.0625, 1));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0, 0.0625, 0.5625, 0.4375, 0.0625, 0.75));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.423125, 0.1343749999999999, 0.375, 0.673125, 0.1343749999999999, 1));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0, 0.1343749999999999, 0.326875, 0.625, 0.1343749999999999, 0.576875));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(-0.03749999999999987, 0.12375000000000003, 0.3375, 0.6624999999999999, 0.31125, 0.4));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.6000000000000001, 0.12375000000000003, 0.3375, 0.6625000000000001, 0.31125, 1.0375));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(-0.03749999999999987, 0.12375000000000003, 0.4, 0.5999999999999999, 0.12375000000000003, 0.5875));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.4125000000000001, 0.12375000000000003, 0.5875000000000001, 0.6000000000000001, 0.12375000000000003, 1.0374999999999999));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.4375, 0, 0.5625, 0.4375, 0.125, 1));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0, 0, 0.5625, 0.4375, 0.125, 0.5625));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0, 0, 0.5625, 0.4375, 0, 1));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(-0.03125, 0.3125, 0.375, 0.03125, 1, 0.4375));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.5625, 0.3125, 0.96875, 0.625, 1, 1.03125));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(-0.015625, 0.735625, 0.96875, 0.140625, 0.798125, 1.03125));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(-0.03125, 0.735625, 0.859375, 0.03125, 0.798125, 1.015625));

        return shape;
    }

    public VoxelShape voxelU3(){
        VoxelShape shape = VoxelShapes.empty();
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0, 0.0625, 0, 0.25, 0.0625, 0.25));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0, 0.0625, 0.25, 0.4375, 0.0625, 0.4375));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.25, 0.0625, 0, 0.4375, 0.0625, 0.4375));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0, 0.1343749999999999, 0.423125, 0.625, 0.1343749999999999, 0.673125));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.423125, 0.1343749999999999, 0, 0.673125, 0.1343749999999999, 0.625));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(-0.03750000000000009, 0.12375000000000003, 0.6000000000000001, 0.6625000000000001, 0.31125, 0.6625000000000001));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.6000000000000001, 0.12375000000000003, -0.03750000000000009, 0.6625000000000001, 0.31125, 0.6625000000000001));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.025000000000000133, 0.12375000000000003, 0.4125000000000001, 0.5999999999999999, 0.12375000000000003, 0.6000000000000001));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.4125000000000001, 0.12375000000000003, -0.03749999999999987, 0.6000000000000001, 0.12375000000000003, 0.41249999999999987));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.4375, 0, 0, 0.4375, 0.125, 0.4375));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0, 0, 0.4375, 0.4375, 0.125, 0.4375));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0, 0, 0, 0.4375, 0, 0.4375));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(-0.03125, 0.3125, 0.5625, 0.03125, 1, 0.625));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.5625, 0.3125, -0.03125, 0.625, 1, 0.03125));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(-0.015625, 0.735625, -0.03125, 0.140625, 0.798125, 0.03125));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(-0.03125, 0.735625, -0.015625, 0.03125, 0.798125, 0.140625));

        return shape;
    }

    public VoxelShape voxelU4(){
        VoxelShape shape = VoxelShapes.empty();
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.75, 0.0625, 0, 1, 0.0625, 0.25));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.5625, 0.0625, 0.25, 1, 0.0625, 0.4375));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.5625, 0.0625, 0, 0.75, 0.0625, 0.4375));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.375, 0.1343749999999999, 0.423125, 1, 0.1343749999999999, 0.673125));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.326875, 0.1343749999999999, 0.014375000000000027, 0.576875, 0.1343749999999999, 0.639375));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.3375, 0.12375000000000003, 0.6000000000000001, 1.0375, 0.31125, 0.6625000000000001));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.3374999999999999, 0.12375000000000003, -0.03750000000000009, 0.3999999999999999, 0.31125, 0.6625000000000001));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.40000000000000013, 0.12375000000000003, 0.4125000000000001, 1.0374999999999999, 0.12375000000000003, 0.6000000000000001));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.3999999999999999, 0.12375000000000003, -0.03749999999999987, 0.5874999999999999, 0.12375000000000003, 0.41249999999999987));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.5625, 0, 0, 0.5625, 0.125, 0.4375));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.5625, 0, 0.4375, 1, 0.125, 0.4375));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.5625, 0, 0, 1, 0, 0.4375));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.96875, 0.3125, 0.5625, 1.03125, 1, 0.625));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.375, 0.3125, -0.03125, 0.4375, 1, 0.03125));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.859375, 0.735625, -0.03125, 1.015625, 0.798125, 0.03125));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.96875, 0.735625, -0.015625, 1.03125, 0.798125, 0.140625));

        return shape;
    }



}
