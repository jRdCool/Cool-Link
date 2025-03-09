package com.cbi.coollink.items;

import com.cbi.coollink.Main;
import com.cbi.coollink.WireInfoComponent;
import com.cbi.coollink.blocks.cables.createadditons.WireType;
import com.cbi.coollink.net.OpenPortSelectGuiPacket;
import com.cbi.coollink.rendering.IWireNode;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
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


        if(world.getBlockEntity(pos) instanceof IWireNode node){
            Main.LOGGER.info("Clicked on an IWire node");
            if(stack.contains(Main.WIRE_INFO_COMPONENT)) {
                WireInfoComponent comp = stack.get(Main.WIRE_INFO_COMPONENT);

                if (comp!=null&&(!comp.originBlock().isWithinDistance(pos, 16))) {
                    if (context.getPlayer() instanceof ServerPlayerEntity player) {player.sendMessageToClient(Text.of("!!Nodes are too far apart. Aborting!!"), false);}
                    stack.remove(Main.WIRE_INFO_COMPONENT);
                    return super.useOnBlock(context);
                }
            }
            int index = 0;
            int nodeCount = node.getNodeCount();
            ArrayList<Integer> ofType = new ArrayList<>();
            for(int i=0;i< nodeCount;i++){
                if(node.getPortType(i)==TYPE){
                    ofType.add(i);
                }
            }
            if(ofType.isEmpty()){
                if(context.getPlayer() instanceof ServerPlayerEntity player){player.sendMessageToClient(Text.of("No " + TYPE.toString() + " ports on this device"),false);}
                return super.useOnBlock(context);
            }
            if(ofType.size()>1){
                /*TODO: Open up gui to select desired port
                 * Pass ofType, TYPE, world, and pos
                 * return the integer index of the selected port
                 * Have the GUI display the ports available and weather or not they are currently used
                 * gather used ports and display as used (isNodeInUse method)
                 */
                //Main.LOGGER.info("IM IN HERE!");
                if(context.getPlayer() instanceof ServerPlayerEntity sp) {
                    ServerPlayNetworking.send(sp, new OpenPortSelectGuiPacket(ofType, TYPE, context.getWorld().getRegistryKey(), context.getBlockPos(), context.getStack()));
                }
            }else if (nodeCount>1){
                index = ofType.get(0);
            }

            if(stack.contains(Main.WIRE_INFO_COMPONENT)){
                Main.LOGGER.info("Info component present");
                WireInfoComponent comp = stack.get(Main.WIRE_INFO_COMPONENT);
                Main.LOGGER.info(comp.originBlock().toString());
                if(comp!=null&&world.getBlockEntity(comp.originBlock()) instanceof IWireNode block){
                   block.setNode(comp.index(),index,pos,TYPE);
                    ((IWireNode) world.getBlockEntity(pos)).setNode(comp.index(),index,block.getPos(),TYPE);
                    Main.LOGGER.info("Rendering");
                    Main.LOGGER.info(block.getNodeOffset(comp.index())+"");
                }
                //TODO: connect set the wire to render

                stack.remove(Main.WIRE_INFO_COMPONENT);
            }
            else {
                stack.set(Main.WIRE_INFO_COMPONENT, new WireInfoComponent(index, pos));
                Main.LOGGER.info("Stack Component Set");
                Main.LOGGER.info(stack.get(Main.WIRE_INFO_COMPONENT).toString());
            }
        }
        if(context.getPlayer()!= null) context.getPlayer().swingHand(context.getHand());


        //CoaxCable.ENTRY.onPlaced(world,placedPos,CoaxCable.ENTRY.getDefaultState(),context.getPlayer(), context.getStack());
        return super.useOnBlock(context);
    }

}
