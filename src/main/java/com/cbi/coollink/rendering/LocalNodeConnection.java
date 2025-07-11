package com.cbi.coollink.rendering;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record LocalNodeConnection(int index, int otherIndex, int type ,int x, int y, int z) {
    public static final Codec<LocalNodeConnection> CODEC = RecordCodecBuilder.create(builder -> {
        return builder.group(
                Codec.INT.fieldOf(LocalNode.ID).forGetter(LocalNodeConnection::index),
                Codec.INT.fieldOf(LocalNode.OTHER).forGetter(LocalNodeConnection::otherIndex),
                Codec.INT.fieldOf(LocalNode.TYPE).forGetter(LocalNodeConnection::type),
                Codec.INT.fieldOf(LocalNode.X).forGetter(LocalNodeConnection::x),
                Codec.INT.fieldOf(LocalNode.Y).forGetter(LocalNodeConnection::y),
                Codec.INT.fieldOf(LocalNode.Z).forGetter(LocalNodeConnection::z)
        ).apply(builder, LocalNodeConnection::new);
    });
}
