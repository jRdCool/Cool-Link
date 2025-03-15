package com.cbi.coollink.net;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public record OpenConduitGuiPacket(NbtCompound conduitNBT, BlockPos pos) implements CustomPayload {

    public static final Id<OpenConduitGuiPacket> ID = new CustomPayload.Id<>(Identifier.of("cool-link", "open-conduit-gui"));
    public static final PacketCodec<RegistryByteBuf, OpenConduitGuiPacket> CODEC = PacketCodec.tuple(
            PacketCodecs.NBT_COMPOUND, OpenConduitGuiPacket::conduitNBT,
            BlockPos.PACKET_CODEC, OpenConduitGuiPacket::pos,
            OpenConduitGuiPacket::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}

