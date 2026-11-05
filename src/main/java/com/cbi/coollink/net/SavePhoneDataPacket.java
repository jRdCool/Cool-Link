package com.cbi.coollink.net;

import com.cbi.coollink.Main;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.NonNull;

public record SavePhoneDataPacket(CompoundTag nbt, ItemStack phone) implements CustomPacketPayload {
    public static final Type<SavePhoneDataPacket> ID =new Type<>(Identifier.fromNamespaceAndPath(Main.namespace,"save-phone-data"));
    public static final StreamCodec<RegistryFriendlyByteBuf, SavePhoneDataPacket> CODEC = StreamCodec.composite(
            ByteBufCodecs.COMPOUND_TAG, SavePhoneDataPacket::nbt,
            ItemStack.STREAM_CODEC, SavePhoneDataPacket::phone,
            SavePhoneDataPacket::new
    );
    @Override
    public @NonNull Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}
