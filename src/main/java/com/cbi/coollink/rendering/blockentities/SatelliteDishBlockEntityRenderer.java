package com.cbi.coollink.rendering.blockentities;

import com.cbi.coollink.blocks.blockentities.SatelliteDishBlockEntity;
import com.cbi.coollink.blocks.networkdevices.SatelliteDishBlock;
import com.cbi.coollink.rendering.WireNodeRenderer;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.Vec3d;

import static com.cbi.coollink.Main.ASSEMBLED_BOOLEAN_PROPERTY;
import static com.cbi.coollink.blocks.networkdevices.SatelliteDishBlock.multiBlockPose;

public class SatelliteDishBlockEntityRenderer extends WireNodeRenderer<SatelliteDishBlockEntity> {
    public SatelliteDishBlockEntityRenderer(BlockEntityRendererFactory.Context context) {
        super(context);
    }

    @Override
    public void render(SatelliteDishBlockEntity tileEntityIn, float partialTicks, MatrixStack matrixStackIn, VertexConsumerProvider bufferIn, int combinedLightIn, int combinedOverlayIn, Vec3d cameraPos) {
        BlockState state = tileEntityIn.getCachedState();
        if(state.get(ASSEMBLED_BOOLEAN_PROPERTY).equals(true) && state.get(multiBlockPose).equals(SatelliteDishBlock.MultiBlockPartStates.D1)) {
            super.render(tileEntityIn, partialTicks, matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn, cameraPos);
        }
    }
}
