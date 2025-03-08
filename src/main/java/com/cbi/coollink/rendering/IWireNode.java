package com.cbi.coollink.rendering;

import com.cbi.coollink.blocks.cables.createadditons.WireType;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface IWireNode {


    /**
     * Get the number of nodes this {@link IWireNode} supports.
     *
     * @return  The number of supported nodes.
     */
    default int getNodeCount() {
        return 1;
    }

    /**
     * Check if this {@link IWireNode} has a node at the given index.
     *
     * @param   index
     *          The index of the node to check.
     *
     * @return  True if the node exists, false otherwise.
     */
    default boolean hasConnection(int index) {
        return getLocalNode(index) != null;
    };

    /**
     * Check if this {@link IWireNode} has a node at the given position.
     *
     * @param   pos
     *          The position to check.
     *
     * @return  True if a node at the given position exists, false otherwise.
     */
    default boolean hasConnectionTo(BlockPos pos) {
        if (pos == null) return false;
        for (int i = 0; i < getNodeCount(); i++) {
            LocalNode node = getLocalNode(i);
            if (node == null) continue;
            if (node.getTargetPos().equals(pos)) return true;
        }
        return false;
    }

    Vec3d getNodeOffset(int node);

    /**
     * Get the {@link IWireNode} at the given index.
     *
     * @param   index
     *          The index of the node.
     *
     * @return  The {@link IWireNode} at the given index, or null if the node
     *          doesn't exist.
     */
    IWireNode getWireNode(int index);

    static IWireNode getWireNode(World world, BlockPos pos) {
        if(pos == null)
            return null;
        BlockEntity te = world.getBlockEntity(pos);
        if(te == null)
            return null;
        if(!(te instanceof IWireNode))
            return null;
        return (IWireNode) te;
    }

    int getOtherNodeIndex(int index);

    default BlockPos getNodePos(int index){
        LocalNode node = getLocalNode(index);
        if (node == null) return null;
        return node.getTargetPos();
    }

    BlockPos getPos();

    default WireType getNodeType(int index){
        LocalNode node = getLocalNode(index);
        if (node == null) return null;
        return node.getType();
    }

    /**
     * Get the {@link LocalNode} for the given index.
     *
     * @param   index
     *          The index of the node.
     *
     * @return  The {@link LocalNode} for the given index, or null if the node
     *          doesn't exist.
     */
    @Nullable
    LocalNode getLocalNode(int index);

    void setNode(int index, int other, BlockPos pos, WireType type);

    /**
     * Remove the given node.
     *
     * @param   index
     *          The index of the node to remove.
     * @param   dropWire
     *          Whether to drop wires or not.
     */
    void removeNode(int index, boolean dropWire);

    /**
     * Remove the given node.
     *
     * @param   index
     *          The index of the node to remove.
     */
    default void removeNode(int index) {
        removeNode(index, false);
    }

    /**
     * Remove the given node.
     *
     * @param   node
     *          The node to remove.
     * @param   dropWire
     *          Whether to drop wires or not.
     */
    default void removeNode(LocalNode node, boolean dropWire) {
        removeNode(node.getIndex(), dropWire);
    }

    /**
     * Remove the given node.
     *
     * @param   node
     *          The node to remove.
     */
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
            if (node.getTargetPos().equals(pos)) return node;
        }
        return null;
    }

    /**
     * Used by {@link IWireNode#getWireNode(int)} to get a cached
     * {@link IWireNode}.
     */
    @Nullable
    static IWireNode getWireNodeFrom(int index, IWireNode obj, LocalNode[] localNodes, IWireNode[] nodeCache,
                                     World level) {
        if (!obj.hasConnection(index)) return null;
        // Cache the node if it isn't already.
        if (nodeCache[index] == null)
            nodeCache[index] = IWireNode.getWireNode(level, localNodes[index].getTargetPos());
        // If the node is still null, remove it.
        if (nodeCache[index] == null) obj.removeNode(index);
        return nodeCache[index];
    }
}
