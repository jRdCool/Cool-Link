package com.cbi.coollink.net;

import com.cbi.coollink.Main;
import com.cbi.coollink.blocks.cables.createadditons.WireType;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public record WireInfoDataPacket(int index, BlockPos originBlock, ItemStack heldItem, WireType wireType, boolean clear) implements CustomPayload {
    public static final Id<WireInfoDataPacket> ID = new CustomPayload.Id<>(Identifier.of(Main.namespace, "wire-info-data"));
    public static final PacketCodec<RegistryByteBuf, WireInfoDataPacket> CODEC = PacketCodec.tuple(
            PacketCodecs.INTEGER, WireInfoDataPacket::index,
            BlockPos.PACKET_CODEC, WireInfoDataPacket::originBlock,
            ItemStack.PACKET_CODEC, WireInfoDataPacket::heldItem,
            WireType.PACKET_CODEC, WireInfoDataPacket::wireType,
            PacketCodecs.BOOL, WireInfoDataPacket::clear,
            WireInfoDataPacket::new
    );
    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
