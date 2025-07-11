package com.cbi.coollink.rendering;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.render.BuiltBuffer;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderPhase;
import net.minecraft.client.render.VertexFormats;

import static net.minecraft.client.gl.RenderPipelines.POSITION_COLOR_SNIPPET;
import static net.minecraft.client.gl.RenderPipelines.TRANSFORMS_AND_PROJECTION_SNIPPET;


public class RenderType extends RenderLayer {

    public RenderType(String name, int size, boolean hasCrumbling, boolean translucent, Runnable begin, Runnable end) {
        super(name, size,hasCrumbling,translucent,begin,end);
    }//String name, int size, boolean hasCrumbling, boolean translucent, Runnable begin, Runnable end


    private static final RenderPipeline PIPELINE = RenderPipeline.builder( new RenderPipeline.Snippet[]{POSITION_COLOR_SNIPPET})
            .withLocation("pipeline/debug_quads")
            .withCull(true)
            .withVertexFormat(VertexFormats.POSITION_COLOR_LIGHT,VertexFormat.DrawMode.QUADS)
            .build();

    public static final RenderLayer WIRE = RenderLayer.of(
            "wire",
            256,
            false,
            true,
            PIPELINE,
            MultiPhaseParameters.builder()
                    //other params (texture?) can be configured here
                    .build(false)
    );

    public static final RenderLayer SERVER_RACK = RenderLayer.of(
            "serverrack",
            256,
            false,
            true,
            PIPELINE,
            MultiPhaseParameters.builder()
                    //other params (texture?) can be configured here
                    .build(false)
    );
    @Override
    public void draw(BuiltBuffer buffer) {

    }

    @Override
    public VertexFormat getVertexFormat() {
        return null;
    }

    @Override
    public VertexFormat.DrawMode getDrawMode() {
        return null;
    }
}
