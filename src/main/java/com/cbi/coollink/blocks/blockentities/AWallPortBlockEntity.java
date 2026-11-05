package com.cbi.coollink.blocks.blockentities;

import com.cbi.coollink.blocks.cables.createadditons.WireType;
import com.cbi.coollink.rendering.IWireNode;
import com.cbi.coollink.rendering.LocalNode;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import java.util.List;

public abstract class AWallPortBlockEntity extends BlockEntity implements IWireNode {
    public AWallPortBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        this.localNodes = new LocalNode[getNodeCount()];
    }

    protected int nodeCount = 1;//Needs to be set by each wall port blockEntity

    private final boolean[] isNodeUsed = new boolean[nodeCount];

    private final LocalNode[] localNodes;

    @Override
    protected void saveAdditional(ValueOutput view) {
        ValueOutput.ValueOutputList nodeIDS = view.childrenList("connections");
        for(int i=0;i<nodeCount;i++){
            ValueOutput compound = nodeIDS.addChild();
            if(localNodes[i]==null){
                continue;
            }

            localNodes[i].write(compound);
        }


        super.saveAdditional(view);
    }

    @Override
    protected void loadAdditional(@NonNull ValueInput view) {

        List<ValueInput> nodeIDS = view.childrenListOrEmpty("connections").stream().toList();

        for (int i=0;i<nodeCount;i++){
            ValueInput compound = nodeIDS.get(i);
            if(compound == null || compound.childrenListOrEmpty(LocalNode.ID).isEmpty()){
                isNodeUsed[i] = false;
                continue;
            }
            localNodes[i]=new LocalNode(this , compound);
            isNodeUsed[i] = true;
        }

        super.loadAdditional(view);
    }

    @Override
    public Vec3 getNodeOffset(int node) {
        Vec3[][] nodes = {
                {new Vec3(0,0,0)},//NORTH
                {new Vec3(0,0,0)},//EAST
                {new Vec3(0,0,0)},//SOUTH
                {new Vec3(0,0,0)},//WEST
                {new Vec3(0,0,0)},//UP
                {new Vec3(0,0,0)}//DOWN
        };
        return getNodeOffset(node,nodes);
    }

    public Vec3 getNodeOffset(int node,Vec3[][] nodes) {
        int dir = 0;
        switch (getBlockState().getValue(BlockStateProperties.FACING)){
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
        setChanged();
        assert level != null;
        level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 0);
    }

    @Override
    public void removeNode(int index, boolean dropWire) {
        //LocalNode old = this.localNodes[index];
        this.localNodes[index] = null;
        //this.nodeCache[index] = null;
        setChanged();
        assert level != null;
        level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 0);
        this.setIsNodeUsed(index,false);
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
