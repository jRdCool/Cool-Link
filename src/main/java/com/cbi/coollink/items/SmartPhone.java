package com.cbi.coollink.items;

import com.cbi.coollink.Main;
import com.cbi.coollink.app.AbstractPhoneApp;
import com.cbi.coollink.app.SettingsPhoneApp;
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

import java.util.ArrayList;

public class SmartPhone extends Item {
    public boolean clickedOnAIO=false;
    public ArrayList<AbstractPhoneApp> apps = new ArrayList<>();
    public SmartPhone(Settings settings) {
        super(settings);
        apps.add(SettingsPhoneApp.getDummyInstance());
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
            if(usedBlockEntity instanceof AIOBlockEntity){
                client.setScreen(new PhoneScreen(new PhoneGui(this, world, usedBlockEntity).openApp(SettingsPhoneApp.getDummyInstance())));
            }else {
                client.setScreen(new PhoneScreen(new PhoneGui(this, world, usedBlockEntity)));
            }
            usedBlockEntity=null;

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
                usedBlockEntity=be;
            }

        }
        return ActionResult.PASS;
    }
}
