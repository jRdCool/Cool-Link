package com.cbi.coollink.net;

import com.cbi.coollink.Main;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.NonNull;

public record UpdateConduitBlockCover(BlockPos position, BlockState state, ResourceKey<Level> world) implements CustomPacketPayload {
    public static final Type<UpdateConduitBlockCover> ID =new Type<>(Identifier.fromNamespaceAndPath(Main.namespace,"update-conduit-cover"));
    public static final StreamCodec<RegistryFriendlyByteBuf, UpdateConduitBlockCover> CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, UpdateConduitBlockCover::position,
            ByteBufCodecs.fromCodecWithRegistries(BlockState.CODEC), UpdateConduitBlockCover::state,
            ByteBufCodecs.fromCodecWithRegistries(Level.RESOURCE_KEY_CODEC), UpdateConduitBlockCover::world,
            UpdateConduitBlockCover::new
    );
    @Override
    public @NonNull Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}
