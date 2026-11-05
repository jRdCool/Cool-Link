package com.cbi.coollink.net;

import com.cbi.coollink.Main;
import com.cbi.coollink.blocks.cables.createadditons.WireType;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.NonNull;

public record WireInfoDataPacket(int index, BlockPos originBlock, ItemStack heldItem, WireType wireType, boolean clear) implements CustomPacketPayload {
    public static final Type<WireInfoDataPacket> ID = new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath(Main.namespace, "wire-info-data"));
    public static final StreamCodec<RegistryFriendlyByteBuf, WireInfoDataPacket> CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, WireInfoDataPacket::index,
            BlockPos.STREAM_CODEC, WireInfoDataPacket::originBlock,
            ItemStack.STREAM_CODEC, WireInfoDataPacket::heldItem,
            WireType.PACKET_CODEC, WireInfoDataPacket::wireType,
            ByteBufCodecs.BOOL, WireInfoDataPacket::clear,
            WireInfoDataPacket::new
    );
    @Override
    public @NonNull Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}
