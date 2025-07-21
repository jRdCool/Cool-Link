package com.cbi.coollink.blocks.blockentities.wallports;

import com.cbi.coollink.Main;
import com.cbi.coollink.blocks.blockentities.AWallPortBlockEntity;
import com.cbi.coollink.blocks.cables.createadditons.WireType;
import com.cbi.coollink.net.protocol.WireDataPacket;
import com.cbi.coollink.rendering.LocalNode;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class CoaxWallPortSingleBE extends AWallPortBlockEntity {

    /**used to get the constructor with entries for the various wood types
     */
    public static FabricBlockEntityTypeBuilder.Factory<CoaxWallPortSingleBE> of(String woodType){
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

    /**
     * Get the node of the destination device at the other end of the wire (not the next place the wire connects).
     * NOTE FOR IMPLEMENTATION: first: validate that the specified node has a connection/exsists then call IWireNode.traverseWire()
     *
     * @param connectionIndex The index of the start of the connection on this device
     * @return The {@link LocalNode} representing the other end of the wire, where index is the index of the node connection this wire terminates in on the receiving device
     */
    @Override
    public LocalNode getDestinationNode(int connectionIndex) {
        return null;
    }

    /**
     * Send a packet of data to this device.
     * NOTE FOR IMPLEMENTATION: this method is your class receiving this data from another class, this is called from another class.
     * Mid wire blocks(wall ports, conduits, ect..) should throw a warning upon calling this method.
     * All other blocks should first check that the data packet is of the correct type (coax, ethernet, fiber ect..) then process the packet accordingly
     *
     * @param connectionIndex The index of the connection node on the destination device that is reviving the data
     * @param data            The data to send to the other device
     */
    @Override
    public void transmitData(int connectionIndex, WireDataPacket data) {
        Main.LOGGER.error("Tried to send data to a mid cable block "+data+" "+getPos());
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
