package com.cbi.coollink.net;

import com.cbi.coollink.Main;
import com.cbi.coollink.net.protocol.IpDataPacket;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record WIFIServerIpPacket(IpDataPacket data, BlockPos apPos) implements CustomPacketPayload {
    public static final Type<WIFIServerIpPacket> ID =new Type<>(Identifier.fromNamespaceAndPath(Main.namespace,"wifi-server-ip-packet"));
    public static final StreamCodec<RegistryFriendlyByteBuf,WIFIServerIpPacket> CODEC = StreamCodec.composite(
            IpDataPacket.PACKET_CODEC, WIFIServerIpPacket::data,
            BlockPos.STREAM_CODEC,WIFIServerIpPacket::apPos,
            WIFIServerIpPacket::new
    );
    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }


}
