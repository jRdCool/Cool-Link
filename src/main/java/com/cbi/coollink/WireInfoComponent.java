package com.cbi.coollink;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.math.BlockPos;

public record WireInfoComponent(int index, BlockPos originBlock){
    public static final Codec<WireInfoComponent> CODEC = RecordCodecBuilder.create(builder -> {
        return builder.group(
                Codec.INT.fieldOf("index").forGetter(WireInfoComponent::index),
                BlockPos.CODEC.fieldOf("origin_block").forGetter(WireInfoComponent::originBlock)
        ).apply(builder, WireInfoComponent::new);
    });

}
