package com.cbi.coollink.blocks.blockentities;

import com.cbi.coollink.Main;
import com.cbi.coollink.blocks.cables.createadditons.WireType;
import com.cbi.coollink.blocks.networkdevices.SatelliteDishBlock;
import com.cbi.coollink.net.protocol.CoaxDataPacket;
import com.cbi.coollink.net.protocol.WireDataPacket;
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
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Heightmap;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;

public class SatelliteDishBlockEntity extends BlockEntity implements IWireNode {
    public SatelliteDishBlockEntity(BlockPos pos, BlockState state) {
        super(Main.SATELLITE_DISH_BLOCK_ENTITY, pos, state);
    }

    LocalNode connection;

    @Override
    protected void readData(ReadView view) {
        super.readData(view);
        if(hasNode(getCachedState())){
            Optional<ReadView> connectionCompound = view.getOptionalReadView("connection");
            if(connectionCompound.isPresent() && connectionCompound.get().getOptionalInt(LocalNode.ID).isPresent()){
                connection = new LocalNode(this,connectionCompound.get());
            }
        }
    }

    @Override
    protected void writeData(WriteView view) {
        if(hasNode(getCachedState()) && connection != null){
            WriteView connectionCompound = view.get("connection");
            connection.write(connectionCompound);
        }
        super.writeData(view);
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup) {
        return createNbt(registryLookup);
    }

    public void updateStates(){
        if(world!=null) world.updateListeners(getPos(),getCachedState(),getCachedState(), Block.NOTIFY_LISTENERS);
    }

    @Override
    public Vec3d getNodeOffset(int node) {
        if(hasNode(getCachedState())){
            return new Vec3d(0.5,0.5,0.5);
        }
        return null;
    }

    /**
     * Get the {@link IWireNode} at the given index.
     *
     * @param index The index of the node.
     * @return The {@link IWireNode} at the given index, or null if the node
     * doesn't exist.
     */
    @Override
    public IWireNode getWireNode(int index) {
        if(hasNode(getCachedState())) {
            return this;
        }
        return (IWireNode) getActiveBlock();
    }

    @Override
    public int getOtherNodeIndex(int index) {
        if(hasNode(getCachedState())){
            return connection.getOtherIndex();
        }
        return 0;
    }

    /**
     * Get the {@link LocalNode} for the given index.
     *
     * @param index The index of the node.
     * @return The {@link LocalNode} for the given index, or null if the node
     * doesn't exist.
     */
    @Override
    public LocalNode getLocalNode(int index) {
        if(hasNode(getCachedState())){
            return connection;
        }
        return null;
    }

    @Override
    public void setNode(int index, int otherNode, BlockPos pos, WireType type) {
        if(hasNode(getCachedState())){
            connection = new LocalNode(this, index, otherNode, type, pos);
            markDirty();
            if(world != null)
                world.updateListeners(getPos(), getCachedState(), getCachedState(), 0);
        }
    }

    /**
     * Remove the given node.
     *
     * @param index    The index of the node to remove.
     * @param dropWire Whether to drop wires or not.
     */
    @Override
    public void removeNode(int index, boolean dropWire) {
        if(hasNode(getCachedState())){
            connection = null;
            markDirty();
            if(world != null)
                world.updateListeners(getPos(), getCachedState(), getCachedState(), 0);
        }
    }

    @Override
    public WireType getPortType(int index) {
        return WireType.COAX;
    }

    @Override
    public boolean isNodeInUse(int index) {
        return connection != null;
    }

    @Override
    public void setIsNodeUsed(int index, boolean set) {}

    /**
     * Get the node of the destination device at the other end of the wire (not the next place the wire connects)
     *
     * @param connectionIndex The index of the start of the connection on this device
     * @return The {@link LocalNode} representing the other end of the wire, where index is the index of the node connection this wire terminates in on the receiving device
     */
    @Override
    public LocalNode getDestinationNode(int connectionIndex) {
        if(connectionIndex != 0){
            return null;
        }
        if(connection == null){
            return null;
        }
        LocalNode outputNode = IWireNode.traverseWire(connection);
        if(outputNode == null || outputNode.getType() != WireType.COAX){
            Main.LOGGER.error("Null destination or incorrect output wire type (from SatelliteDishBlockEntity)");
            return null;
        }
        return outputNode;
    }

