package com.cbi.coollink.blocks.blockentities;

import com.cbi.coollink.Main;
import com.cbi.coollink.blocks.conduits.Conduit;
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
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class ConduitBlockEntity extends BlockEntity {

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

}
