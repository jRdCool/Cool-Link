package com.cbi.coollink.net;

import com.cbi.coollink.Main;
import com.cbi.coollink.net.protocol.IpDataPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.NonNull;

public record WIFIClientIpPacket(ResourceKey<Level> world, BlockPos accessPointPosition, IpDataPacket payload) implements CustomPacketPayload {
    public static final Type<WIFIClientIpPacket> ID =new Type<>(Identifier.fromNamespaceAndPath(Main.namespace,"wifi-client-ip-packet"));
    public static final StreamCodec<RegistryFriendlyByteBuf, WIFIClientIpPacket> CODEC = StreamCodec.composite(
            ByteBufCodecs.fromCodecWithRegistries(Level.RESOURCE_KEY_CODEC), WIFIClientIpPacket::world,
            BlockPos.STREAM_CODEC, WIFIClientIpPacket::accessPointPosition,
            IpDataPacket.PACKET_CODEC, WIFIClientIpPacket::payload,
            WIFIClientIpPacket::new
    );
    @Override
    public @NonNull Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}
