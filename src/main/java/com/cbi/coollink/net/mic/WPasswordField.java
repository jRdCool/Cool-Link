package com.cbi.coollink.net.mic;

import io.github.cottonmc.cotton.gui.widget.WTextField;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.MutableComponent;
import org.jspecify.annotations.NonNull;

public class WPasswordField extends WTextField {
    boolean textVisible =false;
    public WPasswordField() {
        super();
    }

    public WPasswordField(MutableComponent suggestion) {
        super(suggestion);
    }


    @Override
    @Environment(EnvType.CLIENT)
    protected void renderText(@NonNull GuiGraphicsExtractor context, int x, int y, @NonNull String visibleText) {
        if(!textVisible) {
            String hiddenText = "";
            for (int i = 0; i < visibleText.length(); i++) {
                hiddenText += "*";
            }

            super.renderText(context, x, y, hiddenText);
        }else{
            super.renderText(context, x, y, visibleText);
        }
    }

    @Override
    @Environment(EnvType.CLIENT)
    protected void renderCursor(@NonNull GuiGraphicsExtractor context, int x, int y, @NonNull String visibleText) {
        if(!textVisible) {
            String hiddenText="";
            for(int i=0;i<visibleText.length();i++){
                hiddenText+="*";
            }
            super.renderCursor(context,x,y,hiddenText);
        }else{
            super.renderCursor(context,x,y,visibleText);
        }

    }

    @Override
    @Environment(EnvType.CLIENT)
    protected void renderSelection(@NonNull GuiGraphicsExtractor context, int x, int y, @NonNull String visibleText) {
        if(!textVisible) {
            String hiddenText = "";
            for (int i = 0; i < visibleText.length(); i++) {
                hiddenText += "*";
            }
            super.renderSelection(context, x, y, hiddenText);
        }else{
            super.renderSelection(context, x, y, visibleText);
        }
    }

    public void setShown(boolean shown){
        textVisible=shown;
    }
}
