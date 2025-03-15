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

public record AioSetSSIDPacket(BlockPos pos, String newName, RegistryKey<World> world) implements CustomPayload {
    public static final Id<AioSetSSIDPacket> ID = new Id<>(Identifier.of(Main.namespace,"aio-set-ssid"));
    public static final PacketCodec<RegistryByteBuf, AioSetSSIDPacket> CODEC = PacketCodec.tuple(
        BlockPos.PACKET_CODEC, AioSetSSIDPacket::pos,
        PacketCodecs.STRING, AioSetSSIDPacket::newName,
        PacketCodecs.registryCodec(World.CODEC), AioSetSSIDPacket::world,
        AioSetSSIDPacket::new
    );
    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
