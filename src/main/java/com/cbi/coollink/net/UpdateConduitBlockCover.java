package com.cbi.coollink.net;

import com.cbi.coollink.Main;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public record UpdateConduitBlockCover(BlockPos position, BlockState state, RegistryKey<World> world) implements CustomPayload {
    public static final Id<UpdateConduitBlockCover> ID =new Id<>(Identifier.of(Main.namespace,"update-conduit-cover"));
    public static final PacketCodec<RegistryByteBuf, UpdateConduitBlockCover> CODEC = PacketCodec.tuple(
            BlockPos.PACKET_CODEC, UpdateConduitBlockCover::position,
            PacketCodecs.codec(BlockState.CODEC), UpdateConduitBlockCover::state,
            PacketCodecs.registryCodec(World.CODEC), UpdateConduitBlockCover::world,
            UpdateConduitBlockCover::new
    );
    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