    /**
     * Send a packet of data to this device.
     * NOTE FOR IMPLEMENTATION: this method is your class receiving this data from another class, this is called from another class.
     * Mid wire blocks(wall ports, conduits, ect..) should throw a warning upon calling this method.
     * All other blocks should first check that the data packet is of the correct type (coax, ethernet, fiber ect..) then process the packet accordingly
     *
     * @param connectionIndex The index of the connection node on the destination device that is reviving the data
     * @param data            The data to send to the other device
     */
    @Override
    public void transmitData(int connectionIndex, WireDataPacket data) {
        if(data instanceof CoaxDataPacket coaxData){
            if(coaxData.isRequestOnline()){
                boolean online = false;
                //check assembled
                boolean assembled = Objects.requireNonNull(getWorld()).getBlockState(getPos()).get(Main.ASSEMBLED_BOOLEAN_PROPERTY);
                if(assembled){
                    //check if there is sky axis
                    BlockPos thisBlockPos = getPos();
                    BlockPos thisBlockPos2 = thisBlockPos.south();
                    BlockPos thisBlockPos3 = thisBlockPos.east();
                    BlockPos thisBlockPos4 = thisBlockPos2.east();
                    int topBlock = Objects.requireNonNull(getWorld()).getTopY(Heightmap.Type.WORLD_SURFACE,thisBlockPos.getX(),thisBlockPos.getZ());
                    int topBlock2 = Objects.requireNonNull(getWorld()).getTopY(Heightmap.Type.WORLD_SURFACE,thisBlockPos2.getX(),thisBlockPos2.getZ());
                    int topBlock3 = Objects.requireNonNull(getWorld()).getTopY(Heightmap.Type.WORLD_SURFACE,thisBlockPos3.getX(),thisBlockPos3.getZ());
                    int topBlock4 = Objects.requireNonNull(getWorld()).getTopY(Heightmap.Type.WORLD_SURFACE,thisBlockPos4.getX(),thisBlockPos4.getZ());
                    int aboveThisBlock = getPos().getY()+2;
                    if(topBlock == aboveThisBlock && topBlock2 == aboveThisBlock && topBlock3 == aboveThisBlock && topBlock4 == aboveThisBlock){
                        //sky axis
                        online = true;
                    }
                }
                LocalNode other = getDestinationNode(0);
                if(other != null){
                    if(other.getBlockEntity() instanceof IWireNode otherNode){
                        otherNode.transmitData(other.getIndex(),CoaxDataPacket.ofResponse(online));
                    }
                }
            }
        }
    }

    @Override
    public int getNodeCount() {
        BlockState state = getCachedState();
        if(hasNode(state)){
            return 1;
        }else {
            return 0;
        }
    }

    @Override
    public boolean hasConnection(int index) {
        return connection != null;
    }

    private boolean hasNode(BlockState state){
        return state.get(Main.ASSEMBLED_BOOLEAN_PROPERTY) && state.get(SatelliteDishBlock.multiBlockPose) == SatelliteDishBlock.MultiBlockPartStates.D1;
    }

    public BlockEntity getActiveBlock(){
        BlockState state = getCachedState();
        if(!state.get(Main.ASSEMBLED_BOOLEAN_PROPERTY)){
            return null;
        }
        if(world == null){
            return null;
        }
        switch(state.get(SatelliteDishBlock.multiBlockPose)){
            case D1 -> Main.LOGGER.info(pos.toString());
            case D2 -> Main.LOGGER.info(pos.add(-1,0,0)  .toString());
            case D3 -> Main.LOGGER.info(pos.add(-1,0,-1) .toString());
            case D4 -> Main.LOGGER.info(pos.add(0,0,-1)  .toString());
            case U1 -> Main.LOGGER.info(pos.add(0,-1,0)  .toString());
            case U2 -> Main.LOGGER.info(pos.add(-1,-1,0) .toString());
            case U3 -> Main.LOGGER.info(pos.add(-1,-1,-1).toString());
            case U4 -> Main.LOGGER.info(pos.add(0,-1,-1) .toString());

        }
        return switch(state.get(SatelliteDishBlock.multiBlockPose)){
            case D1 -> this;
            case D2 -> world.getBlockEntity(pos.add(-1,0,0));
            case D3 -> world.getBlockEntity(pos.add(-1,0,-1));
            case D4 -> world.getBlockEntity(pos.add(0,0,-1));
            case U1 -> world.getBlockEntity(pos.add(0,-1,0));
            case U2 -> world.getBlockEntity(pos.add(-1,-1,0));
            case U3 -> world.getBlockEntity(pos.add(-1,-1,-1));
            case U4 -> world.getBlockEntity(pos.add(0,-1,-1));
            case null, default -> null;
        };

    }
}
