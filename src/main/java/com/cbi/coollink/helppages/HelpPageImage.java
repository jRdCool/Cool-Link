package com.cbi.coollink.helppages;

import io.github.cottonmc.cotton.gui.widget.WSprite;
import io.github.cottonmc.cotton.gui.widget.WWidget;
import net.minecraft.util.Identifier;

public class HelpPageImage extends HelpPageComponent{

    private final Identifier image;
    private final int width;
    private final int height;

    public HelpPageImage(Identifier image, int with,int height){
        this.height = height;
        this.width = with;
        this.image = image;
    }
    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public int getWidth(int maxWidth) {
        return Math.min(width,maxWidth);
    }

    @Override
    public WWidget getItem() {
        return new WSprite(image);
    }
}
