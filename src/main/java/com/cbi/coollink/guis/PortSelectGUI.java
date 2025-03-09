package com.cbi.coollink.guis;

import io.github.cottonmc.cotton.gui.client.LightweightGuiDescription;
import io.github.cottonmc.cotton.gui.widget.WPlainPanel;

public class PortSelectGUI extends LightweightGuiDescription {
    WPlainPanel root;
    public PortSelectGUI(){
        root = new WPlainPanel();//.setBackgroundPainter(new BackgroundPainter());
        setRootPanel(root);
    }
}
