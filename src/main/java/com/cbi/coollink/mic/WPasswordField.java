package com.cbi.coollink.mic;

import io.github.cottonmc.cotton.gui.widget.WTextField;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
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
    protected void renderText(MatrixStack matrices, int x, int y, String visibleText) {
        if(!textVisible) {
            String hiddenText = "";
            for (int i = 0; i < visibleText.length(); i++) {
                hiddenText += "*";
            }

            super.renderText(matrices, x, y, hiddenText);
        }else{
            super.renderText(matrices, x, y, visibleText);
        }
    }

    @Override
    @Environment(EnvType.CLIENT)
    protected void renderCursor(MatrixStack matrices, int x, int y, String visibleText) {
        if(!textVisible) {
            String hiddenText="";
            for(int i=0;i<visibleText.length();i++){
                hiddenText+="*";
            }
            super.renderCursor(matrices,x,y,hiddenText);
        }else{
            super.renderCursor(matrices,x,y,visibleText);
        }

    }

    @Override
    @Environment(EnvType.CLIENT)
    protected void renderSelection(MatrixStack matrices, int x, int y, String visibleText) {
        if(!textVisible) {
            String hiddenText = "";
            for (int i = 0; i < visibleText.length(); i++) {
                hiddenText += "*";
            }
            super.renderSelection(matrices, x, y, hiddenText);
        }else{
            super.renderSelection(matrices, x, y, visibleText);
        }
    }

    public void setShown(boolean shown){
        textVisible=shown;
    }
}
