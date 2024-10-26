package com.cbi.coollink.net;

import com.cbi.coollink.Main;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public record AioSyncMacPacket(BlockPos pos, byte[] mac1, byte[] mac2, RegistryKey<World> world) implements CustomPayload {
    public static final Id<AioSyncMacPacket> ID = new CustomPayload.Id<>(Identifier.of(Main.namespace, "aio-sync-mac"));
    public static final PacketCodec<RegistryByteBuf, AioSyncMacPacket> CODEC = PacketCodec.tuple(
        BlockPos.PACKET_CODEC, AioSyncMacPacket::pos,
        PacketCodecs.BYTE_ARRAY, AioSyncMacPacket::mac1,
        PacketCodecs.BYTE_ARRAY, AioSyncMacPacket::mac2,
        PacketCodecs.registryCodec(World.CODEC), AioSyncMacPacket::world,
        AioSyncMacPacket::new
    );
    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
