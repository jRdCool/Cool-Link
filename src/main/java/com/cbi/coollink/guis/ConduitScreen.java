package com.cbi.coollink.guis;

import io.github.cottonmc.cotton.gui.GuiDescription;
import io.github.cottonmc.cotton.gui.client.CottonClientScreen;

public class ConduitScreen extends CottonClientScreen {
    public ConduitScreen(GuiDescription description) {
        super(description);
    }

    @Override
    public boolean shouldPause(){
        return false;
    }
}
