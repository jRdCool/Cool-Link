package com.cbi.coollink.items;

import com.cbi.coollink.Main;
import com.cbi.coollink.WireInfoComponent;
import com.cbi.coollink.blocks.cables.createadditons.WireType;
import com.cbi.coollink.rendering.IWireNode;
import net.minecraft.component.ComponentType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.ArrayList;

public class ACableItem extends Item {
    public ACableItem(Settings settings) {
        super(settings);
    }

    public WireType TYPE = WireType.COAX;

    public ActionResult useOnBlock(ItemUsageContext context) {
        BlockPos pos=context.getBlockPos();
        BlockPos placedPos;
        Direction dir=context.getSide();;
        World world=context.getWorld();
        ItemStack stack = context.getStack();



        if(world.getBlockEntity(pos) instanceof IWireNode node){
            if(stack.contains(Main.WIRE_INFO_COMPONENT)) {
                WireInfoComponent comp = stack.get(Main.WIRE_INFO_COMPONENT);
                if (!comp.originBlock().isWithinDistance(pos, 16)) {
                    if (context.getPlayer() instanceof ServerPlayerEntity player) {player.sendMessageToClient(Text.of("!!Nodes are too far apart. Aborting!!"), false);}
                    stack.remove(Main.WIRE_INFO_COMPONENT);
                    return super.useOnBlock(context);
                }
            }
            int index = 0;
            int nodeCount = node.getNodeCount();
            WireType[] nodes = new WireType[nodeCount];
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
                //TODO: Open up gui to select desired port
            }
            if(stack.contains(Main.WIRE_INFO_COMPONENT)){
                WireInfoComponent comp = stack.get(Main.WIRE_INFO_COMPONENT);

                if(comp.originBlock() instanceof  IWireNode){
                    ((IWireNode) comp.originBlock()).setNode(comp.index(),index,pos,TYPE);
                }
                //TODO: connect set the wire to render

                stack.remove(Main.WIRE_INFO_COMPONENT);
            }
            else {
                stack.set(Main.WIRE_INFO_COMPONENT, new WireInfoComponent(index, pos));
            }
        }

        //CoaxCable.ENTRY.onPlaced(world,placedPos,CoaxCable.ENTRY.getDefaultState(),context.getPlayer(), context.getStack());
        return super.useOnBlock(context);
    }

}
