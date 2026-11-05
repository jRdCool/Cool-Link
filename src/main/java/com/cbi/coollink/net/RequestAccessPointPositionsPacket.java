package com.cbi.coollink.net;

import com.cbi.coollink.Main;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.NonNull;

public record RequestAccessPointPositionsPacket(ResourceKey<Level> world, BlockPos accessPointPosition) implements CustomPacketPayload {

    public static final Type<RequestAccessPointPositionsPacket> ID = new Type<>(Identifier.fromNamespaceAndPath(Main.namespace,"request-access-point-position-packet"));
    public static final StreamCodec<RegistryFriendlyByteBuf, RequestAccessPointPositionsPacket> CODEC = StreamCodec.composite(
            ByteBufCodecs.fromCodecWithRegistries(Level.RESOURCE_KEY_CODEC), RequestAccessPointPositionsPacket::world,
            BlockPos.STREAM_CODEC, RequestAccessPointPositionsPacket::accessPointPosition,
            RequestAccessPointPositionsPacket::new
    );

    @Override
    public @NonNull Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}
