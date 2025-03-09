package com.cbi.coollink.blocks.blockentities;

import com.cbi.coollink.blocks.cables.createadditons.WireType;
import com.cbi.coollink.rendering.IWireNode;
import com.cbi.coollink.rendering.LocalNode;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

public abstract class AWallPortBlockEntity extends BlockEntity implements IWireNode {
    public AWallPortBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public Vec3d getNodeOffset(int node) {
        return null;
    }

    @Override
    public IWireNode getWireNode(int index) {
        return null;
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
