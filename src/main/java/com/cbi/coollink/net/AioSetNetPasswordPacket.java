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

public record AioSetNetPasswordPacket(BlockPos pos, String newPassword, ResourceKey<Level> world) implements CustomPacketPayload {
    public static final Type<AioSetNetPasswordPacket> ID = new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath(Main.namespace,"aio-set-net-password"));
    public static final StreamCodec<RegistryFriendlyByteBuf, AioSetNetPasswordPacket> CODEC = StreamCodec.composite(
        BlockPos.STREAM_CODEC, AioSetNetPasswordPacket::pos,
        ByteBufCodecs.STRING_UTF8, AioSetNetPasswordPacket::newPassword,
        ByteBufCodecs.fromCodecWithRegistries(Level.RESOURCE_KEY_CODEC), AioSetNetPasswordPacket::world,
        AioSetNetPasswordPacket::new
    );
    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}
