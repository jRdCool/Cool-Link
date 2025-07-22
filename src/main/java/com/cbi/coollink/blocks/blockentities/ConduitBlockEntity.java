package com.cbi.coollink.blocks.blockentities;

import com.cbi.coollink.Main;
import com.cbi.coollink.blocks.cables.createadditons.WireType;
import com.cbi.coollink.blocks.conduits.Conduit;
import com.cbi.coollink.rendering.IWireNode;
import com.cbi.coollink.rendering.LocalNode;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class ConduitBlockEntity extends BlockEntity implements IWireNode {

    public static ConduitBlockEntity of(BlockPos pos, BlockState state, int type){
        return switch (type){
            case 0 -> new ConduitBlockEntity(pos, state, Main.SMALL_CONDUIT_BLOCK_ENTITY);
            case 1 -> new ConduitBlockEntity(pos, state, Main.MEDIUM_CONDUIT_BLOCK_ENTITY);
            case 2 -> new ConduitBlockEntity(pos, state, Main.LARGE_CONDUIT_BLOCK_ENTITY);
            default -> null;
        };
    }
    public ConduitBlockEntity(BlockPos pos, BlockState state, BlockEntityType<?> type) {
                super(type, pos, state);
    }
    private BlockState coverBlock;

    @Override
    public void writeData(WriteView view){
        super.writeData(view);
        if(coverBlock != null){
            view.put("cover",BlockState.CODEC,coverBlock);
        }
    }


    @Override
    public void readData(ReadView view) {
        super.readData(view);
        coverBlock = view.read("cover",BlockState.CODEC).orElse(null);
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }


    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup) {
        return createNbt(registryLookup);
    }


    public void updateStates(){
        world.updateListeners(getPos(),getCachedState(),getCachedState(), Block.NOTIFY_LISTENERS);
    }

    public boolean isCovered(){
        return coverBlock != null;
    }

    public BlockState getCoverBlock(){
        return coverBlock;
    }

    public void setCoverBlock(BlockState newCover){
        coverBlock = newCover;

        if(world != null) {//stop the regular model from rendering if it is being hidden
            world.setBlockState(getPos(), world.getBlockState(getPos()).with(Conduit.HIDDEN, coverBlock != null));
        }
        markDirty();
        world.updateListeners(getPos(), getCachedState(), getCachedState(), 0);//CRITICAL FOR RENDER UPDATE, MAKE SURE FLAGS IS 0
    }




    //---------------------WireNode Functions---------------------//

    /*
    *Wire nodes for conduits are complex. They will be initially passes as binary and be indexed as such;
    * FFTTTTWW
    * F = Facing direction
    *   00 = N, 01 = S
    *   10 = W, 11 = E
    * T = Tube #
    *0b      0001  0011  0101  0111  1001  1101  1110
    *0b   0000  0010  0100  0110  1000  0101  0110  0111
    *        1 3 5 7 9 11
    *       0 2 4 6 8 10 12
    * W = Wire #
    *  00 01
    *  10 11
    *
    *
    * small conduits automatically use
     */


    @Override
    public Vec3d getNodeOffset(int node) {
        //int test = node ^ 0b11111111;//bit masking operation
        int filteredDir = node ^11000000;//clear out the node
        int dir = filteredDir >>> 6 ;//shift right 6 bits


        return null;
    }

    @Override
    public IWireNode getWireNode(int index) {return this;}

    @Override
    public int getOtherNodeIndex(int index) {
        return 0;
    }

    @Override
    public LocalNode getLocalNode(int index) {
        return null;
    }

    @Override
    public void setNode(int index, int otherNode, BlockPos pos, WireType type) {

    }

    @Override
    public void removeNode(int index, boolean dropWire) {

    }

    @Override
    public WireType getPortType(int index) {
        return null;
    }

    @Override
    public boolean isNodeInUse(int index) {
        return false;
    }

    @Override
    public void setIsNodeUsed(int index, boolean set) {

    }
}
