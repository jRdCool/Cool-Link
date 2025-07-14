package com.cbi.coollink.rendering.blockentities;

import com.cbi.coollink.blocks.blockentities.ConduitBlockEntity;
import com.cbi.coollink.rendering.WireNodeRenderer;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.model.BlockModelPart;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.LocalRandom;
import net.minecraft.util.math.random.Random;

import java.util.ArrayList;
import java.util.List;

public class ConduitBlockEntityRender extends WireNodeRenderer<ConduitBlockEntity> {

    private final BlockRenderManager blockRenderManager = MinecraftClient.getInstance().getBlockRenderManager();
    private final Random random = new LocalRandom(0);

    public ConduitBlockEntityRender(BlockEntityRendererFactory.Context context) {
        super(context);
    }

    @Override
    public void render(ConduitBlockEntity conduit, float partialTicks, MatrixStack matrixStackIn, VertexConsumerProvider bufferIn, int combinedLightIn, int combinedOverlayIn, Vec3d cameraPos) {
        if(conduit.isCovered()){
            BlockState coverState = conduit.getCoverBlock();
            //get the random just necessary to render as a block
            RenderLayer blockRenderingLayer = RenderLayers.getMovingBlockLayer(coverState);
            VertexConsumer vertexConsumer = bufferIn.getBuffer(blockRenderingLayer);
            List<BlockModelPart> parts = blockRenderManager.getModel(coverState).getParts(random);
            //render the cover block
            blockRenderManager.renderBlock(conduit.getCoverBlock(),conduit.getPos(),conduit.getWorld(),matrixStackIn, vertexConsumer, true, parts);
        }
    }

    //for later when we want to be able to render wires from the conduits
    public void renderWires(ConduitBlockEntity tileEntityIn, float partialTicks, MatrixStack matrixStackIn, VertexConsumerProvider bufferIn, int combinedLightIn, int combinedOverlayIn, Vec3d cameraPos){
        super.render(tileEntityIn, partialTicks, matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn, cameraPos);
    }
}
