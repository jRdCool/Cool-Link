package com.cbi.coollink.net;

import com.cbi.coollink.Main;
import com.cbi.coollink.net.protocol.Mac;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

public record ConnectToWifiNetworkRequestPacket(ResourceKey<Level> world, BlockPos accessPointPosition, String password, Mac deviceMacAddress, String deviceName) implements CustomPacketPayload {
    public static final Type<ConnectToWifiNetworkRequestPacket> ID = new Type<>(Identifier.fromNamespaceAndPath(Main.namespace,"client-connect-to-wifi-packet"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ConnectToWifiNetworkRequestPacket> CODEC = StreamCodec.composite(
            ByteBufCodecs.fromCodecWithRegistries(Level.RESOURCE_KEY_CODEC), ConnectToWifiNetworkRequestPacket::world,
            BlockPos.STREAM_CODEC, ConnectToWifiNetworkRequestPacket::accessPointPosition,
            ByteBufCodecs.STRING_UTF8, ConnectToWifiNetworkRequestPacket::password,
            Mac.PACKET_CODEC, ConnectToWifiNetworkRequestPacket::deviceMacAddress,
            ByteBufCodecs.STRING_UTF8, ConnectToWifiNetworkRequestPacket::deviceName,
            ConnectToWifiNetworkRequestPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {return ID;}
}
