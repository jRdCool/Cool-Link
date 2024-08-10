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

public record AioSetNetPasswordPacket(BlockPos pos, String newPassword, RegistryKey<World> world) implements CustomPayload {
    public static final Id<AioSetNetPasswordPacket> ID = new CustomPayload.Id<>(Identifier.of(Main.namespace,"aio-set-net-password"));
    public static final PacketCodec<RegistryByteBuf, AioSetNetPasswordPacket> CODEC = PacketCodec.tuple(
        BlockPos.PACKET_CODEC, AioSetNetPasswordPacket::pos,
        PacketCodecs.STRING, AioSetNetPasswordPacket::newPassword,
        PacketCodecs.registryCodec(World.CODEC), AioSetNetPasswordPacket::world,
        AioSetNetPasswordPacket::new
    );
    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
