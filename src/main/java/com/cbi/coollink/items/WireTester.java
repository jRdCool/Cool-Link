package com.cbi.coollink.items;

import com.cbi.coollink.Main;
import com.cbi.coollink.blocks.networkdevices.RSSenderWired;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.SwingAnimation;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.NonNull;


public class WireTester extends Item {

    public static final Identifier ID = Identifier.fromNamespaceAndPath(Main.namespace, "wire_tester");
    public static final ResourceKey<Item> ITEM_KEY = Main.createItemRegistryKey(ID);

    public WireTester(Properties settings) {
        super(settings);
    }

    @Override
    public @NonNull InteractionResult useOn(@NonNull UseOnContext context) {
        BlockPos pos = context.getClickedPos();
        Level world = context.getLevel();
        //ItemStack stack = context.getStack();
        BlockState state = world.getBlockState(pos);
        if(state.getBlock() instanceof RSSenderWired sender){

            if(state.getSignal(world,pos, Direction.NORTH)==0){
                sender.setPower(world,state,15,pos,1);
                //Main.LOGGER.info("Set to 15");
            }
            else{
                sender.setPower(world,state,0,pos,1);
                //Main.LOGGER.info("Set to 0");
            }
            if (context.getPlayer() !=null) {context.getPlayer().swing(context.getHand(), SwingAnimation.DEFAULT, true);}
        }
        return super.useOn(context);
    }

}
