package com.cbi.coollink.net;

import com.cbi.coollink.Main;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;

public record ClientWifiConnectionResultPacket(boolean incorrectPassword,boolean networkFull, String deviceIp, String ssid, boolean connectedToTheInternet) implements CustomPacketPayload {
    public static final Type<ClientWifiConnectionResultPacket> ID = new Type<>(Identifier.fromNamespaceAndPath(Main.namespace,"client-wifi-connection-result"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ClientWifiConnectionResultPacket> CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL, ClientWifiConnectionResultPacket::incorrectPassword,
            ByteBufCodecs.BOOL, ClientWifiConnectionResultPacket::networkFull,
            ByteBufCodecs.STRING_UTF8, ClientWifiConnectionResultPacket::deviceIp,
            ByteBufCodecs.STRING_UTF8, ClientWifiConnectionResultPacket::ssid,
            ByteBufCodecs.BOOL, ClientWifiConnectionResultPacket::connectedToTheInternet,
            ClientWifiConnectionResultPacket::new
    );
    @Override
    public @NonNull Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}
