package com.cbi.coollink.blocks.blockentities;

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
import org.jetbrains.annotations.Nullable;

public class ConduitBlockEntity extends BlockEntity {
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

}
