package com.cbi.coollink.net;

import com.cbi.coollink.Main;
import com.cbi.coollink.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;

public record AccessPointLocationPacket(BlockPos[] aps, String ssid) implements CustomPacketPayload {
    public static final Type<AccessPointLocationPacket> ID =new Type<>(Identifier.fromNamespaceAndPath(Main.namespace,"access-point-location-packet"));
    public static final StreamCodec<RegistryFriendlyByteBuf, AccessPointLocationPacket> CODEC = StreamCodec.composite(
            Util.arrayPacketCodec(BlockPos.STREAM_CODEC, BlockPos[]::new), AccessPointLocationPacket::aps,
            ByteBufCodecs.STRING_UTF8, AccessPointLocationPacket::ssid,
            AccessPointLocationPacket::new
    );
    @Override
    public @NonNull Type<? extends CustomPacketPayload> type() {
        return ID;
    }

}
