package com.cbi.coollink.net;

import com.cbi.coollink.Main;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record ClientWifiConnectionResultPacket(boolean incorrectPassword,boolean networkFull, String deviceIp, String ssid, boolean connectedToTheInternet) implements CustomPayload {
    public static final Id<ClientWifiConnectionResultPacket> ID = new Id<>(Identifier.of(Main.namespace,"client-wifi-connection-result"));
    public static final PacketCodec<RegistryByteBuf, ClientWifiConnectionResultPacket> CODEC = PacketCodec.tuple(
            PacketCodecs.BOOLEAN, ClientWifiConnectionResultPacket::incorrectPassword,
            PacketCodecs.BOOLEAN, ClientWifiConnectionResultPacket::networkFull,
            PacketCodecs.STRING, ClientWifiConnectionResultPacket::deviceIp,
            PacketCodecs.STRING, ClientWifiConnectionResultPacket::ssid,
            PacketCodecs.BOOLEAN, ClientWifiConnectionResultPacket::connectedToTheInternet,
            ClientWifiConnectionResultPacket::new
    );
    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
