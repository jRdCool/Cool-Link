package com.cbi.coollink.net;

import com.cbi.coollink.Main;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

public record AioSyncMacPacket(BlockPos pos, byte[] mac1, byte[] mac2, ResourceKey<Level> world) implements CustomPacketPayload {
    public static final Type<AioSyncMacPacket> ID = new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath(Main.namespace, "aio-sync-mac"));
    public static final StreamCodec<RegistryFriendlyByteBuf, AioSyncMacPacket> CODEC = StreamCodec.composite(
        BlockPos.STREAM_CODEC, AioSyncMacPacket::pos,
        ByteBufCodecs.BYTE_ARRAY, AioSyncMacPacket::mac1,
        ByteBufCodecs.BYTE_ARRAY, AioSyncMacPacket::mac2,
        ByteBufCodecs.fromCodecWithRegistries(Level.RESOURCE_KEY_CODEC), AioSyncMacPacket::world,
        AioSyncMacPacket::new
    );
    @Override
    public Type<? extends CustomPacketPayload> type() {return ID;}
}
