package com.cbi.coollink.net;


import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.NonNull;

public record OpenPhoneGuiPacket(ResourceKey<Level> world, ItemStack heldItem, Vec3 playerPos) implements CustomPacketPayload {
    //public static final PacketType<OpenPhoneGuiPacket> TYPE = PacketType.create(new Identifier("cool-link", "open-phone-gui"), OpenPhoneGuiPacket::new);
    public static final Type<OpenPhoneGuiPacket> ID = new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath("cool-link", "open-phone-gui"));
    public static final StreamCodec<RegistryFriendlyByteBuf, OpenPhoneGuiPacket> CODEC = StreamCodec.composite(
            ByteBufCodecs.fromCodecWithRegistries(Level.RESOURCE_KEY_CODEC), OpenPhoneGuiPacket::world,
            ItemStack.STREAM_CODEC, OpenPhoneGuiPacket::heldItem,
            ByteBufCodecs.fromCodecWithRegistries(Vec3.CODEC), OpenPhoneGuiPacket::playerPos,
            OpenPhoneGuiPacket::new
    );

    @Override
    public @NonNull Type<? extends CustomPacketPayload> type() {
        return ID;
    }

}
