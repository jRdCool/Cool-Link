package com.cbi.coollink.blocks.blockentities;

import com.cbi.coollink.Main;
import com.cbi.coollink.blocks.cables.createadditons.WireType;
import com.cbi.coollink.blocks.networkdevices.SatelliteDishBlock;
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
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

public class SatelliteDishBlockEntity extends BlockEntity implements IWireNode {
    public SatelliteDishBlockEntity(BlockPos pos, BlockState state) {
        super(Main.SATELLITE_DISH_BLOCK_ENTITY, pos, state);
    }

    LocalNode connection;

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        if(hasNode(getCachedState())){
            NbtCompound connectionCompound = nbt.getCompound("connection");
            if(connectionCompound != null && !connectionCompound.isEmpty()){
                connection = new LocalNode(this,connectionCompound);
            }
        }
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        NbtCompound connectionCompound = new NbtCompound();
        if(hasNode(getCachedState()) && connection != null){
            connection.write(connectionCompound);
            nbt.put("connection",connectionCompound);
        }
        super.writeNbt(nbt, registryLookup);
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
