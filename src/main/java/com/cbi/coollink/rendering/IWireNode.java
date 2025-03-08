package com.cbi.coollink.rendering;

import com.cbi.coollink.blocks.cables.createadditons.WireType;
import net.minecraft.util.CuboidBlockIterator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public interface IWireNode {


    int getNodeCount();

    boolean hasConnection(int i);

    Vec3d getNodeOffset(int i);

    IWireNode getWireNode(int i);

    int getOtherNodeIndex(int i);

    BlockPos getNodePos(int i);

    BlockPos getPos();

    WireType getNodeType(int i);
}
