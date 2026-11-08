package com.cbi.coollink.rendering;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.renderpearl.api.pipeline.*;
import com.mojang.renderpearl.api.vertex.VertexFormat;
import net.minecraft.client.renderer.BindGroupLayouts;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.rendertype.RenderSetup;

import static net.minecraft.client.renderer.RenderPipelines.GLOBALS_SNIPPET;


public class RenderType {

/*
    public RenderType(String name, int size, boolean hasCrumbling, boolean translucent, Runnable begin, Runnable end) {
        super(name, size,hasCrumbling,translucent,begin,end);
    }//String name, int size, boolean hasCrumbling, boolean translucent, Runnable begin, Runnable end
*/

    //modified from RenderPipelines.DEBUG_FILLED_SNIPPET
    public static final RenderPipeline.Snippet FILLED_SNIPPET = RenderPipeline.builder(GLOBALS_SNIPPET)
            .withBindGroupLayout(BindGroupLayouts.PROJECTION)
            .withBindGroupLayout(BindGroupLayouts.DYNAMIC_TRANSFORMS)
            .withVertexShader("core/position_color")
            .withFragmentShader("core/position_color")
            .withColorTargetState(new ColorTargetState(BlendFunction.TRANSLUCENT))
            .withVertexBinding(0, DefaultVertexFormat.POSITION_COLOR)
            .withPrimitiveTopology(PrimitiveTopology.QUADS)
            .withDepthStencilState(DepthStencilState.DEFAULT)
            .withCull(false)
            .buildSnippet();

    private static final RenderPipeline PIPELINE = RenderPipelines.register(RenderPipeline.builder(FILLED_SNIPPET)
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
            //create a custom pipeline to allow for a texture
            //then pass the texture here
            RenderSetup.builder(PIPELINE).createRenderSetup()
    );
}
