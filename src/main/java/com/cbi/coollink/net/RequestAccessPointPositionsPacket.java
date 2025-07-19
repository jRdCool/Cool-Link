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

public record RequestAccessPointPositionsPacket(RegistryKey<World> world, BlockPos accessPointPosition) implements CustomPayload {

    public static final Id<RequestAccessPointPositionsPacket> ID = new Id<>(Identifier.of(Main.namespace,"request-access-point-position-packet"));
    public static final PacketCodec<RegistryByteBuf, RequestAccessPointPositionsPacket> CODEC = PacketCodec.tuple(
            PacketCodecs.registryCodec(World.CODEC), RequestAccessPointPositionsPacket::world,
            BlockPos.PACKET_CODEC, RequestAccessPointPositionsPacket::accessPointPosition,
            RequestAccessPointPositionsPacket::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return null;
    }
}
