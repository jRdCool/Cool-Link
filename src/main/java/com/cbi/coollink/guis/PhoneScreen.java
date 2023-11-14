package com.cbi.coollink.guis;

import com.cbi.coollink.Main;
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

    @Override
    public boolean keyPressed(int ch,int keyCode,int modifiers){
        //Main.LOGGER.info(ch+" "+keyCode+" "+modifiers);
        if(description instanceof PhoneGui phoneGui){
            phoneGui.keyPressed(ch,keyCode,modifiers);
        }
        return super.keyPressed(ch,keyCode,modifiers);
    }

    @Override
    public boolean keyReleased(int ch,int keyCode,int modifiers){
        if(description instanceof PhoneGui phoneGui){
            phoneGui.keyReleased(ch,keyCode,modifiers);
        }
        return super.keyReleased(ch,keyCode,modifiers);
    }

    /*KEY MAPPINGS
    char        Code       key
    265         328        UP
    263         331        LEFT
    264         336        DOWN
    262         333        RIGHT


     */
}
