package com.cbi.coollink.guis;

import io.github.cottonmc.cotton.gui.GuiDescription;
import io.github.cottonmc.cotton.gui.client.CottonClientScreen;

public class PhoneScreen extends CottonClientScreen {
    public PhoneScreen(GuiDescription description) {
        super(description);
    }

    @Override
    public boolean shouldPause(){
        return false;
    }

}
