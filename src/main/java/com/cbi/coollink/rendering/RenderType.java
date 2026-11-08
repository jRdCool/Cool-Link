package com.cbi.coollink.rendering;

import com.mojang.renderpearl.api.pipeline.RenderPipeline;
import com.mojang.renderpearl.api.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.rendertype.RenderSetup;


public class RenderType {

/*
    public RenderType(String name, int size, boolean hasCrumbling, boolean translucent, Runnable begin, Runnable end) {
        super(name, size,hasCrumbling,translucent,begin,end);
    }//String name, int size, boolean hasCrumbling, boolean translucent, Runnable begin, Runnable end
*/

    private static final RenderPipeline PIPELINE = RenderPipelines.register(RenderPipeline.builder(RenderPipelines.DEBUG_FILLED_SNIPPET)
            .withLocation("pipeline/debug_quads")
            .withCull(true)
            //.withVertexFormat(VertexFormats.POSITION_COLOR_LIGHT, VertexFormat.DrawMode.QUADS)
            .build()
    );

    public static final net.minecraft.client.renderer.rendertype.RenderType WIRE = net.minecraft.client.renderer.rendertype.RenderType.create(
            "wire",
            //256,
            //false,
            //true,
            //PIPELINE,
            //MultiPhaseParameters.builder()
                    //other params (texture?) can be configured here
            //        .build(false)
            RenderSetup.builder(PIPELINE).createRenderSetup()
    );

    public static final net.minecraft.client.renderer.rendertype.RenderType SERVER_RACK = net.minecraft.client.renderer.rendertype.RenderType.create(
            "serverrack",
            //256,
            //false,
            //true,
            //PIPELINE,
            //MultiPhaseParameters.builder()
                    //other params (texture?) can be configured here
            //        .build(false)
            RenderSetup.builder(PIPELINE).createRenderSetup()
    );
}
