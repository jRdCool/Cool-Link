package com.cbi.coollink.items;

import com.cbi.coollink.Main;
import com.cbi.coollink.blocks.cables.CoaxCable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.world.World;

public class CoaxialCable extends Item {
    public CoaxialCable(Settings settings) {
        super(settings);
    }

    /*@Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand){
        return super.use(world, user, hand);
    }*/

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        BlockPos pos=context.getBlockPos();
        BlockPos placedPos;
        Direction dir=context.getSide();
        placedPos=pos.add(dir.getOffsetX(),dir.getOffsetY(),dir.getOffsetZ());
        World world=context.getWorld();
        world.setBlockState(placedPos, CoaxCable.ENTRY.getDefaultState());

        return super.useOnBlock(context);
    }
}
