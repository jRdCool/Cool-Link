package com.cbi.coollink.rendering;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderPhase;
import net.minecraft.client.render.VertexFormat;

public class RenderType extends RenderLayer {

    public RenderType(String name, VertexFormat vertexFormat, VertexFormat.DrawMode drawMode, int expectedBufferSize, boolean hasCrumbling, boolean translucent, Runnable startAction, Runnable endAction) {
        super(name, vertexFormat, drawMode, expectedBufferSize, hasCrumbling, translucent, startAction, endAction);
    }

    public static final RenderLayer WIRE = RenderLayer.of("wire", VertexFormat.builder().build(), VertexFormat.DrawMode.QUADS, 256, false, true, RenderLayer.of(RenderPhase.LEASH_PROGRAM));

}
