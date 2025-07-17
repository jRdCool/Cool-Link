package com.cbi.coollink.items;

import com.cbi.coollink.Main;
import com.cbi.coollink.blocks.networkdevices.RSSenderWired;
import net.minecraft.block.BlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class WireTester extends Item {

    public static final Identifier ID = Identifier.of(Main.namespace, "wire_tester");
    public static final RegistryKey<Item> ITEM_KEY = Main.createItemRegistryKey(ID);

    public WireTester(Settings settings) {
        super(settings);
    }

    public ActionResult useOnBlock(ItemUsageContext context) {
        BlockPos pos = context.getBlockPos();
        World world = context.getWorld();
        //ItemStack stack = context.getStack();
        BlockState state = world.getBlockState(pos);
        if(state.getBlock() instanceof RSSenderWired sender){
            //Main.LOGGER.info("Clicked on sender");
            //Main.LOGGER.info("Power = "+state.getWeakRedstonePower(world,pos, Direction.NORTH));
            if(state.getWeakRedstonePower(world,pos, Direction.NORTH)==0){
                sender.setPower(world,state,15,pos,1);
                //Main.LOGGER.info("Set to 15");
            }
            else{
                sender.setPower(world,state,0,pos,1);
                //Main.LOGGER.info("Set to 0");
            }
            if (context.getPlayer() !=null) {context.getPlayer().swingHand(context.getHand());}
        }

        return super.useOnBlock(context);
    }

}
