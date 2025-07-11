package com.cbi.coollink.blocks.blockentities;

import com.cbi.coollink.blocks.cables.createadditons.WireType;
import com.cbi.coollink.rendering.IWireNode;
import com.cbi.coollink.rendering.LocalNode;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

public abstract class AWallPortBlockEntity extends BlockEntity implements IWireNode {
    public AWallPortBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        this.localNodes = new LocalNode[getNodeCount()];
    }

    protected int nodeCount = 1;//Needs to be set by each wall port blockEntity

    private final boolean[] isNodeUsed = new boolean[nodeCount];

    private final LocalNode[] localNodes;

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        NbtList nodeIDS = new NbtList();
        for(int i=0;i<nodeCount;i++){
            NbtCompound compound = new NbtCompound();
            if(localNodes[i]==null){
                nodeIDS.add(compound);
                continue;
            }

            localNodes[i].write(compound);
            nodeIDS.add(compound);
        }
        nbt.put("connections",nodeIDS);

        super.writeNbt(nbt, registryLookup);
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {

        NbtList nodeIDS = nbt.getList("connections",NbtCompound.COMPOUND_TYPE);
        for (int i=0;i<nodeCount;i++){
            NbtCompound compound = nodeIDS.getCompound(i);
            if(compound==null || compound.isEmpty()){
                isNodeUsed[i] = false;
                continue;
            }
            localNodes[i]=new LocalNode(this , compound);
            isNodeUsed[i] = true;
        }

        super.readNbt(nbt, registryLookup);
    }

    @Override
    public Vec3d getNodeOffset(int node) {
        Vec3d[][] nodes = {
                {new Vec3d(0,0,0)},//NORTH
                {new Vec3d(0,0,0)},//EAST
                {new Vec3d(0,0,0)},//SOUTH
                {new Vec3d(0,0,0)},//WEST
                {new Vec3d(0,0,0)},//UP
                {new Vec3d(0,0,0)}//DOWN
        };
        return getNodeOffset(node,nodes);
    }

    public Vec3d getNodeOffset(int node,Vec3d[][] nodes) {
        int dir = 0;
        switch (getCachedState().get(Properties.FACING)){
            case EAST -> dir=1;
            case SOUTH -> dir=2;
            case WEST -> dir=3;
            case UP ->dir=4;
            case DOWN -> dir=5;
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
        return 0;
    }


    @Override
    public @Nullable LocalNode getLocalNode(int index) {
        return null;
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
        //LocalNode old = this.localNodes[index];
        this.localNodes[index] = null;
        //this.nodeCache[index] = null;
        markDirty();
        assert world != null;
        world.updateListeners(getPos(), getCachedState(), getCachedState(), 0);
    }

    @Override
    public boolean isNodeInUse(int index) {
        return isNodeUsed[index];
    }

    @Override
    public void setIsNodeUsed(int index, boolean set) {
        isNodeUsed[index]= set;
    }

    @Override
    public int getNodeCount() {
        return nodeCount;
    }
}
