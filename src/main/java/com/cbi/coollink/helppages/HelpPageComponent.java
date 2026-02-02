package com.cbi.coollink.helppages;

import io.github.cottonmc.cotton.gui.widget.WWidget;

public abstract class HelpPageComponent {
    public int getWidth(int maxWidth){
        return maxWidth;
    }
    public abstract int getHeight();
    public abstract WWidget getItem();
}
