package com.cbi.coollink.items;

import com.cbi.coollink.Main;
import com.cbi.coollink.WireInfoComponent;
import com.cbi.coollink.blocks.blockentities.ConduitBlockEntity;
import com.cbi.coollink.blocks.cables.createadditons.WireType;
import com.cbi.coollink.net.OpenPortSelectGuiPacket;
import com.cbi.coollink.rendering.IWireNode;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSystemChatPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.SwingAnimation;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;

public abstract class ACableItem extends Item {
    public ACableItem(Properties settings) {
        super(settings);
    }

    public WireType TYPE = WireType.COAX;

    @Override
    public @NonNull InteractionResult useOn(UseOnContext context) {
        BlockPos pos=context.getClickedPos();
        Level world=context.getLevel();
        ItemStack stack = context.getItemInHand();


        if(world.getBlockEntity(pos) instanceof IWireNode node) {
            //Main.LOGGER.info("Clicked on an IWire node");
            if (stack.has(Main.WIRE_INFO_COMPONENT)) {
                WireInfoComponent comp = stack.get(Main.WIRE_INFO_COMPONENT);
                if (comp != null && (!comp.originBlock().closerToCenterThan(pos, 16))) {
                    if (context.getPlayer() instanceof ServerPlayer player) {
                        //player.sendMessageToClient(Text.of("!!Nodes are too far apart. Aborting!!"), false);
                        player.connection.send(new ClientboundSystemChatPacket(Component.literal("!!Nodes are too far apart. Aborting!!"),true));
                    }
                    stack.remove(Main.WIRE_INFO_COMPONENT);
                    return super.useOn(context);
                }
            }
            int index = 0;
            int nodeCount = node.getNodeCount();
            ArrayList<Integer> ofType = new ArrayList<>();

            for (int i = 0; i < nodeCount; i++) {
                if ((node.getPortType(i) == TYPE || node.getPortType(i) == WireType.ANY)||(node instanceof ConduitBlockEntity)) {
                    ofType.add(i);
                }
            }
            if (ofType.isEmpty()) {
                if (context.getPlayer() instanceof ServerPlayer player) {
                    //player.sendMessageToClient(Text.of("No " + TYPE.toString() + " ports on this device"), false);
                    player.connection.send(new ClientboundSystemChatPacket(Component.literal("No " + TYPE.toString() + " ports on this device"),true));
                }
                return super.useOn(context);
            }
            if (ofType.size() > 1) {
                if (context.getPlayer() instanceof ServerPlayer sp) {
                    ServerPlayNetworking.send(sp, new OpenPortSelectGuiPacket(ofType, TYPE, context.getLevel().dimension(), context.getClickedPos(), context.getItemInHand()));
                }
                return super.useOn(context);
            } else if (nodeCount > 1) {
                index = ofType.get(0);
            }
            if (world.getBlockEntity(pos) instanceof IWireNode tb && tb.isNodeInUse(index)) {
                return super.useOn(context);
            }

            if (stack.has(Main.WIRE_INFO_COMPONENT)) {
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

                if (context.getPlayer() instanceof ServerPlayer player) {
                    player.connection.send(new ClientboundSystemChatPacket(Component.literal("Starting "+ TYPE.toString()+" Connection at: "+ pos.getX()+", "+pos.getY()+", "+pos.getZ()),true));
                }
            }
            if (context.getPlayer() !=null) {context.getPlayer().swing(context.getHand(), SwingAnimation.DEFAULT,true);}
        }



        //CoaxCable.ENTRY.onPlaced(world,placedPos,CoaxCable.ENTRY.getDefaultState(),context.getPlayer(), context.getStack());
        return super.useOn(context);
    }

}
