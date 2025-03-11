package com.cbi.coollink.blocks.blockentities.wallports;

import com.cbi.coollink.Main;
import com.cbi.coollink.blocks.blockentities.AWallPortBlockEntity;
import com.cbi.coollink.blocks.cables.createadditons.WireType;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class CoaxWallPortSingleBE extends AWallPortBlockEntity {

    /**used to get the constructor with entries for the various wood types
     */
    public static BlockEntityType.BlockEntityFactory<CoaxWallPortSingleBE> of(String woodType){
        return ((pos1, state) -> new CoaxWallPortSingleBE(woodType,pos1,state));
    }
    public CoaxWallPortSingleBE(String woodType,BlockPos pos, BlockState state) {
        //resolve type here!!!!
        super(Main.coaxWallPortSingleBlockEntities.get(woodType), pos, state);
        nodeCount = 1;
    }




    @Override
    public WireType getPortType(int index) {
        return WireType.COAX;
    }


    @Override
    public Vec3d getNodeOffset(int node) {
        Vec3d[][] nodes = {
                {new Vec3d(0.5,0.5,0)},//NORTH
                {new Vec3d(1,0.5,0.5)},//EAST
                {new Vec3d(0.5,0.5,1)},//SOUTH
                {new Vec3d(0,0.5,0.5)},//WEST
                {new Vec3d(0.5,1,0.5)},//UP
                {new Vec3d(0.5,0,0.5)}//DOWN
        };
        return getNodeOffset(node,nodes);
    }


}
