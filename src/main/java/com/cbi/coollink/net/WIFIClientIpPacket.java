package com.cbi.coollink.net;

import com.cbi.coollink.Main;
import com.cbi.coollink.net.protocol.IpDataPacket;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public record WIFIClientIpPacket(RegistryKey<World> world, BlockPos accessPointPosition, IpDataPacket payload) implements CustomPayload {
    public static final Id<WIFIClientIpPacket> ID =new Id<>(Identifier.of(Main.namespace,"wifi-client-ip-packet"));
    public static final PacketCodec<RegistryByteBuf, WIFIClientIpPacket> CODEC = PacketCodec.tuple(
            PacketCodecs.registryCodec(World.CODEC), WIFIClientIpPacket::world,
            BlockPos.PACKET_CODEC, WIFIClientIpPacket::accessPointPosition,
            IpDataPacket.PACKET_CODEC, WIFIClientIpPacket::payload,
            WIFIClientIpPacket::new
    );
    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
