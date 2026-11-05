package com.cbi.coollink.blocks.wallports;

import com.cbi.coollink.Main;
import com.cbi.coollink.blocks.cables.AIOCableBundle;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.*;

@SuppressWarnings("all")
@Deprecated
public class AIOWallPort extends Block {

    public static final Identifier ID = Identifier.fromNamespaceAndPath(Main.namespace,"aio_wall_port");
    public static final ResourceKey<Block> BLOCK_KEY = Main.createBlockRegistryKey(ID);
    public static final ResourceKey<Item> ITEM_KEY = Main.createItemRegistryKey(ID);
    public static final AIOWallPort ENTRY = new AIOWallPort(Properties.of().strength(0.5f)/*.registryKey(BLOCK_KEY)*/);

    public AIOWallPort(Properties settings){
        super(settings);
        //Main.LOGGER.info("AIOWallPort loaded");
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> stateManager) {
        super.createBlockStateDefinition(stateManager);
        stateManager.add(FACING);
    }

    @Override
    public @org.jspecify.annotations.Nullable BlockState getStateForPlacement(BlockPlaceContext ctx) {
        switch (ctx.getHorizontalDirection()){
            case NORTH -> {return this.defaultBlockState().setValue(FACING, Direction.SOUTH);}
            case EAST -> {return this.defaultBlockState().setValue(FACING, Direction.WEST);}
            case WEST -> {return this.defaultBlockState().setValue(FACING, Direction.EAST);}
            default -> {return this.defaultBlockState().setValue(FACING, Direction.NORTH);}
        }
    }

    @Override
    public void setPlacedBy(Level world, BlockPos pos, BlockState state, @org.jspecify.annotations.Nullable LivingEntity placer, ItemStack itemStack) {
        super.setPlacedBy(world, pos, state, placer, itemStack);
        editOtherBlock(world,pos,state,true);
    }


    @Override
    public void destroy(LevelAccessor world, BlockPos pos, BlockState state) {
        editOtherBlock((Level)world,pos,state,false);
    }

    private void editOtherBlock(Level world, BlockPos pos, BlockState state,Boolean create){
        switch (state.getValue(FACING)){
            case NORTH -> {
                //Main.LOGGER.info("North");
                if (world.getBlockState(pos.north()).getBlock() instanceof AIOCableBundle) {//check if the neighbor block is a coax cable
                    //Main.LOGGER.info(!((AIOCableBundle) world.getBlockState(pos.north()).getBlock()).isFacingMultiDirection(world,pos)+"");
                    if(((AIOCableBundle) world.getBlockState(pos.north()).getBlock()).isFacingSingleDirection(world,pos.north())||!create) {
                        //Main.LOGGER.info("Neighbor to north is coax");
                        world.setBlock(pos.north(), world.getBlockState(pos.north()).setValue(AIOCableBundle.south, create), UPDATE_ALL);//set the neighbor block to point to this block
                    }
                }
            }
            case SOUTH -> {
                //Main.LOGGER.info("South");
                if (world.getBlockState(pos.south()).getBlock() instanceof AIOCableBundle) {//check if the neighbor block is a coax cable
                    //Main.LOGGER.info(!((AIOCableBundle) world.getBlockState(pos.south()).getBlock()).isFacingMultiDirection(world,pos)+"");
                    if(((AIOCableBundle) world.getBlockState(pos.south()).getBlock()).isFacingSingleDirection(world,pos.south())||!create) {
                        //Main.LOGGER.info("Neighbor to north is coax");
                        world.setBlock(pos.south(), world.getBlockState(pos.south()).setValue(AIOCableBundle.north, create), UPDATE_ALL);//set the neighbor block to point to this block
                    }
                }
            }
            case WEST -> {
                //Main.LOGGER.info("West");
                if (world.getBlockState(pos.west()).getBlock() instanceof AIOCableBundle) {//check if the neighbor block is a coax cable
                    //Main.LOGGER.info(!((AIOCableBundle) world.getBlockState(pos.west()).getBlock()).isFacingMultiDirection(world,pos)+"");
                    if(((AIOCableBundle) world.getBlockState(pos.west()).getBlock()).isFacingSingleDirection(world,pos.west())||!create) {
                        //Main.LOGGER.info("Neighbor to north is coax");
                        world.setBlock(pos.west(), world.getBlockState(pos.west()).setValue(AIOCableBundle.east, create), UPDATE_ALL);//set the neighbor block to point to this block
                    }
                }
            }
            case EAST -> {
                //Main.LOGGER.info("east");
                if (world.getBlockState(pos.east()).getBlock() instanceof AIOCableBundle) {//check if the neighbor block is a coax cable
                    //Main.LOGGER.info(!((AIOCableBundle) world.getBlockState(pos.east()).getBlock()).isFacingMultiDirection(world,pos)+"");
                    if(((AIOCableBundle) world.getBlockState(pos.east()).getBlock()).isFacingSingleDirection(world,pos.east())||!create) {
                        //Main.LOGGER.info("Neighbor to north is coax");
                        world.setBlock(pos.east(), world.getBlockState(pos.east()).setValue(AIOCableBundle.west, create), UPDATE_ALL);//set the neighbor block to point to this block
                    }
                }
            }
        }
    }
}
