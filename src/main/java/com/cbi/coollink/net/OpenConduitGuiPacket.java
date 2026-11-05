package com.cbi.coollink.net;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record OpenConduitGuiPacket(CompoundTag conduitNBT, BlockPos pos) implements CustomPacketPayload {

    public static final Type<OpenConduitGuiPacket> ID = new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath("cool-link", "open-conduit-gui"));
    public static final StreamCodec<RegistryFriendlyByteBuf, OpenConduitGuiPacket> CODEC = StreamCodec.composite(
            ByteBufCodecs.COMPOUND_TAG, OpenConduitGuiPacket::conduitNBT,
            BlockPos.STREAM_CODEC, OpenConduitGuiPacket::pos,
            OpenConduitGuiPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return null;
    }
}

