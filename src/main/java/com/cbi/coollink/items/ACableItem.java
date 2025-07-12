package com.cbi.coollink.items;

import com.cbi.coollink.Main;
import com.cbi.coollink.WireInfoComponent;
import com.cbi.coollink.blocks.cables.createadditons.WireType;
import com.cbi.coollink.net.OpenPortSelectGuiPacket;
import com.cbi.coollink.rendering.IWireNode;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.network.packet.s2c.play.OverlayMessageS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;

public class ACableItem extends Item {
    public ACableItem(Settings settings) {
        super(settings);
    }

    public WireType TYPE = WireType.COAX;

    public ActionResult useOnBlock(ItemUsageContext context) {
        BlockPos pos=context.getBlockPos();
        World world=context.getWorld();
        ItemStack stack = context.getStack();


        if(world.getBlockEntity(pos) instanceof IWireNode node) {
            //Main.LOGGER.info("Clicked on an IWire node");
            if (stack.contains(Main.WIRE_INFO_COMPONENT)) {
                WireInfoComponent comp = stack.get(Main.WIRE_INFO_COMPONENT);

                if (comp != null && (!comp.originBlock().isWithinDistance(pos, 16))) {
                    if (context.getPlayer() instanceof ServerPlayerEntity player) {
                        //player.sendMessageToClient(Text.of("!!Nodes are too far apart. Aborting!!"), false);
                        player.networkHandler.sendPacket(new OverlayMessageS2CPacket(Text.of("!!Nodes are too far apart. Aborting!!")));
                    }
                    stack.remove(Main.WIRE_INFO_COMPONENT);
                    return super.useOnBlock(context);
                }
            }
            int index = 0;
            int nodeCount = node.getNodeCount();
            ArrayList<Integer> ofType = new ArrayList<>();
            for (int i = 0; i < nodeCount; i++) {
                if (node.getPortType(i) == TYPE) {
                    ofType.add(i);
                }
            }
            if (ofType.isEmpty()) {
                if (context.getPlayer() instanceof ServerPlayerEntity player) {
                    //player.sendMessageToClient(Text.of("No " + TYPE.toString() + " ports on this device"), false);
                    player.networkHandler.sendPacket(new OverlayMessageS2CPacket(Text.of("No " + TYPE.toString() + " ports on this device")));
                }
                return super.useOnBlock(context);
            }
            if (ofType.size() > 1) {
                if (context.getPlayer() instanceof ServerPlayerEntity sp) {
                    ServerPlayNetworking.send(sp, new OpenPortSelectGuiPacket(ofType, TYPE, context.getWorld().getRegistryKey(), context.getBlockPos(), context.getStack()));
                }
                return super.useOnBlock(context);
            } else if (nodeCount > 1) {
                index = ofType.get(0);
            }
            if (world.getBlockEntity(pos) instanceof IWireNode tb && tb.isNodeInUse(index)) {
                return super.useOnBlock(context);
            }

            if (stack.contains(Main.WIRE_INFO_COMPONENT)) {
                Main.LOGGER.info("Info component present");
                WireInfoComponent comp = stack.get(Main.WIRE_INFO_COMPONENT);
                //Main.LOGGER.info(comp.originBlock().toString());
                if (comp != null && world.getBlockEntity(comp.originBlock()) instanceof IWireNode block) {
                    //pos = B
                    //block = A
                    //comp.originBlock = A
                    //node = B
                    //Main.LOGGER.info(pos+" "+block.getPos()+" "+comp.originBlock()+" "+node.getPos());
                    block.setNode(comp.index(), index, pos, TYPE);
                    node.setNode(index, comp.index(), comp.originBlock(), TYPE);
                    //Main.LOGGER.info("Rendering");
                    //Main.LOGGER.info(block.getNodeOffset(comp.index())+"");}
                }

                stack.remove(Main.WIRE_INFO_COMPONENT);
            } else {
                stack.set(Main.WIRE_INFO_COMPONENT, new WireInfoComponent(index, pos));

                if (context.getPlayer() instanceof ServerPlayerEntity player) {
                    player.networkHandler.sendPacket(new OverlayMessageS2CPacket(Text.of("Starting "+ TYPE.toString()+" Connection at: "+ pos.getX()+", "+pos.getY()+", "+pos.getZ())));
                }
                //Main.LOGGER.info("Stack Component Set");
                //Main.LOGGER.info(stack.get(Main.WIRE_INFO_COMPONENT).toString());
            }
            if (context.getPlayer() !=null) {context.getPlayer().swingHand(context.getHand());}
        }



        //CoaxCable.ENTRY.onPlaced(world,placedPos,CoaxCable.ENTRY.getDefaultState(),context.getPlayer(), context.getStack());
        return super.useOnBlock(context);
    }

}
