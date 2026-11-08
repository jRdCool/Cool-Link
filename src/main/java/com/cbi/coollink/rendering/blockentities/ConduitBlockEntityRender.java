package com.cbi.coollink.rendering.blockentities;

import com.cbi.coollink.blocks.blockentities.ConduitBlockEntity;
import com.cbi.coollink.rendering.WireNodeRenderer;
import com.mojang.authlib.minecraft.client.MinecraftClient;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class ConduitBlockEntityRender extends WireNodeRenderer<ConduitBlockEntity> {

    //BlockRenderManager
    private final BlockRenderManager blockRenderManager = MinecraftClient.getInstance().getBlockRenderManager();
    private final RandomSource random = new ChunkRandom(new LocalRandom(0));//used for textures that can have random rotations(dirt, grass, ect...) no idea how the game makes it no spaz out

    public ConduitBlockEntityRender(BlockEntityRendererFactory.Context context) {
        super(context);
    }


    @Override
    public void render(ConduitBlockEntity conduit, float partialTicks, MatrixStack matrixStackIn, VertexConsumerProvider bufferIn, int combinedLightIn, int combinedOverlayIn, Vec3 cameraPos) {
        if(conduit.isCovered()){
            BlockState coverState = conduit.getCoverBlock();
            //get the random just necessary to render as a block
            RenderLayer blockRenderingLayer = RenderLayers.getMovingBlockLayer(coverState);
            VertexConsumer vertexConsumer = bufferIn.getBuffer(blockRenderingLayer);
            random.setSeed((long) conduit.getBlockPos().getX() *3L*conduit.getBlockPos().getY()*17L+conduit.getBlockPos().getZ()* 23L);//make the texture rotation constant based on location
            List<BlockModelPart> parts = blockRenderManager.getModel(coverState).getParts(random);
            //render the cover block
            blockRenderManager.renderBlock(conduit.getCoverBlock(),conduit.getBlockPos(),conduit.getLevel(),matrixStackIn, vertexConsumer, true, parts);
        }
        super.render(conduit,partialTicks,matrixStackIn,bufferIn,combinedLightIn,combinedOverlayIn,cameraPos);
    }

    //for later when we want to be able to render wires from the conduits
    public void renderWires(ConduitBlockEntity tileEntityIn, float partialTicks, MatrixStack matrixStackIn, VertexConsumerProvider bufferIn, int combinedLightIn, int combinedOverlayIn, Vec3 cameraPos){
        super.render(tileEntityIn, partialTicks, matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn, cameraPos);
    }
}
