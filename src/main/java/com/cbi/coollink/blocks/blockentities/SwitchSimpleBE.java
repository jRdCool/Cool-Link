package com.cbi.coollink.blocks.blockentities;

import com.cbi.coollink.Main;
import com.cbi.coollink.blocks.cables.createadditons.WireType;
import com.cbi.coollink.rendering.IWireNode;
import com.cbi.coollink.rendering.LocalNode;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.state.property.Properties;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class SwitchSimpleBE extends BlockEntity implements IWireNode {

    public SwitchSimpleBE(BlockPos pos, BlockState state) {
        super(Main.SWITCH_SIMPLE_BLOCK_ENTITY, pos, state);
        //String currentThread = Thread.currentThread().getName();
        this.localNodes = new LocalNode[getNodeCount()];
    }//Constructor

    //variables
    private static final int nodeCount = 5;
    private final boolean[] isNodeUsed = new boolean[nodeCount];
    private final LocalNode[] localNodes;

    @Override
    protected void writeData(WriteView view) {
        super.writeData(view);
        WriteView.ListView connections = view.getList("connections");
        //WriteView.ListAppender<LocalNodeConnection> listAppender = view.getListAppender("connections", LocalNodeConnection.CODEC);
        for(int i=0;i<nodeCount;i++){
            if(localNodes[i]==null){
                connections.add();
            }else{
                WriteView connection = connections.add();
                localNodes[i].write(connection);
            }
        }

        //view.put("connections",,nodeIDS);
    }//Writing data

    @Override
    protected void readData(ReadView view) {
        super.readData(view);
        ReadView.ListReadView lrv = view.getListReadView("connections");
        List<ReadView> connectionNodes = lrv.stream().toList();

        for (int i=0;i<nodeCount;i++){
            if(i < connectionNodes.size()){
                ReadView cn = connectionNodes.get(i);

                if(cn.getOptionalInt(LocalNode.ID).isEmpty()){
                    isNodeUsed[i] = false;
                }else{
                    localNodes[i]=new LocalNode(this , cn);
                    isNodeUsed[i] = true;
                }
            }else{
                isNodeUsed[i] = false;
            }
        }
    }//reading data

    @Override
    public Vec3d getNodeOffset(int node) {
        double node0XN = 0.3;
        double node1XN = 0.4;
        double node2XN = 0.5;
        double node3XN = 0.6;
        double node4XN = 0.7;
        double nodeZN = 0.625;
        double nodeY = .04  ;
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
    }//offsets for the nodes

    @Override
    public IWireNode getWireNode(int index) {
        return this;
    }//returns this class

    @Override
    public int getOtherNodeIndex(int index) {
        return localNodes[index].getOtherIndex();
    }//getting the index of the other node

    @Override
    public LocalNode getLocalNode(int index) {
        return localNodes[index];
    }//getting the local nodes

    @Override
    public void setNode(int index, int otherNode, BlockPos pos, WireType type) {
        this.localNodes[index] = new LocalNode(this, index, otherNode, type, pos);
        isNodeUsed[index]=true;
        markDirty();
        assert world != null;
        world.updateListeners(getPos(), getCachedState(), getCachedState(), 0);
    }//setting the node status

    @Override
    public void removeNode(int index, boolean dropWire) {
        this.localNodes[index] = null;
        markDirty();
        assert world != null;
        world.updateListeners(getPos(), getCachedState(), getCachedState(), 0);
    }//Removing the wire

    @Override
    public WireType getPortType(int index) {
        return WireType.CAT6;
    }//The types of nodes declaration

    @Override
    public boolean isNodeInUse(int index) {
        return isNodeUsed[index];
    }//checks if the node is in use

    @Override
    public void setIsNodeUsed(int index, boolean set) {
        isNodeUsed[index]=set;
    }//setting node usage

    @Override
    public int getNodeCount() {
        return nodeCount;
    }//returns the number of nodes

    @Override
    public String toString() {
        return "SwitchSimpleBE{" +
                "isNodeUsed=" + Arrays.toString(isNodeUsed) +
                ", localNodes=" + Arrays.toString(localNodes) +
                '}';
    }

    public void updateStates(){
        if(world!=null) world.updateListeners(getPos(),getCachedState(),getCachedState(), Block.NOTIFY_LISTENERS);
    }//notifies the world of updates to the block state

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup) {
        return createNbt(registryLookup);
    }//Rendering crucial

}
