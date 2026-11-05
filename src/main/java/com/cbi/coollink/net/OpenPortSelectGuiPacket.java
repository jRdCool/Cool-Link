package com.cbi.coollink.net;

import com.cbi.coollink.Main;
import com.cbi.coollink.blocks.cables.createadditons.WireType;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;

public record OpenPortSelectGuiPacket(ArrayList<Integer> ofType, WireType wType, ResourceKey<Level> world, BlockPos pos, ItemStack heldItem) implements CustomPacketPayload {
    public static final Type<OpenPortSelectGuiPacket> ID = new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath(Main.namespace, "open-port-select-gui"));

    public static final StreamCodec<RegistryFriendlyByteBuf, OpenPortSelectGuiPacket> CODEC = StreamCodec.composite(
        ByteBufCodecs.collection(ArrayList::new,ByteBufCodecs.INT), OpenPortSelectGuiPacket::ofType,
        WireType.PACKET_CODEC, OpenPortSelectGuiPacket::wType,
        ByteBufCodecs.fromCodecWithRegistries(Level.RESOURCE_KEY_CODEC), OpenPortSelectGuiPacket::world,
        BlockPos.STREAM_CODEC, OpenPortSelectGuiPacket::pos,
        ItemStack.STREAM_CODEC, OpenPortSelectGuiPacket::heldItem,
        OpenPortSelectGuiPacket::new
    );
    @Override
    public @NonNull Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}
