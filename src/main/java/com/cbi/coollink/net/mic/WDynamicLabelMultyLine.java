package com.cbi.coollink.net.mic;

import io.github.cottonmc.cotton.gui.client.ScreenDrawing;
import io.github.cottonmc.cotton.gui.impl.client.TextAlignment;
import io.github.cottonmc.cotton.gui.widget.WDynamicLabel;
import net.minecraft.client.gui.DrawContext;

import java.util.function.Supplier;

public class WDynamicLabelMultyLine extends WDynamicLabel {
    public WDynamicLabelMultyLine(Supplier<String> text) {
        super(text, -12566464);
    }

    @Override
    public void paint(DrawContext context, int x, int y, int mouseX, int mouseY) {
        int yOffset = TextAlignment.getTextOffsetY(this.verticalAlignment, this.height, 1);
        String tr = this.text.get();
        String[] lines = tr.split("\n");
        for(int i=0;i<lines.length;i++) {
            if (this.getDrawShadows()) {
                ScreenDrawing.drawStringWithShadow(context, lines[i], this.horizontalAlignment, x, y + yOffset+  i * 9, this.getWidth(), this.shouldRenderInDarkMode() ? this.darkmodeColor : this.color);
            } else {
                ScreenDrawing.drawString(context, lines[i], this.horizontalAlignment, x, y + yOffset + i * 9, this.getWidth(), this.shouldRenderInDarkMode() ? this.darkmodeColor : this.color);
            }
        }

    }

    public void calculateHeight(){
        String tr = this.text.get();
        String[] lines = tr.split("\n");
        height = Math.max(20,9*lines.length+4);
    }
}
