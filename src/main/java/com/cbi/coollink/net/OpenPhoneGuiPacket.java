package com.cbi.coollink.net;

import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public record OpenPhoneGuiPacket(RegistryKey<World> world, BlockPos block, ItemStack heldItem,boolean noBlockEntity) implements FabricPacket {
    public static final PacketType<OpenPhoneGuiPacket> TYPE = PacketType.create(new Identifier("cool-link", "open-phone-gui"), OpenPhoneGuiPacket::new);

    public OpenPhoneGuiPacket(PacketByteBuf buf) {
        this(buf.readRegistryKey(RegistryKeys.WORLD),buf.readBlockPos(),buf.readItemStack(),buf.readBoolean());
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeRegistryKey(this.world);
        buf.writeBlockPos(this.block);
        buf.writeItemStack(this.heldItem);
        buf.writeBoolean(this.noBlockEntity);
    }

    @Override
    public PacketType<?> getType() {
        return TYPE;
    }
}
