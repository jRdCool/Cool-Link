package com.cbi.coollink.blocks.blockentities;

import com.cbi.coollink.blocks.cables.createadditons.WireType;
import com.cbi.coollink.rendering.IWireNode;
import com.cbi.coollink.rendering.LocalNode;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

public abstract class AWallPortBlockEntity extends BlockEntity implements IWireNode {
    public AWallPortBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
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
    public BlockPos getPos() {
        return null;
    }

    @Override
    public @Nullable LocalNode getLocalNode(int index) {
        return null;
    }

    @Override
    public void setNode(int index, int otherNode, BlockPos pos, WireType type) {

    }

    @Override
    public void removeNode(int index, boolean dropWire) {

    }
}
