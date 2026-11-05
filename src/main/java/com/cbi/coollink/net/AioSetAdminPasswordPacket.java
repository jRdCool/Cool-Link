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

public record AioSetAdminPasswordPacket(BlockPos pos, String newPassword, ResourceKey<Level> world) implements CustomPacketPayload {
    public static final Type<AioSetAdminPasswordPacket> ID = new Type<>(Identifier.fromNamespaceAndPath(Main.namespace,"aio-set-password"));
    public static final StreamCodec<RegistryFriendlyByteBuf, AioSetAdminPasswordPacket> CODEC = StreamCodec.composite(
        BlockPos.STREAM_CODEC, AioSetAdminPasswordPacket::pos,
        ByteBufCodecs.STRING_UTF8, AioSetAdminPasswordPacket::newPassword,
        ByteBufCodecs.fromCodecWithRegistries(Level.RESOURCE_KEY_CODEC), AioSetAdminPasswordPacket::world,
        AioSetAdminPasswordPacket::new
    );
    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}
