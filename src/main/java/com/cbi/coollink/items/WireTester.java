package com.cbi.coollink.items;

import com.cbi.coollink.Main;
import com.cbi.coollink.blocks.networkdevices.RSSenderWired;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.SwingAnimation;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.TagValueOutput;
import org.jspecify.annotations.NonNull;


public class WireTester extends Item {

    public static final Identifier ID = Identifier.fromNamespaceAndPath(Main.namespace, "wire_tester");
    public static final ResourceKey<Item> ITEM_KEY = Main.createItemRegistryKey(ID);

    public WireTester(Properties settings) {
        super(settings.setId(ITEM_KEY));
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
        } else if (world.getBlockEntity(pos) instanceof BlockEntity be){
            TagValueOutput tvo = TagValueOutput.createWithoutContext(ProblemReporter.DISCARDING);
            be.saveWithFullMetadata(tvo);
            CompoundTag tag = tvo.buildResult();
            String env = "";
            if(context.getPlayer() instanceof ServerPlayer){
                env = "server";
            } else {
                env = "client";
            }
            Main.LOGGER.info(env+": "+ tag);
        }
        return super.useOn(context);
    }

}
