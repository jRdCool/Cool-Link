package com.cbi.coollink.guis;

import com.cbi.coollink.Main;
import io.github.cottonmc.cotton.gui.GuiDescription;
import io.github.cottonmc.cotton.gui.client.CottonClientScreen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import org.jspecify.annotations.NonNull;

public class PhoneScreen extends CottonClientScreen {
    public PhoneScreen(GuiDescription description) {
        super(description);
    }

    @Override
    public boolean isPauseScreen(){
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
    public boolean mouseClicked(@NonNull MouseButtonEvent click, boolean doubled) {
        super.mouseClicked(click,doubled);
        if(description instanceof PhoneGui phoneGui){
            phoneGui.mouseClicked(click.x(),click.y());
        }
        return true;
    }

    @Override
    public boolean keyPressed(@NonNull KeyEvent input){
        //Main.LOGGER.info(ch+" "+keyCode+" "+modifiers);
        if(description instanceof PhoneGui phoneGui){
            phoneGui.keyPressed(input.key(),input.keycode(),input.modifiers());
        }
        try {
            return super.keyPressed(input);
        } catch (Exception e){
            Main.LOGGER.error("Error processing keyboard in phone GUI: ",e);
        }
        return false;
    }

    @Override
    public boolean keyReleased(@NonNull KeyEvent input){
        if(description instanceof PhoneGui phoneGui){
            phoneGui.keyReleased(input.key(),input.keycode(),input.modifiers());
        }
        return super.keyReleased(input);
    }

    /*KEY MAPPINGS
    char        Code       key
    265         328        UP
    263         331        LEFT
    264         336        DOWN
    262         333        RIGHT


     */
}
