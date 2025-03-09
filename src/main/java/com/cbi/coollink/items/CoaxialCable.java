package com.cbi.coollink.items;

import com.cbi.coollink.blocks.cables.CoaxCable;
import com.cbi.coollink.blocks.cables.createadditons.WireType;
import com.cbi.coollink.rendering.IWireNode;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.world.World;

import javax.imageio.event.IIOWriteProgressListener;

public class CoaxialCable extends ACableItem {
    public CoaxialCable(Settings settings) {
        super(settings);
    }

    /*@Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand){
        return super.use(world, user, hand);
    }*/


    public WireType TYPE = WireType.COAX;

}
