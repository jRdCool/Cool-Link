package com.cbi.coollink.terminal;

import com.cbi.coollink.Main;
import com.cbi.coollink.net.mic.WDynamicLabelMultyLine;
import io.github.cottonmc.cotton.gui.widget.WDynamicLabel;
import io.github.cottonmc.cotton.gui.widget.WScrollPanel;
import io.github.cottonmc.cotton.gui.widget.WWidget;
import net.fabricmc.fabric.api.util.TriState;
import net.minecraft.client.MinecraftClient;

import java.util.ArrayList;

public class CommandTextOutputArea {

    private final WScrollPanel scrollPanel;
    private final ArrayList<String> textContent = new ArrayList<>();
    private final int maxTextWidth;
    private final int maxLines;
    private final WDynamicLabelMultyLine text;


    public CommandTextOutputArea(int width, int height, int maxLines, String initialMessage){
        text = new WDynamicLabelMultyLine(() -> String.join("\n", textContent));
        text.setColor(0xFF_FFFFFF,0xFF_FFFFFF);
        scrollPanel = new WScrollPanel(text);
        scrollPanel.setSize(width,height);
        scrollPanel.setScrollingHorizontally(TriState.FALSE);
        //scrollPanel.setScrollingVertically(TriState.TRUE);
        text.setSize(width,9*maxLines);

        maxTextWidth = width - 5;
        this.maxLines = maxLines;


        //THIS LINE MUST BE LAST
        addLine(initialMessage);

    }

    public WWidget getWidget(){
        return scrollPanel;
    }

    public void addLine(String line){

        int initalLineWidth = MinecraftClient.getInstance().textRenderer.getWidth(line);

        if(initalLineWidth < maxTextWidth) {//if the whole thing fits on a single line then just add it to the list
            textContent.add(line);
        }else {

            //otherwise, we need to cur it down to size
            while (MinecraftClient.getInstance().textRenderer.getWidth(line) > maxTextWidth) {
                String cutLine = MinecraftClient.getInstance().textRenderer.trimToWidth(line, maxTextWidth);//get a string cut to the exact max length
                line = line.substring(cutLine.length());//remove that part from the remaining string
                textContent.add(cutLine);//add the section we cut to the displayed lines
            }
            if (!line.isEmpty()) {//if there is anything left in the line
                textContent.add(line);//add it
            }
        }
        //limit the max number of lines
        while(textContent.size() > maxLines){
            textContent.removeFirst();
        }
        text.calculateHeight();

        boolean atBottom = scrollPanel.getVerticalScrollBar().getValue() == scrollPanel.getVerticalScrollBar().getMaxScrollValue();
        scrollPanel.layout();
        if(atBottom){
            scrollPanel.getVerticalScrollBar().setValue(scrollPanel.getVerticalScrollBar().getMaxScrollValue());
        }

    }
}
