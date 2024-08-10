package com.cbi.coollink.net;

import com.cbi.coollink.Main;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record SavePhoneDataPacket(NbtCompound nbt, ItemStack phone) implements CustomPayload {
    public static final Id<SavePhoneDataPacket> ID =new Id<>(Identifier.of(Main.namespace,"save-phone-data"));
    public static final PacketCodec<RegistryByteBuf, SavePhoneDataPacket> CODEC = PacketCodec.tuple(
            PacketCodecs.NBT_COMPOUND, SavePhoneDataPacket::nbt,
            ItemStack.PACKET_CODEC, SavePhoneDataPacket::phone,
            SavePhoneDataPacket::new
    );
    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
