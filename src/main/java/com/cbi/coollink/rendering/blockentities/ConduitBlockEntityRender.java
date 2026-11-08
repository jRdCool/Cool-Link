package com.cbi.coollink.rendering.blockentities;

import com.cbi.coollink.blocks.blockentities.ConduitBlockEntity;
import com.cbi.coollink.rendering.WireNodeRenderer;
import com.cbi.coollink.rendering.blockentities.state.WireNodeRenderState;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.MovingBlockRenderState;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.NonNull;


public class ConduitBlockEntityRender extends WireNodeRenderer<ConduitBlockEntity> {
    public ConduitBlockEntityRender(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void submit(WireNodeRenderState state, @NonNull PoseStack poseStack, @NonNull SubmitNodeCollector submitNodeCollector, @NonNull CameraRenderState camera) {
        BlockPos pos = state.blockEntity.getBlockPos();
        ConduitBlockEntity conduitBlockEntity = (ConduitBlockEntity) state.blockEntity;
        if(conduitBlockEntity.isCovered()){
            BlockState blockState = conduitBlockEntity.getCoverBlock();
            ClientLevel level = (ClientLevel) conduitBlockEntity.getLevel();
            assert level != null;
            Holder<Biome> biome = level.getBiome(pos);

            MovingBlockRenderState movingBlockRenderState = new MovingBlockRenderState();
            movingBlockRenderState.randomSeedPos = pos;
            movingBlockRenderState.blockPos = pos;
            movingBlockRenderState.blockState = blockState;
            movingBlockRenderState.biome = biome;
            movingBlockRenderState.cardinalLighting = level.cardinalLighting();
            movingBlockRenderState.lightEngine = level.getLightEngine();


            submitNodeCollector.submitMovingBlock(poseStack, movingBlockRenderState, 0);
        }
        super.submit(state, poseStack, submitNodeCollector, camera);
    }

    /*
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
     */

    /*
    //for later when we want to be able to render wires from the conduits
    public void renderWires(ConduitBlockEntity tileEntityIn, float partialTicks, MatrixStack matrixStackIn, VertexConsumerProvider bufferIn, int combinedLightIn, int combinedOverlayIn, Vec3 cameraPos){
        super.render(tileEntityIn, partialTicks, matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn, cameraPos);
    }
     */
}
