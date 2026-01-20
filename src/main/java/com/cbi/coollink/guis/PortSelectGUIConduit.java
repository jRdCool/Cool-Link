package com.cbi.coollink.guis;

import com.cbi.coollink.Main;
import com.cbi.coollink.blocks.blockentities.ConduitBlockEntity;
import com.cbi.coollink.blocks.cables.createadditons.WireType;
import com.cbi.coollink.blocks.conduits.Conduit;
import com.cbi.coollink.net.WireInfoDataPacket;
import com.cbi.coollink.rendering.IWireNode;
import io.github.cottonmc.cotton.gui.client.CottonClientScreen;
import io.github.cottonmc.cotton.gui.client.LightweightGuiDescription;
import io.github.cottonmc.cotton.gui.widget.*;
import io.github.cottonmc.cotton.gui.widget.data.Axis;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.PlainTextContent;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

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
            int size = usedOnBlock.getCachedState().get(Conduit.cableLevel);
            int shape = usedOnBlock.getCachedState().get(Conduit.cableShape);
            //NSWE
            boolean[] connectedDirection = {usedOnBlock.getCachedState().get(Conduit.north),usedOnBlock.getCachedState().get(Conduit.south),usedOnBlock.getCachedState().get(Conduit.west),usedOnBlock.getCachedState().get(Conduit.east)};

            //figure out the total number of possible ports that can be connected to
            ArrayList<WButton> portButtons = new ArrayList<>();
            ArrayList<Integer> portButtonIndex = new ArrayList<>();
            ConduitBlockEntity.OtherEnd otherEnd;
            int numDirections=1;
            //NSEW   Direction.NORTH;
            Direction openFace = Direction.NORTH;
            switch (shape){
                case 1 ->{
                    if(connectedDirection[2]){openFace=Direction.EAST;
                    }else if(connectedDirection[3]){openFace=Direction.WEST;
                    }else{numDirections=2;}
                }//EW
                case 4 ->{
                    if(connectedDirection[0]){openFace=Direction.EAST;
                    }else if(connectedDirection[1]){Main.blank();//Set to north //used stop IED Complaining about being blank or unnecessary
                    }else{numDirections=2;}
                }//NE
                case 5 ->{
                    if(connectedDirection[3]){openFace=Direction.SOUTH;
                    }else if(connectedDirection[1]){openFace=Direction.EAST;
                    }else{numDirections=2;}
                }//SE
                case 6 ->{
                    if(connectedDirection[2]){openFace=Direction.SOUTH;
                    }else if(connectedDirection[1]){openFace=Direction.WEST;
                    }else{numDirections=2;}
                }//SW
                case 7 ->{
                    if(connectedDirection[0]){openFace=Direction.WEST;
                    }else if(connectedDirection[2]){Main.blank();//Set to north //used stop IED Complaining about being blank or unnecessary
                    }else{numDirections=2;}
                }//NW
                default -> {
                    if(connectedDirection[0]){openFace=Direction.SOUTH;
                    }else if(connectedDirection[1]){Main.blank();//Set to north //used stop IED Complaining about being blank or unnecessary
                    }else{numDirections=2;}
                }//NS
            }

            //create a button for each of them
            int x = 237 ,y1= 7,y2=60;

            for (int i = 0; i < ofType.size(); i++) {
                int tubeDirection = intNodeDirection(ofType.get(i));
                int tube = tubeNumber(ofType.get(i));
                int wire = wireNum(ofType.get(i));
                boolean outside = (size <= 2 && (tube < 4 || tube > 8)) || (size == 1 && (tube != 6));
                if((numDirections==1)&&(node instanceof ConduitBlockEntity conduit)) {
                    otherEnd = ConduitBlockEntity.otherConduitEnd(conduit, openFace);
                    ConduitBlockEntity otherBlock = otherEnd.blockEntity();
                    Direction otherEndDir = otherEnd.direction();
                    int otherIndex = ConduitBlockEntity.directionIndexTranslation(otherEndDir,i);
                    //otherBlock.
                    //Main.LOGGER.info("Local Index:"+Integer.toBinaryString(i)+" Other Index:"+Integer.toBinaryString(otherIndex));
                    //Main.LOGGER.info("oX:"+otherBlock.getPos().getX()+" oY:"+otherBlock.getPos().getY()+" oZ:"+otherBlock.getPos().getZ()+" oDir:"+otherEndDir.asString()+" lDir:"+openFace.asString());
                    if(tube < 13) {
                        switch (openFace) {
                            case EAST -> {
                                if (outside) break;
                                if (tubeDirection == 3) {
                                    if(node.isNodeInUse(ofType.get(i))){
                                        portButtons.add(new WButton(coloredText(i, tube, wire, usedOnBlock)));
                                        portButtonIndex.add(i);
                                    } else if (otherBlock.getPortType(otherIndex) == type||otherBlock.getPortType(otherIndex)==WireType.ANY) {
                                        portButtons.add(new WButton(coloredText(otherIndex, tube, wire, otherBlock)));
                                        portButtonIndex.add(i);
                                    } else {
                                        portButtons.add(new WButton(coloredText(otherIndex, tube, wire, otherBlock)));
                                        portButtonIndex.add(i);
                                        portButtons.getLast().setEnabled(false);
                                    }
                                }
                            }
                            case WEST -> {
                                if (outside) break;
                                if (tubeDirection == 2) {
                                    if(node.isNodeInUse(ofType.get(i))){
                                        portButtons.add(new WButton(coloredText(i, tube, wire, usedOnBlock)));
                                        portButtonIndex.add(i);
                                    } else if (otherBlock.getPortType(otherIndex) == type||otherBlock.getPortType(otherIndex)==WireType.ANY) {
                                        portButtons.add(new WButton(coloredText(otherIndex, tube, wire, otherBlock)));
                                        portButtonIndex.add(i);
                                    } else {
                                        portButtons.add(new WButton(coloredText(otherIndex, tube, wire, otherBlock)));
                                        portButtonIndex.add(i);
                                        portButtons.getLast().setEnabled(false);
                                    }
                                }
                            }
                            case SOUTH -> {
                                if (outside) break;
                                if (tubeDirection == 1) {
                                    if(node.isNodeInUse(ofType.get(i))){
                                        portButtons.add(new WButton(coloredText(i, tube, wire, usedOnBlock)));
                                        portButtonIndex.add(i);
                                    } else if (otherBlock.getPortType(otherIndex) == type||otherBlock.getPortType(otherIndex)==WireType.ANY) {
                                        portButtons.add(new WButton(coloredText(otherIndex, tube, wire, otherBlock)));
                                        portButtonIndex.add(i);
                                    } else {
                                        portButtons.add(new WButton(coloredText(otherIndex, tube, wire, otherBlock)));
                                        portButtonIndex.add(i);
                                        portButtons.getLast().setEnabled(false);
                                    }
                                }
                            }
                            default -> {
                                if (outside) break;
                                if (tubeDirection == 0) {
                                    if(node.isNodeInUse(ofType.get(i))){
                                        portButtons.add(new WButton(coloredText(i, tube, wire, usedOnBlock)));
                                        portButtonIndex.add(i);
                                    } else if (otherBlock.getPortType(otherIndex) == type||otherBlock.getPortType(otherIndex)==WireType.ANY) {
                                        portButtons.add(new WButton(coloredText(otherIndex, tube, wire, otherBlock)));
                                        portButtonIndex.add(i);
                                    } else {
                                        portButtons.add(new WButton(coloredText(otherIndex, tube, wire, otherBlock)));
                                        portButtonIndex.add(i);
                                        portButtons.getLast().setEnabled(false);
                                    }
                                }
                            }//north
                        }
                        if (node.isNodeInUse(ofType.get(i))) {
                            portButtons.getLast().setEnabled(false);
                        }
                        if (!portButtons.isEmpty()) {
                            //Main.LOGGER.info("length: "+portButtons.size());
                            portButtons.getLast().setSize(14, 15);
                        }
                    }
                }else {



                    //Main.LOGGER.info(i+","+nodeDirection(ofType.get(i))+","+tubeDirection+","+tube+","+ wire+","+shape+","+portButtons.size());

                    if (tube < 13) {//Main.LOGGER.info("result: "+(outside||connectedDirection[tubeDirection])+" Tube: "+tubeDirection+" CD0: "+connectedDirection[0]+" CD1: "+connectedDirection[1]+" CD2: "+connectedDirection[2]+" CD3: "+connectedDirection[3]);
                        switch (shape) {
                            case 1 -> {
                                if (outside) break;
                                if (connectedDirection[tubeDirection]) {
                                    numDirections = 1;
                                    break;
                                }
                                if (tubeDirection >= 2) {
                                    portButtons.add(new WButton(coloredText(i, tube, wire, usedOnBlock)));
                                    portButtonIndex.add(i);
                                    //Main.LOGGER.info("Registered");
                                }
                            }//EW
                            case 4 -> {
                                if (outside) break;
                                if (connectedDirection[tubeDirection]) {
                                    numDirections = 1;
                                    break;
                                }
                                if (tubeDirection == 0 || tubeDirection == 3) {
                                    portButtons.add(new WButton(coloredText(i, tube, wire, usedOnBlock)));
                                    portButtonIndex.add(i);
                                }
                            }//NE
                            case 5 -> {
                                if (outside) break;
                                if (connectedDirection[tubeDirection]) {
                                    numDirections = 1;
                                    break;
                                }
                                if (tubeDirection % 2 == 1) {
                                    portButtons.add(new WButton(coloredText(i, tube, wire, usedOnBlock)));
                                    portButtonIndex.add(i);
                                }
                            }//SE
                            case 6 -> {
                                if (outside) break;
                                if (connectedDirection[tubeDirection]) {
                                    numDirections = 1;
                                    break;
                                }
                                if (tubeDirection == 1 || tubeDirection == 2) {
                                    portButtons.add(new WButton(coloredText(i, tube, wire, usedOnBlock)));
                                    portButtonIndex.add(i);
                                }
                            }//SW
                            case 7 -> {
                                if (outside) break;
                                if (connectedDirection[tubeDirection]) {
                                    numDirections = 1;
                                    break;
                                }
                                if (tubeDirection % 2 == 0) {
                                    portButtons.add(new WButton(coloredText(i, tube, wire, usedOnBlock)));
                                    portButtonIndex.add(i);
                                }
                            }//NW
                            default -> {
                                if (outside) break;
                                if (connectedDirection[tubeDirection]) {
                                    numDirections = 1;
                                    break;
                                }
                                if (tubeDirection < 2) {
                                    portButtons.add(new WButton(coloredText(i, tube, wire, usedOnBlock)));
                                    portButtonIndex.add(i);
                                }
                            }//NS
                        }
                        //Main.LOGGER.info("Buttons: "+portButtons.size());
                        //disable the ones that already have been connected
                        if (node.isNodeInUse(ofType.get(i))||(node.getPortType(i)!=WireType.ANY)&&(node.getPortType(i)!=type)) {
                            portButtons.getLast().setEnabled(false);
                        }
                        if (!portButtons.isEmpty()) {
                            //Main.LOGGER.info("length: "+portButtons.size());
                            portButtons.getLast().setSize(14, 15);
                        }
                    }
                }
            }


            //-----------label positioning
            switch (size){
                case 1 ->x=11;//Small
                case 2 ->x=87;//Medium
                default ->{}//Large
            }
            if(numDirections==1){
                y2=y1;
            }
            switch (shape){
                case 1 -> {
                    if(!connectedDirection[2])root.add(new WLabel(Text.of("West")), x, y1);
                    if(!connectedDirection[3])root.add(new WLabel(Text.of("East")), x, y2);
                }//EW
                case 4-> {
                    if(!connectedDirection[0])root.add(new WLabel(Text.of("North")), x, y1);
                    if(!connectedDirection[3])root.add(new WLabel(Text.of("East")), x, y2);
                }//NS
                case 5 ->{
                    if(!connectedDirection[1])root.add(new WLabel(Text.of("South")), x, y1);
                    if(!connectedDirection[3])root.add(new WLabel(Text.of("East")), x, y2);
                }//SE
                case 6 ->{
                    if(!connectedDirection[1])root.add(new WLabel(Text.of("South")), x, y1);
                    if(!connectedDirection[2])root.add(new WLabel(Text.of("West")), x, y2);
                }//SW
                case 7 ->{
                    if(!connectedDirection[0])root.add(new WLabel(Text.of("North")), x, y1);
                    if(!connectedDirection[2])root.add(new WLabel(Text.of("West")), x, y2);
                }//NW
                default -> {
                    if(!connectedDirection[0])root.add(new WLabel(Text.of("North")), x, y1);
                    if(!connectedDirection[1])root.add(new WLabel(Text.of("South")), x, y2);
                }//NS
            }


            int halfPortButtons = portButtons.size()/2;
            float hPB= (float) portButtons.size()/2; //same as line above only as a float to work better in the window sizing context
            //Main.LOGGER.info("with: "+((((double) portButtons.size() /numDirections)*1.23*(portButtons.getFirst().getWidth()+1.5))/2)+" num buttons: "+portButtons.size()+" button width: "+portButtons.getFirst().getWidth());
            //root.setSize((int)(((hPB) * 1.2 * (portButtons.getFirst().getWidth() + 1.5)) / 2) + portButtons.getFirst().getWidth(), portButtons.getFirst().getHeight() * (4*numDirections) + 5);//Set the Windows size
            int widthOffset =switch(size){
                case 1 ->10;
                case 2 ->5;
                default -> 0;
            };
            root.setSize((int)((((double) portButtons.size() /numDirections)*1.23*(portButtons.getFirst().getWidth()+1.5))/2)+widthOffset, portButtons.getFirst().getHeight() * (4*numDirections) + 5);//Set the Windows size
            setRootPanel(root);
            //Main.LOGGER.info("Panel Width: "+root.getWidth());
            //add to the panel

            if(numDirections==2) {
                for (int i = 0; i < halfPortButtons; i += 2) {
                    root.add(portButtons.get(i), (int) (i * portButtons.get(i).getWidth() / 1.525) + (int) (portButtons.get(i).getWidth() / 1.5) + 3 - 4, (int) (portButtons.get(i).getHeight() * .5) + 10);
                    root.add(portButtons.get(i + 1), (int) (i * portButtons.get(i + 1).getWidth() / 1.525) + (int) (portButtons.get(i).getWidth() / 1.5) - 4, (int) (portButtons.get(i).getHeight() * 1.5) + 10);

                    root.add(portButtons.get(halfPortButtons + i), (int) (i * portButtons.get(halfPortButtons + i).getWidth() / 1.525) + (int) (portButtons.get(i).getWidth() / 1.5) - 4, (int) (portButtons.get(i).getHeight() * 4.0));
                    root.add(portButtons.get(halfPortButtons + i + 1), (int) (i * portButtons.get(halfPortButtons + i + 1).getWidth() / 1.525) + (int) (portButtons.get(i).getWidth() / 1.5) - 4, (int) (portButtons.get(i).getHeight() * 5.15));
                }
            }else{
                int buffer =switch(size){
                    case 1,2 ->(int) (portButtons.getFirst().getWidth() / 2.25);
                    default -> (int) (portButtons.getFirst().getWidth() / 1.5);
                };
                for(int i = 0; i < portButtons.size(); i +=2){
                    root.add(portButtons.get(i), (int) (i * portButtons.get(i).getWidth() / 1.525)+buffer  , (int) (portButtons.get(i).getHeight() * .5) + 10);
                    root.add(portButtons.get(i + 1), (int) (i * portButtons.get(i + 1).getWidth() / 1.525) + buffer , (int) (portButtons.get(i).getHeight() * 1.5) + 10);
                   //
                }
                //Main.LOGGER.info("Panel Width: "+root.getWidth());
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

    private MutableText coloredText(int index, int tube, int wire, BlockEntity blockEntity){
        Style textstyle = Style.EMPTY.withColor(0x0000FF);
        if(blockEntity instanceof IWireNode node){
            if(node.getPortType(index)!= null){
                textstyle =Style.EMPTY.withColor(node.getPortType(index).rgb()).withShadowColor(0xFFFFFF);
            } else{
                textstyle =Style.EMPTY.withColor(0xFFFFFF).withShadowColor(0xFFFFFF);
            }
            //Main.LOGGER.info("button"+tube+","+wire +"Wire type: "+node.getPortType(index).toString()+" RGB:"+Integer.toHexString(node.getPortType(index).rgb()));
        }
        return MutableText.of(new PlainTextContent.Literal(tube+","+wire)).setStyle(textstyle);
    }
}
