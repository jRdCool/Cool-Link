package com.cbi.coollink.items;

import com.cbi.coollink.Main;
import com.cbi.coollink.blocks.AIOBlockEntity;
import com.cbi.coollink.guis.PhoneGui;
import com.cbi.coollink.guis.PhoneScreen;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SmartPhone extends Item {
    public boolean clickedOnAIO=false;
    public SmartPhone(Settings settings) {
        super(settings);
    }
    public BlockEntity usedBlockEntity;


    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand){

        String currentThread = Thread.currentThread().getName();
        //check witch thread the code is being executed on
        if(currentThread.equals("Render thread")) {//if the code is being executed on the render thread
            //Main.LOGGER.info("normal");
            //open the phone GUI

            MinecraftClient client = MinecraftClient.getInstance();
            client.setScreen(new PhoneScreen(new PhoneGui(this,world)));
            clickedOnAIO=false;

        }
        //MinecraftClient.getInstance().setScreen(new PhoneScreen(new PhoneGui()));
        return super.use(world, user, hand);
    }

    public ActionResult useOnBlock(ItemUsageContext context) {//this gets called before use if used on a block
        if(Thread.currentThread().getName().equals("Render thread")) {
            BlockPos pos = context.getBlockPos();
            //Main.LOGGER.info(pos.toShortString());
            BlockEntity be = context.getWorld().getBlockEntity(pos);
            if (be != null) {
                //Main.LOGGER.info(be.getClass().getName());
                if(be instanceof AIOBlockEntity aio){
                    //Main.LOGGER.info("its an aio");
                    usedBlockEntity=aio;
                    clickedOnAIO=true;
                }
            }

        }
        return ActionResult.PASS;
    }
}
