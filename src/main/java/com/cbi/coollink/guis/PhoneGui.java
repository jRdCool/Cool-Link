package com.cbi.coollink.guis;

import io.github.cottonmc.cotton.gui.client.LightweightGuiDescription;
import io.github.cottonmc.cotton.gui.widget.WGridPanel;

public class PhoneGui extends LightweightGuiDescription {
    public PhoneGui(){
        WGridPanel root = new WGridPanel();
        setRootPanel(root);
        root.setSize(100,200);
    }
}
