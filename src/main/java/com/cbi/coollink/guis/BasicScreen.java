package com.cbi.coollink.guis;

import io.github.cottonmc.cotton.gui.GuiDescription;
import io.github.cottonmc.cotton.gui.client.CottonClientScreen;

public class BasicScreen extends CottonClientScreen {
    public BasicScreen(GuiDescription description) {
        super(description);
        if(description instanceof WantsScreenAccess wsa){
            wsa.setScreen(this);
        }
    }

    @Override
    public boolean shouldPause(){
        return false;
    }
}
