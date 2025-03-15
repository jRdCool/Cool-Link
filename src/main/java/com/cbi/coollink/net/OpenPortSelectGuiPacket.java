package com.cbi.coollink.net;

import com.cbi.coollink.Main;
import com.cbi.coollink.blocks.cables.createadditons.WireType;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;

public record OpenPortSelectGuiPacket(ArrayList<Integer> ofType, WireType type, RegistryKey<World> world, BlockPos pos, ItemStack heldItem) implements CustomPayload {
    public static final Id<OpenPortSelectGuiPacket> ID = new CustomPayload.Id<>(Identifier.of(Main.namespace, "open-port-select-gui"));

    public static final PacketCodec<RegistryByteBuf, OpenPortSelectGuiPacket> CODEC = PacketCodec.tuple(
        PacketCodecs.collection(ArrayList::new,PacketCodecs.INTEGER), OpenPortSelectGuiPacket::ofType,
        WireType.PACKET_CODEC, OpenPortSelectGuiPacket::type,
        PacketCodecs.registryCodec(World.CODEC), OpenPortSelectGuiPacket::world,
        BlockPos.PACKET_CODEC, OpenPortSelectGuiPacket::pos,
        ItemStack.PACKET_CODEC, OpenPortSelectGuiPacket::heldItem,
        OpenPortSelectGuiPacket::new
    );
    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
