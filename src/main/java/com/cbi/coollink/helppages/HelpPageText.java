package com.cbi.coollink.helppages;

import io.github.cottonmc.cotton.gui.widget.WText;
import io.github.cottonmc.cotton.gui.widget.WWidget;
import net.minecraft.network.chat.Component;

public class HelpPageText extends HelpPageComponent{

    private final Component item;
    private final int height;

    public HelpPageText(Component text, int height){
        item = text;
        this.height = height;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public WWidget getItem() {
        return new WText(item);
    }
}
