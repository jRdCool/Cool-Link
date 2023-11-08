package com.cbi.coollink.items;

import com.cbi.coollink.blocks.cables.CoaxCable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
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
        if(world.getBlockState(placedPos).isAir()){
            world.setBlockState(placedPos, CoaxCable.ENTRY.getDefaultState());
            PlayerEntity player  = context.getPlayer();
            if(player!=null) {
                //block placement sound
                player.playSound(SoundEvent.of(new Identifier("minecraft:block.wool.place")), SoundCategory.BLOCKS, 1, 1);
                if(!player.isCreative()){
                    ItemStack item = context.getStack();
                    item.decrement(1);
                }
                player.swingHand(context.getHand());
            }
        }
        CoaxCable.ENTRY.onPlaced(world,placedPos,CoaxCable.ENTRY.getDefaultState(),context.getPlayer(), context.getStack());
        return super.useOnBlock(context);
    }
}
