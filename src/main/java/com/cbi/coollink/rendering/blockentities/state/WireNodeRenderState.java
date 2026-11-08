package com.cbi.coollink.rendering.blockentities.state;

import com.cbi.coollink.rendering.IWireNode;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;

public class WireNodeRenderState extends BlockEntityRenderState {

    public Vec3 startPos= Vec3.ZERO;
    public Vec3 endPos= Vec3.ZERO;
    public int nodeCount = 0;
    public BlockEntity blockEntity;
}
