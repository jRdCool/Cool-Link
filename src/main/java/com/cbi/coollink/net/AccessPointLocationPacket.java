package com.cbi.coollink.net;

import com.cbi.coollink.Main;
import com.cbi.coollink.Util;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public record AccessPointLocationPacket(BlockPos[] aps, String ssid) implements CustomPayload {
    public static final Id<AccessPointLocationPacket> ID =new Id<>(Identifier.of(Main.namespace,"access-point-location-packet"));
    public static final PacketCodec<RegistryByteBuf, AccessPointLocationPacket> CODEC = PacketCodec.tuple(
            Util.arrayPacketCodec(BlockPos.PACKET_CODEC, BlockPos[]::new), AccessPointLocationPacket::aps,
            PacketCodecs.STRING, AccessPointLocationPacket::ssid,
            AccessPointLocationPacket::new
    );
    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
