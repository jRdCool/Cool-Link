package com.cbi.coollink.blocks.blockentities;

import com.cbi.coollink.Main;
import com.cbi.coollink.blocks.cables.createadditons.WireType;
import com.cbi.coollink.net.protocol.Mac;
import com.cbi.coollink.rendering.IWireNode;
import com.cbi.coollink.rendering.LocalNode;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class SwitchSimpleBE extends BlockEntity implements IWireNode {

    public SwitchSimpleBE(BlockPos pos, BlockState state) {
        super(Main.SWITCH_SIMPLE_BLOCK_ENTITY, pos, state);
        //String currentThread = Thread.currentThread().getName();
        this.localNodes = new LocalNode[getNodeCount()];
    }

    private static final int nodeCount = 5;
    private final boolean[] isNodeUsed = new boolean[nodeCount];
    private final LocalNode[] localNodes;


    @Override
    public Vec3d getNodeOffset(int node) {
        double node0XN = 0.275;
        double node1XN = 0.475;
        double node2XN = 0.75;
        double node3XN = 0.0;
        double node4XN = 0.0;
        double nodeZN = 0.9;
        double nodeY = .05;
        Vec3d[][] nodes = {
                {new Vec3d(node0XN,nodeY,nodeZN), new Vec3d(node1XN,nodeY,nodeZN), new Vec3d(node2XN,nodeY,nodeZN), new Vec3d(node3XN,nodeY,nodeZN), new Vec3d(node4XN,nodeY,nodeZN) },// - - - - - - - - - - - - - - NORTH
                {new Vec3d(1-nodeZN,nodeY,node0XN), new Vec3d(1-nodeZN,nodeY,node1XN), new Vec3d(1-nodeZN,nodeY,node2XN), new Vec3d(1-nodeZN,nodeY,node3XN), new Vec3d(1-nodeZN,nodeY,node4XN) },// - - - - - - - EAST
                {new Vec3d(1-node0XN,nodeY,1-nodeZN), new Vec3d(1-node1XN,nodeY,1-nodeZN), new Vec3d(1-node2XN,nodeY,1-nodeZN), new Vec3d(1-node3XN,nodeY,1-nodeZN), new Vec3d(1-node4XN,nodeY,1-nodeZN) },// SOUTH
                {new Vec3d(nodeZN,nodeY,1-node0XN), new Vec3d(nodeZN,nodeY,1-node1XN), new Vec3d(nodeZN,nodeY,1-node2XN), new Vec3d(nodeZN,nodeY,1-node3XN), new Vec3d(nodeZN,nodeY,1-node4XN) } // - - - - - - - WEST
        };
        int dir = 0;
        switch (getCachedState().get(Properties.HORIZONTAL_FACING)){
            case EAST -> dir=1;
            case SOUTH -> dir=2;
            case WEST -> dir=3;
            default -> {}
        }

        return nodes[dir][node];
    }

    @Override
    public IWireNode getWireNode(int index) {
        return this;
    }

    @Override
    public int getOtherNodeIndex(int index) {
        return localNodes[index].getOtherIndex();
    }

    @Override
    public LocalNode getLocalNode(int index) {
        return localNodes[index];
    }

    @Override
    public void setNode(int index, int otherNode, BlockPos pos, WireType type) {
        this.localNodes[index] = new LocalNode(this, index, otherNode, type, pos);
        isNodeUsed[index]=true;
        markDirty();
        assert world != null;
        world.updateListeners(getPos(), getCachedState(), getCachedState(), 0);
    }

    @Override
    public void removeNode(int index, boolean dropWire) {
        this.localNodes[index] = null;
        markDirty();
        assert world != null;
        world.updateListeners(getPos(), getCachedState(), getCachedState(), 0);
    }

    @Override
    public WireType getPortType(int index) {
        return WireType.CAT6;
    }

    @Override
    public boolean isNodeInUse(int index) {
        return isNodeUsed[index];
    }

    @Override
    public void setIsNodeUsed(int index, boolean set) {
        isNodeUsed[index]=set;
    }


}
