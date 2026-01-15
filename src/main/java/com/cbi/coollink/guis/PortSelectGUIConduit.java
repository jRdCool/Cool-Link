package com.cbi.coollink.guis;

import com.cbi.coollink.Main;
import com.cbi.coollink.blocks.cables.createadditons.WireType;
import com.cbi.coollink.blocks.conduits.Conduit;
import com.cbi.coollink.net.WireInfoDataPacket;
import com.cbi.coollink.rendering.IWireNode;
import io.github.cottonmc.cotton.gui.client.CottonClientScreen;
import io.github.cottonmc.cotton.gui.client.LightweightGuiDescription;
import io.github.cottonmc.cotton.gui.widget.WButton;
import io.github.cottonmc.cotton.gui.widget.WLabel;
import io.github.cottonmc.cotton.gui.widget.WPlainPanel;
import io.github.cottonmc.cotton.gui.widget.WText;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.Direction;

import java.util.ArrayList;

public class PortSelectGUIConduit extends LightweightGuiDescription implements WantsScreenAccess{

    private boolean shouldClose = false;
    WPlainPanel root;
    CottonClientScreen screen;

    public PortSelectGUIConduit(ArrayList<Integer> ofType, WireType type, BlockEntity usedOnBlock, ItemStack heldItem) {
        if(usedOnBlock instanceof IWireNode node) {
            //Main.LOGGER.info(usedOnBlock.toString());
            Main.blank();
            root = new WPlainPanel();//.setBackgroundPainter(new BackgroundPainter());
            int shape = usedOnBlock.getCachedState().get(Conduit.cableShape);
            //figure out the total number of possible ports that can be connected to
            ArrayList<WButton> portButtons = new ArrayList<>();
            ArrayList<Integer> portButtonIndex = new ArrayList<>();
            //create a button for each of them
            for (int i = 0; i < ofType.size(); i++) {
                int tubeDirection = intNodeDirection(ofType.get(i));
                int tube = tubeNumber(ofType.get(i));
                int wire = wireNum(ofType.get(i));
                int x = 237 ,y1= 7,y2=60;
                //Main.LOGGER.info(i+","+nodeDirection(ofType.get(i))+","+tubeDirection+","+tube+","+ wire+","+shape+","+portButtons.size());
            //-----------label positioning
                if(tube<13) {
                    switch (shape) {
                        case 1 -> {
                            if(i==0) {
                                root.add(new WLabel(Text.of("West")), x, y1);
                                root.add(new WLabel(Text.of("East")), x, y2);
                            }
                            if (tubeDirection >= 2) {
                                portButtons.add(new WButton(Text.of(tube + "," + wire)));
                                portButtonIndex.add(i);
                            }
                        }//EW
                        case 4 -> {
                            if(i==0) {
                                root.add(new WLabel(Text.of("North")), x, y1);
                                root.add(new WLabel(Text.of("East")), x, y2);
                            }
                            if (tubeDirection == 0 || tubeDirection == 3) {
                                portButtons.add(new WButton(Text.of(tube + "," + wire)));
                                portButtonIndex.add(i);
                            }
                        }//NE
                        case 5 -> {
                            if(i==0) {
                                root.add(new WLabel(Text.of("South")), x, y1);
                                root.add(new WLabel(Text.of("East")), x, y2);
                            }
                            if (tubeDirection % 2 == 1) {
                                portButtons.add(new WButton(Text.of(tube + "," + wire)));
                                portButtonIndex.add(i);
                            }
                        }//SE
                        case 6 -> {
                            if(i==0) {
                                root.add(new WLabel(Text.of("South")), x, y1);
                                root.add(new WLabel(Text.of("West")), x, y2);
                            }
                            if (tubeDirection == 1 || tubeDirection == 2) {
                                portButtons.add(new WButton(Text.of(tube + "," + wire)));
                                portButtonIndex.add(i);
                            }
                        }//SW
                        case 7 -> {
                            if(i==0) {
                                root.add(new WLabel(Text.of("North")), x, y1);
                                root.add(new WLabel(Text.of("West")), x, y2);
                            }
                            if (tubeDirection % 2 == 0) {
                                portButtons.add(new WButton(Text.of(tube + "," + wire)));
                                portButtonIndex.add(i);
                            }
                        }//NW
                        default -> {
                            if(i==0) {
                                root.add(new WLabel(Text.of("North")), x, y1);
                                root.add(new WLabel(Text.of("South")), x, y2);
                            }
                            if (tubeDirection < 2) {
                                portButtons.add(new WButton(Text.of(tube + "," + wire)));
                                portButtonIndex.add(i);
                            }
                        }//NS
                    }
                    //disable the ones that already have been connected
                    if (node.isNodeInUse(ofType.get(i))) {
                        portButtons.getLast().setEnabled(false);
                    }
                    //Main.LOGGER.info("length: "+portButtons.size());
                    portButtons.getLast().setSize(14,15);
                }

            }

            Main.LOGGER.info("with: "+((portButtons.size()*1.5*(portButtons.get(0).getWidth()+1.5))/4)+" num buttons: "+portButtons.size()+" button width: "+portButtons.get(0).getWidth());
            root.setSize((int)(((portButtons.size()/2)*1.2*(portButtons.get(0).getWidth()+1.5))/2)+portButtons.get(0).getWidth(),portButtons.get(0).getHeight()*8+5);
            setRootPanel(root);
            //add to the panel
            int halfPortButtons = portButtons.size()/2;
            for(int i=0;i<halfPortButtons;i+=2){
                root.add(portButtons.get(i),(int)(i*portButtons.get(i).getWidth()/1.525)+(int)(portButtons.get(i).getWidth()/1.5)+3,(int)(portButtons.get(i).getHeight()*.5)+10);
                root.add(portButtons.get(i+1),(int)(i*portButtons.get(i+1).getWidth()/1.525)+(int)(portButtons.get(i).getWidth()/1.5),(int)(portButtons.get(i).getHeight()*1.5)+10);

                root.add(portButtons.get(halfPortButtons+i),(int)(i*portButtons.get(halfPortButtons+i).getWidth()/1.525)+(int)(portButtons.get(i).getWidth()/1.5),(int)(portButtons.get(i).getHeight()*4.0));
                root.add(portButtons.get(halfPortButtons+i+1),(int)(i*portButtons.get(halfPortButtons+i+1).getWidth()/1.525)+(int)(portButtons.get(i).getWidth()/1.5),(int)(portButtons.get(i).getHeight()*5.15));
            }
            //set their click events
            for(int i=0;i<portButtons.size();i++){
                int finalI = portButtonIndex.get(i);
                portButtons.get(i).setOnClick(()->{
                    //send packet to the server to set the ports
                    ClientPlayNetworking.send(new WireInfoDataPacket(ofType.get(finalI),usedOnBlock.getPos(),heldItem,type,false));
                    //Main.LOGGER.info("Index: "+Integer.toBinaryString(finalI)+" Dec:"+finalI);
                    //close the GUI
                    screen.close();
                });
            }

        }else{
            shouldClose = true;
        }
    }

    @Override
    public void setScreen(CottonClientScreen screen) {
        if(shouldClose){
            screen.close();
        }
        this.screen = screen;
    }

    private Direction nodeDirection(int node){
        int direction = (node & 0b11000000) >>> 6;//take the direction bits and shift the bits to the right
        return switch(direction){
            case 0b00 -> Direction.NORTH;//North
            case 0b01 -> Direction.SOUTH;//South
            case 0b10 -> Direction.WEST;//West
            case 0b11 -> Direction.EAST;//East
            default -> null;
        };
    }

    private int intNodeDirection(int node){
        return ((node & 0b11000000) >>> 6);//take the direction bits and shift the bits to the right
    }

    private int tubeNumber(int node){
        return ((node & 0b00111100) >>> 2);//take the tube bits and shift them to the right
    }

    private int wireNum(int node){
        return (node & 0b00000011);
    }
}
