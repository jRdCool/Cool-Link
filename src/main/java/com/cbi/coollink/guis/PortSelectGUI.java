package com.cbi.coollink.guis;

import com.cbi.coollink.Main;
import com.cbi.coollink.blocks.cables.createadditons.WireType;
import com.cbi.coollink.net.WireInfoDataPacket;
import com.cbi.coollink.rendering.IWireNode;
import io.github.cottonmc.cotton.gui.client.CottonClientScreen;
import io.github.cottonmc.cotton.gui.client.LightweightGuiDescription;
import io.github.cottonmc.cotton.gui.widget.WButton;
import io.github.cottonmc.cotton.gui.widget.WPlainPanel;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

import java.util.ArrayList;

public class PortSelectGUI extends LightweightGuiDescription implements WantsScreenAccess{
    private boolean shouldClose = false;
    WPlainPanel root;
    CottonClientScreen screen;
    public PortSelectGUI(ArrayList<Integer> ofType, WireType type, BlockEntity usedOnBlock, ItemStack heldItem){
        if(usedOnBlock instanceof IWireNode node) {
            Main.LOGGER.info(usedOnBlock.toString());

            //figure out the total number of possible ports that can be connected to
            WButton[] portButtons = new WButton[ofType.size()];
            //create a button for each of them
            for (int i = 0; i < ofType.size(); i++) {
                portButtons[i] = new WButton(Text.of(""+ofType.get(i)));
                //disable the ones that already have been connected
                if(node.isNodeInUse(ofType.get(i))){
                    portButtons[i].setEnabled(false);
                }
            }
            root = new WPlainPanel();//.setBackgroundPainter(new BackgroundPainter());
            root.setSize((int)(portButtons.length*1.5*(portButtons[0].getWidth()+2)),portButtons[0].getHeight()*2);
            setRootPanel(root);
            //add to the panel
            for(int i=0;i<portButtons.length;i++){
                root.add(portButtons[i],(int)(i*portButtons[i].getWidth()*1.5)+(int)(portButtons[i].getWidth()*0.5),(int)(portButtons[i].getHeight()*0.5));
            }
            //set their click events
            for(int i=0;i<portButtons.length;i++){
                int finalI = i;
                portButtons[i].setOnClick(()->{
                    //send packet to the server to set the ports
                    ClientPlayNetworking.send(new WireInfoDataPacket(ofType.get(finalI),usedOnBlock.getPos(),heldItem,type,false));
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
}
