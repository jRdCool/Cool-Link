package com.cbi.coollink.rendering;

import com.cbi.coollink.blocks.cables.createadditons.WireType;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface IWireNode {


    default int getNodeCount() {
        return 1;
    }

    boolean hasConnection(int i);

    Vec3d getNodeOffset(int node);

    IWireNode getWireNode(int i);

    int getOtherNodeIndex(int i);

    default BlockPos getNodePos(int index){
        LocalNode node = getLocalNode(index);
        if (node == null) return null;
        return node.getPos();
    }

    BlockPos getPos();

    default WireType getNodeType(int index){
        LocalNode node = getLocalNode(index);
        if (node == null) return null;
        return node.getType();
    }

    LocalNode getLocalNode(int index);

    void setNode(int index, int other, BlockPos pos, WireType type);
    void removeNode(int index, boolean dropWire);
    default void removeNode(int index) {
        removeNode(index, false);
    }
    default void removeNode(@NotNull LocalNode node) {
        removeNode(node.getIndex());
    }

    default int getAvailableNode() {
        for (int i = 0; i < getNodeCount(); i++) {
            if (hasConnection(i)) continue;
            return i;
        }
        return -1;
    }
    default int getAvailableNode(Vec3d pos) {
        // before: return 0;
        // Might be a good idea to not return 0 if the method isn't implemented.
        return getAvailableNode();
    }

    @Nullable
    static WireType getTypeOfConnection(World world, BlockPos pos1, BlockPos pos2) {
        BlockEntity te1 = world.getBlockEntity(pos1);
        if (te1 == null) return null;
        if (!(te1 instanceof IWireNode wn)) return null;
        LocalNode ln = wn.getConnectionTo(pos2);
        if (ln == null) return null;
        return ln.getType();
    }

    @Nullable
    default LocalNode getConnectionTo(BlockPos pos) {
        if (pos == null) return null;
        for (int i = 0; i < getNodeCount(); i++) {
            LocalNode node = getLocalNode(i);
            if (node == null) continue;
            if (node.getPos().equals(pos)) return node;
        }
        return null;
    }
}
