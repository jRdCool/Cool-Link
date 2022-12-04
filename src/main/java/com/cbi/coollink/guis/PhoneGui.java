package com.cbi.coollink.guis;

import com.cbi.coollink.items.SmartPhone;
import io.github.cottonmc.cotton.gui.client.LightweightGuiDescription;
import io.github.cottonmc.cotton.gui.client.ScreenDrawing;
import io.github.cottonmc.cotton.gui.widget.*;
import io.github.cottonmc.cotton.gui.widget.icon.TextureIcon;
import net.minecraft.text.LiteralTextContent;
import net.minecraft.text.MutableText;
import net.minecraft.util.Identifier;


public class PhoneGui extends LightweightGuiDescription {
    public PhoneGui(SmartPhone phone){
        MutableText netButtonLabel = MutableText.of(new LiteralTextContent("Network Router"));
        WGridPanel root = new WGridPanel();//.setBackgroundPainter(new BackgroundPainter());
        setRootPanel(root);
        root.setSize(300,250);
        WButton accessPointButton = new WButton().setIcon(new TextureIcon(new Identifier("cool-link","textures/icon/cbi.png")));
        WLabel label = new WLabel(netButtonLabel);
        root.add(accessPointButton,2,1);
        root.add(label,1,3);

    }

    /**sets tha background of the GUI
     *
     */
    @Override
    public void addPainters() {
        if (this.rootPanel!=null && !fullscreen) {
            this.rootPanel.setBackgroundPainter((matrices, left, top, panel) -> {
                //sets the background to a textures                                                                                                                            UVs go form 0 to 1 indicating where on the image to pull from
                ScreenDrawing.texturedRect(matrices,left-4,top-4,panel.getWidth()+2,panel.getHeight()+2,new Identifier("cool-link","textures/gui/phone_background.png"),0,0,1,1,0xFF_FFFFFF);
            });
        }
    }
}
