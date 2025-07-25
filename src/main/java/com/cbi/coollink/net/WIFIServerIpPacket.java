package com.cbi.coollink.net;

import com.cbi.coollink.Main;
import com.cbi.coollink.net.protocol.IpDataPacket;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public record WIFIServerIpPacket(IpDataPacket data, BlockPos apPos) implements CustomPayload {
    public static final Id<WIFIServerIpPacket> ID =new Id<>(Identifier.of(Main.namespace,"wifi-server-ip-packet"));
    public static final PacketCodec<RegistryByteBuf,WIFIServerIpPacket> CODEC = PacketCodec.tuple(
            IpDataPacket.PACKET_CODEC, WIFIServerIpPacket::data,
            BlockPos.PACKET_CODEC,WIFIServerIpPacket::apPos,
            WIFIServerIpPacket::new
    );
    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
