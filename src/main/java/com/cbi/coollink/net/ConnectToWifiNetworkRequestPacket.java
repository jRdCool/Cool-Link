package com.cbi.coollink.net;

import com.cbi.coollink.Main;
import com.cbi.coollink.net.protocol.Mac;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public record ConnectToWifiNetworkRequestPacket(RegistryKey<World> world, BlockPos accessPointPosition, String password, Mac deviceMacAddress, String deviceName) implements CustomPayload {
    public static final Id<ConnectToWifiNetworkRequestPacket> ID = new Id<>(Identifier.of(Main.namespace,"client-connect-to-wifi-packet"));

    public static final PacketCodec<RegistryByteBuf, ConnectToWifiNetworkRequestPacket> CODEC = PacketCodec.tuple(
            PacketCodecs.registryCodec(World.CODEC), ConnectToWifiNetworkRequestPacket::world,
            BlockPos.PACKET_CODEC, ConnectToWifiNetworkRequestPacket::accessPointPosition,
            PacketCodecs.STRING, ConnectToWifiNetworkRequestPacket::password,
            Mac.PACKET_CODEC, ConnectToWifiNetworkRequestPacket::deviceMacAddress,
            PacketCodecs.STRING, ConnectToWifiNetworkRequestPacket::deviceName,
            ConnectToWifiNetworkRequestPacket::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
