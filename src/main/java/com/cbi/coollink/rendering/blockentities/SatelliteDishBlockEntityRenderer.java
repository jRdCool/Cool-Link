package com.cbi.coollink.rendering.blockentities;

import com.cbi.coollink.blocks.blockentities.SatelliteDishBlockEntity;
import com.cbi.coollink.blocks.networkdevices.SatelliteDishBlock;
import com.cbi.coollink.rendering.WireNodeRenderer;
import com.cbi.coollink.rendering.blockentities.state.WireNodeRenderState;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.NonNull;

import static com.cbi.coollink.Main.ASSEMBLED_BOOLEAN_PROPERTY;
import static com.cbi.coollink.blocks.networkdevices.SatelliteDishBlock.multiBlockPose;

public class SatelliteDishBlockEntityRenderer extends WireNodeRenderer<SatelliteDishBlockEntity> {
    public SatelliteDishBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void submit(WireNodeRenderState state, @NonNull PoseStack poseStack, @NonNull SubmitNodeCollector submitNodeCollector, @NonNull CameraRenderState camera) {
        BlockState blockState = state.blockEntity.getBlockState();
        if(blockState.getValue(ASSEMBLED_BOOLEAN_PROPERTY).equals(true) && blockState.getValue(multiBlockPose).equals(SatelliteDishBlock.MultiBlockPartStates.D1)) {
            super.submit(state, poseStack, submitNodeCollector, camera);
        }
    }

}
