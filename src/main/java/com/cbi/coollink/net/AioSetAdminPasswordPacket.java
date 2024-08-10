package com.cbi.coollink.net;

import com.cbi.coollink.Main;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public record AioSetAdminPasswordPacket(BlockPos pos, String newPassword, RegistryKey<World> world) implements CustomPayload {
    public static final Id<AioSetAdminPasswordPacket> ID = new Id<>(Identifier.of(Main.namespace,"aio-set-password"));
    public static final PacketCodec<RegistryByteBuf, AioSetAdminPasswordPacket> CODEC = PacketCodec.tuple(
        BlockPos.PACKET_CODEC, AioSetAdminPasswordPacket::pos,
        PacketCodecs.STRING, AioSetAdminPasswordPacket::newPassword,
        PacketCodecs.registryCodec(World.CODEC), AioSetAdminPasswordPacket::world,
        AioSetAdminPasswordPacket::new
    );
    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
