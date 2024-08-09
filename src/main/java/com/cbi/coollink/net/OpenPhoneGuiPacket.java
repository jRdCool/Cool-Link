package com.cbi.coollink.net;


import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public record OpenPhoneGuiPacket(RegistryKey<World> world, BlockPos block, ItemStack heldItem,boolean noBlockEntity) implements CustomPayload {
    //public static final PacketType<OpenPhoneGuiPacket> TYPE = PacketType.create(new Identifier("cool-link", "open-phone-gui"), OpenPhoneGuiPacket::new);
    public static final Id<OpenPhoneGuiPacket> ID = new CustomPayload.Id<>(Identifier.of("cool-link", "open-phone-gui"));
    public static final PacketCodec<RegistryByteBuf, OpenPhoneGuiPacket> CODEC = PacketCodec.tuple(
            PacketCodecs.registryCodec(World.CODEC), OpenPhoneGuiPacket::world,
            BlockPos.PACKET_CODEC, OpenPhoneGuiPacket::block,
            ItemStack.PACKET_CODEC, OpenPhoneGuiPacket::heldItem,
            PacketCodecs.BOOL, OpenPhoneGuiPacket::noBlockEntity,
            OpenPhoneGuiPacket::new
    );

    //public OpenPhoneGuiPacket(PacketByteBuf buf) {
    //    this(buf.readRegistryKey(RegistryKeys.WORLD),buf.readBlockPos(),/*buf.readItemStack()*/null,buf.readBoolean());
    //}
//
    ////@Override
    //public void write(PacketByteBuf buf) {
    //    buf.writeRegistryKey(this.world);
    //    buf.writeBlockPos(this.block);
    //    //buf.writeItemStack(this.heldItem);
    //    buf.writeBoolean(this.noBlockEntity);
    //}

    @Override
    public Id<? extends CustomPayload> getId() {

        return ID;
    }

}
