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

public record AioSetSSIDPacket(BlockPos pos, String newName, ResourceKey<Level> world) implements CustomPacketPayload {
    public static final Type<AioSetSSIDPacket> ID = new Type<>(Identifier.fromNamespaceAndPath(Main.namespace,"aio-set-ssid"));
    public static final StreamCodec<RegistryFriendlyByteBuf, AioSetSSIDPacket> CODEC = StreamCodec.composite(
        BlockPos.STREAM_CODEC, AioSetSSIDPacket::pos,
        ByteBufCodecs.STRING_UTF8, AioSetSSIDPacket::newName,
        ByteBufCodecs.fromCodecWithRegistries(Level.RESOURCE_KEY_CODEC), AioSetSSIDPacket::world,
        AioSetSSIDPacket::new
    );
    @Override
    public Type<? extends CustomPacketPayload> type() {return ID;}
}
