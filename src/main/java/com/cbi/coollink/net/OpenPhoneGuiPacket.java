package com.cbi.coollink.net;


import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public record OpenPhoneGuiPacket(RegistryKey<World> world, ItemStack heldItem, Vec3d playerPos) implements CustomPayload {
    //public static final PacketType<OpenPhoneGuiPacket> TYPE = PacketType.create(new Identifier("cool-link", "open-phone-gui"), OpenPhoneGuiPacket::new);
    public static final Id<OpenPhoneGuiPacket> ID = new CustomPayload.Id<>(Identifier.of("cool-link", "open-phone-gui"));
    public static final PacketCodec<RegistryByteBuf, OpenPhoneGuiPacket> CODEC = PacketCodec.tuple(
            PacketCodecs.registryCodec(World.CODEC), OpenPhoneGuiPacket::world,
            ItemStack.PACKET_CODEC, OpenPhoneGuiPacket::heldItem,
            PacketCodecs.codec(Vec3d.CODEC), OpenPhoneGuiPacket::playerPos,
            OpenPhoneGuiPacket::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {

        return ID;
    }

}
