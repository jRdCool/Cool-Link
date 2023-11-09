package com.cbi.coollink.guis;

import io.github.cottonmc.cotton.gui.client.LightweightGuiDescription;
import io.github.cottonmc.cotton.gui.widget.WPlainPanel;

public class ConduitGUI extends LightweightGuiDescription {
    WPlainPanel root;

    public ConduitGUI(){
        root = new WPlainPanel();//.setBackgroundPainter(new BackgroundPainter());
        setRootPanel(root);
    }
}
