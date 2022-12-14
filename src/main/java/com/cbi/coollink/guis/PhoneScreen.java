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

    @Override
    public void tick() {
        super.tick();
        if(description instanceof PhoneGui phoneGui){
            phoneGui.tick();
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        super.mouseClicked(mouseX,mouseY,mouseButton);
        if(description instanceof PhoneGui phoneGui){
            phoneGui.mouseClicked(mouseX,mouseY);
        }
        return true;
    }

}
