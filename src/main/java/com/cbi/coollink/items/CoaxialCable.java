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
        switch (dir){
            case NORTH -> {placedPos=pos.add(0,0,-1);}
            case SOUTH -> {placedPos=pos.add(0,0,1);}
            case EAST -> {placedPos=pos.add(1,0,0);}
            case WEST -> {placedPos=pos.add(-1,0,0);}
            case UP -> {placedPos=pos.add(0,1,0);}
            case DOWN -> {placedPos=pos.add(0,-1,0);}
            default -> {
                placedPos=pos;
                Main.LOGGER.error("Failed to get placed direction");
            }
        }
        World world=context.getWorld();
        world.setBlockState(placedPos, CoaxCable.ENTRY.getDefaultState());

        return super.useOnBlock(context);
    }
}
