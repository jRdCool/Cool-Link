package com.cbi.coollink.helppages;

import io.github.cottonmc.cotton.gui.widget.WText;
import io.github.cottonmc.cotton.gui.widget.WWidget;
import net.minecraft.text.Text;

public class HelpPageText extends HelpPageComponent{

    private final Text item;
    private final int height;

    public HelpPageText(Text text, int height){
        item = text;
        this.height = height;
    }

    @Override
    int getHeight() {
        return height;
    }

    @Override
    WWidget getItem() {
        return new WText(item);
    }
}
