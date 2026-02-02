package com.cbi.coollink.helppages;

import io.github.cottonmc.cotton.gui.widget.WWidget;

public abstract class HelpPageComponent {
    int getWidth(int maxWidth){
        return maxWidth;
    }
    abstract int getHeight();
    abstract WWidget getItem();
}
