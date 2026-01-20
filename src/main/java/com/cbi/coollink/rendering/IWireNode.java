package com.cbi.coollink.rendering;

import com.cbi.coollink.Main;
import com.cbi.coollink.blocks.blockentities.ConduitBlockEntity;
import com.cbi.coollink.blocks.cables.createadditons.WireType;
import com.cbi.coollink.net.protocol.WireDataPacket;
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

    /**
     *Used for rendering nodes takes the index of the node and
     * @param node
     *            The index of the requested node
     *
     * @return
     *          The local to the block XYZ position the requested node is
     *          connected to.
     */
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

    LocalNode getLocalNode(int index);

    /** Create a {@link  LocalNode} connection between to 2 specified ports
     * @param index the port on this wire node
     * @param otherNode the port on the remote wire node
     * @param pos the position of the remote node block
     * @param type the type pof the wire
     */
    void setNode(int index, int otherNode, BlockPos pos, WireType type);

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

    WireType getPortType(int index);

    boolean isNodeInUse(int index);
    void setIsNodeUsed(int index,boolean set);

    /**Get the node of the destination device at the other end of the wire (not the next place the wire connects).
     * NOTE FOR IMPLEMENTATION: first: validate that the specified node has a connection/exsists then call IWireNode.traverseWire()
     * @param connectionIndex The index of the start of the connection on this device
     * @return The {@link LocalNode} representing the other end of the wire, where index is the index of the node connection this wire terminates in on the receiving device
     */
    LocalNode getDestinationNode(int connectionIndex);

    /**Send a packet of data to this device.
     * NOTE FOR IMPLEMENTATION: this method is your class receiving this data from another class, this is called from another class.
     * Mid wire blocks(wall ports, conduits, ect..) should throw a warning upon calling this method.
     * All other blocks should first check that the data packet is of the correct type (coax, ethernet, fiber ect..) then process the packet accordingly
     * @param connectionIndex The index of the connection node on the destination device that is reviving the data
     * @param data The data to send to the other device
     */
    void transmitData(int connectionIndex, WireDataPacket data);

    /**Travel down a wire to the device at the other side
     * @param start The instance of the node on this send to start at
     * @return The local node on the ending side of the node
     */
    static LocalNode traverseWire(LocalNode start){
        WireType wireType = start.getType();
        LocalNode endLocation = null;
        World blockWorld = start.getBlockEntity().getWorld();
        if(blockWorld == null){
            return null;
        }
        BlockPos nextBlockPos = start.getTargetPos();
        int nextBlockConnectionIndex = start.getOtherIndex();
        for(int i=0;i< wireType.getTransmissionDistance();i++){//loop a maximum number of times for the distance a transition can go
            BlockEntity otherEntity = blockWorld.getBlockEntity(nextBlockPos);
            if(otherEntity instanceof IWireNode wireNode){
                if(wireNode instanceof ConduitBlockEntity conduit){
                    //mid-wire case
                    //special conduit/wall port transfer case
                    //get the position of the next node and the index the connection is on from the conduit
                    WireDescriptor connectionDescription = conduit.getConnectedNode(nextBlockConnectionIndex);
                    //if nothing was returned then there is no complete connection so return null
                    if(connectionDescription == null) {
//                        Main.LOGGER.info("COnd Null "+nextBlockPos+" "+nextBlockConnectionIndex);
                        return null;
                    }
                    nextBlockPos = connectionDescription.nextBlock();
                    nextBlockConnectionIndex = connectionDescription.portIndex();
                }else{

                    //end of wire case
                    endLocation = wireNode.getLocalNode(nextBlockConnectionIndex);
                }
            }else{
                break;
            }
        }
        return endLocation;
    }

    /**
     *
     * @param nextBlock Posiiotn of the next conduit/wire node
     * @param portIndex port index of the input of the next conduit/wire node
     */
    public record WireDescriptor(BlockPos nextBlock, int portIndex){
        public WireDescriptor(LocalNode node){
            this(node.getTargetPos(),node.getOtherIndex());
        }
    }

}
