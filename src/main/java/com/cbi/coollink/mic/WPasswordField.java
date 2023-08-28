package com.cbi.coollink.mic;

import io.github.cottonmc.cotton.gui.widget.WTextField;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

public class WPasswordField extends WTextField {
    boolean textVisible =false;
    public WPasswordField() {
        super();
    }

    public WPasswordField(Text suggestion) {
        super(suggestion);
    }

    @Override
    @Environment(EnvType.CLIENT)
    protected void renderText(DrawContext context, int x, int y, String visibleText) {
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
    protected void renderCursor(DrawContext context, int x, int y, String visibleText) {
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
    protected void renderSelection(DrawContext context, int x, int y, String visibleText) {
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
